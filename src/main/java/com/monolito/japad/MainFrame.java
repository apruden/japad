package com.monolito.japad;

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 * 
 * @author alex
 *
 */
public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final RSyntaxTextArea editor;
	private final JTree tree;
	private final DefaultMutableTreeNode top;

	/**
	 * 
	 */
	public MainFrame() {
		StringBuilder sb = new StringBuilder();
		sb.append("import com.monolito.japad.App;\n\n");
		sb.append("public class Main {\n");
		sb.append("\tpublic static void main() {\n");
		sb.append("\t}\n}\n");

		JPanel cp = new JPanel(new BorderLayout());

		this.editor = new RSyntaxTextArea(30, 100);
		this.editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		this.editor.setCodeFoldingEnabled(true);
		this.editor.setAntiAliasingEnabled(true);
		this.editor.setText(sb.toString());
		this.editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK), "compile");

		RTextScrollPane sp = new RTextScrollPane(this.editor);
		sp.setFoldIndicatorEnabled(true);
		cp.add(sp, BorderLayout.CENTER);

		this.top = new DefaultMutableTreeNode("watches");
		DefaultTreeModel model = new DefaultTreeModel(this.top, true);
        this.tree = new JTree(model);
        this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        JScrollPane treeView = new JScrollPane(this.tree);
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
	
	/**
	 * 
	 * @return
	 */
	public JTextComponent getEditor() {
		return this.editor;
	}
	
	/**
	 * 
	 * @return
	 */
	public JTree getTreeView() {
		return this.tree;
	}
	
	/**
	 * 
	 * @param parent
	 * @param item
	 */
	public void addItem(DefaultMutableTreeNode parent, Object item) {
		if (parent != null) {
			DefaultTreeModel model = (DefaultTreeModel)this.tree.getModel();
			model.insertNodeInto(new DefaultMutableTreeNode(item, true), parent, parent.getChildCount());
		} else {
			DefaultTreeModel model = (DefaultTreeModel)this.tree.getModel();
			model.insertNodeInto(new DefaultMutableTreeNode(item, true), this.top, this.top.getChildCount());
		}
	}
}