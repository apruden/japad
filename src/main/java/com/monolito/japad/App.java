package com.monolito.japad;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.SwingUtilities;

/**
 * 
 * @author alex
 * 
 */
public class App {
	static MainFrame view = new MainFrame();
	static SketchModel model = new SketchModel();

	/**
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		createTables();

		@SuppressWarnings("unused")
		MainController controller = new MainController(view, model);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				view.setVisible(true);
			}
		});
	}

	/**
	 * 
	 */
	private static void createTables() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		String createString = "create table " + "japad.ENTRIES "
				+ "(ID varchar(100) NOT NULL, " + "CODE long varchar, "
				+ "PRIMARY KEY (ID))";

		String dbURL = "jdbc:derby:data/history;create=true";
		try (Connection conn = DriverManager.getConnection(dbURL)) {
			try (Statement stmt = conn.createStatement()) {
				stmt.executeUpdate(createString);
			} catch (Exception e) {
				System.out.println("Error " + e.getMessage());
			}
		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param obj
	 */
	public static void show(Object obj) {
		model.addWatch(obj);

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

			System.out.format("%s %s: <%s> %s", f.getType(), f.getName(),
					value.getClass(), value);
		}
	}

	/**
	 * 
	 * @param obj
	 */
	public static void dump(Object obj) {
		System.out.println(obj.toString());
	}
}
