package com.monolito.japad;

import javax.swing.SwingUtilities;

/**
 * 
 * @author alex
 *
 */
public class App {

	/**
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
}
