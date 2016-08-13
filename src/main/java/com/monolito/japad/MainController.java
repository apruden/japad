package com.monolito.japad;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class MainController implements PropertyChangeListener {

	private final MainFrame view;
	private final SketchModel model;

	/**
	 * 
	 * @param mainFrame
	 * @param sketchModel
	 */
	public MainController(MainFrame view, SketchModel model) {
		this.view = view;
		this.model = model;

		this.view.addActionListener("compile", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				onCompile();
			}
		});

		this.view.addActionListener("save", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});

		this.view.addActionListener("load", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onLoad();
			}
		});

		this.view.addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent event)
					throws ExpandVetoException {
				onTreeWillExpand((DefaultMutableTreeNode) event.getPath()
						.getLastPathComponent());
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event)
					throws ExpandVetoException {
				return;
			}
		});

		this.model.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("watches")) {
			@SuppressWarnings("unchecked")
			ArrayList<Object> val = (ArrayList<Object>) evt.getNewValue();

			if (!val.isEmpty()) {
				this.view.addItem(null, val.get(val.size() - 1));
			} else {
				this.view.clearItems();
			}
		}
	}

	/**
	 * 
	 */
	protected void onTreeWillExpand(DefaultMutableTreeNode node) {
		Object obj = node.getUserObject();

		if (obj == null) { // top
			return;
		}

		for (Field f : obj.getClass().getDeclaredFields()) {
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

			this.view.addItem(node, value);
		}
	}

	/**
	 * 
	 */
	protected void onSave() {
		String dbURL = "jdbc:derby:data/history;";
		String id = this.view.getSource().split("\n")[0].replace("/", "");

		try(Connection conn = DriverManager.getConnection(dbURL)){
			if (conn != null) {
				PreparedStatement selstm = conn.prepareStatement("select count(*) as rowcount from japad.ENTRIES where id = ?");
				selstm.setString(1, id);
				ResultSet rs = selstm.executeQuery();
				rs.next();
				int count = rs.getInt("rowcount") ;
				rs.close() ;
				if(count == 0) {
					PreparedStatement stm = conn.prepareStatement("insert into japad.ENTRIES values (?, ?)");
					stm.setString(1, id);
					stm.setString(2, this.view.getSource());
					stm.executeUpdate();
					System.out.format("saved: %s", id);
				} else {
					PreparedStatement stm = conn.prepareStatement("update japad.ENTRIES set code = ? where id = ?");
					stm.setString(1, this.view.getSource());
					stm.setString(2, id);
					stm.executeUpdate();
					System.out.format("updated: %s", id);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 */
	protected void onLoad() {
		String dbURL = "jdbc:derby:data/history;";
		String id = this.view.getSource().split("\n")[0].replace("/", "");

		try(Connection conn = DriverManager.getConnection(dbURL)){
			if (conn != null) {
				PreparedStatement stm = conn.prepareStatement("select CODE from japad.ENTRIES where id = ?");
				stm.setString(1, id);
				ResultSet rs = stm.executeQuery();

				if(rs.next()) {
					System.out.println("found");
					this.view.setSource(rs.getString(1));
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	protected void onCompile() {
		@SuppressWarnings("unused")
		String compileOutput = "";
		this.model.clearWatches();

		try {
			compileOutput = new DynamicCompiler().compile("Main",
					this.view.getSource());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("Compile DONE");
		}
	}
}