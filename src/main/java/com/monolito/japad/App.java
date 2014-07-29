package com.monolito.japad;

import java.lang.reflect.Field;

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
	 * @param obj
	 */
	public static void show(Object obj) {
		model.addWatch(obj);

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

			System.out.format("%s %s: <%s> %s", f.getType(), f.getName(), value.getClass(), value);
		}
	}
}
