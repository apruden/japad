package com.monolito.japad;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

/**
 * 
 * @author alex
 *
 */
public class MainController implements PropertyChangeListener{
	
	private final MainFrame view;
	private final SketchModel model;
	
	/**
	 * 
	 * @param mainFrame
	 * @param sketchModel
	 */
	public MainController(MainFrame mainFrame, SketchModel sketchModel) {
		this.view = mainFrame;
		this.model = sketchModel;
		
		this.view.getEditor().getActionMap().put("compile", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				String compileOutput = "";
				model.clearWatches();
				
				try {
					compileOutput = new DynamicCompiler().compile("Main", view.getEditor().getText());
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					System.out.println("Compile DONE");
				}
			}
		});
		
		this.view.getEditor().getActionMap().put("save", new AbstractAction() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
		            String dbURL = "jdbc:derby:data/history;create=true";
		            Connection conn = DriverManager.getConnection(dbURL);

		            if (conn != null) {
		                System.out.println("Connected to database #1");
		            }
		        } catch (SQLException ex) {
		            ex.printStackTrace();
		        }
			}
		});
		
		this.view.getTreeView().addTreeWillExpandListener(new TreeWillExpandListener() {
			
			@Override
			public void treeWillExpand(TreeExpansionEvent event)
					throws ExpandVetoException {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
				Object obj = node.getUserObject();
				
				if (obj == null) { //top
					return;
				}

				for(Field f:obj.getClass().getDeclaredFields()) {
					f.setAccessible(true);
					Object value = null;

					if (obj != null) {
						try {
							value = f.get(obj);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}

					//System.out.format("%s %s: <%s> %s", f.getType(), f.getName(), value.getClass(), value);
					view.addItem((DefaultMutableTreeNode)event.getPath().getLastPathComponent(), value);
				}
			}
			
			@Override
			public void treeWillCollapse(TreeExpansionEvent event)
					throws ExpandVetoException {
				//do nothing
			}
		});
		
		this.model.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("watches")) {
			@SuppressWarnings("unchecked")
			ArrayList<Object> val = (ArrayList<Object>)evt.getNewValue();

			if (!val.isEmpty()) {
				this.view.addItem(null, val.get(val.size() - 1));
			} else {
				this.view.clearItems();
			}
		}
	}
}
