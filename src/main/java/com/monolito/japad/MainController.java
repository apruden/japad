package com.monolito.japad;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

public class MainController implements PropertyChangeListener{
	
	private final MainFrame view;
	private final SketchModel model;
	
	public MainController(MainFrame mainFrame, SketchModel model) {
		this.view = mainFrame;
		this.model = model;
		
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
				String compileOutput = "";
				try {
					compileOutput = new DynamicCompiler().compile("Main", view.getEditor().getText());
				} catch (Exception ex) {
					System.out.println(compileOutput);
					ex.printStackTrace();
				} finally {
					System.out.println("Compile DONE");
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
			this.view.addItem(null, val.get(val.size() - 1));
		}
	}
}
