package com.monolito.japad;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainFrame() {
		StringBuilder sb = new StringBuilder();
		sb.append("public class Main {\n");
		sb.append("\tpublic static void main() {\n");
		sb.append("\t}\n}\n");

		JPanel cp = new JPanel(new BorderLayout());
		
		final RSyntaxTextArea textArea = new RSyntaxTextArea(30, 100);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		textArea.setCodeFoldingEnabled(true);
		textArea.setAntiAliasingEnabled(true);
		textArea.setText(sb.toString());
		textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK), "compile");
		textArea.getActionMap().put("compile", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				try {
					String compileOutput = new DynamicCompiler().compile("Main", textArea.getText());
					System.out.println(compileOutput);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		RTextScrollPane sp = new RTextScrollPane(textArea);
		sp.setFoldIndicatorEnabled(true);
		cp.add(sp, BorderLayout.CENTER);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("The Java Series");
        JTree tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        //tree.addTreeSelectionListener(...);

        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;
 
        category = new DefaultMutableTreeNode("Obj");
        top.add(category);

        book = new DefaultMutableTreeNode("prop");
        category.add(book);

        JScrollPane treeView = new JScrollPane(tree);
		cp.add(treeView, BorderLayout.EAST);
		
		final JTextArea output = new JTextArea(10, 100);
		JScrollPane outputSp = new JScrollPane(output);
		cp.add(outputSp, BorderLayout.PAGE_END);

		PrintStream out = new PrintStream( new TextAreaOutputStream(output));
		System.setOut(out);
		System.setErr(out);

		setContentPane(cp);
		setTitle("JaPad");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}
}
