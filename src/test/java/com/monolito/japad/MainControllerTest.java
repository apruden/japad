package com.monolito.japad;

import javax.swing.Action;
import javax.swing.event.TreeWillExpandListener;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Before;
import org.junit.Test;

public final class MainControllerTest {

	private MainController controller;

	@Before
	public void before() {
		MainFrame view  = new MockUp<MainFrame> () {
			@Mock
			void addActionListener(String action, Action listener) {
			}
			
			@Mock
			void addTreeWillExpandListener(TreeWillExpandListener listener) {
			}
			
			@Mock
			void clearItems() {
			}
			
			@Mock
			String getSource() {
				return "public class Main { public static void main() {} }";
			}
		}.getMockInstance();
		
		controller = new MainController(view, new SketchModel());
	}
	
	@Test
	public void doCompile() {
		controller.onCompile();
	}
}