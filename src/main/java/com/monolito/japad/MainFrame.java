package com.monolito.japad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
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
	private static final Color BACKGROUND = new Color(0x29, 0x31, 0x34);
	private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

	/**
	 * 
	 */
	public MainFrame() {
		StringBuilder sb = new StringBuilder();
		sb.append("//test\n");
		sb.append("import com.monolito.japad.App;\n\n");
		sb.append("public class Main {\n");
		sb.append("\tpublic static void main() {\n");
		sb.append("\t}\n}\n");

		JPanel cp = new JPanel(new BorderLayout());

		this.editor = new RSyntaxTextArea(30, 100);
		this.editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
		this.editor.setCodeFoldingEnabled(true);
		this.editor.setAntiAliasingEnabled(true);
		this.editor.setFont(MainFrame.FONT);
		InputStream in = getClass().getResourceAsStream("/dark.xml");

		try {
			Theme theme = Theme.load(in);
			theme.apply(this.editor);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		this.editor.setText(sb.toString());
		this.editor.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "compile");
		this.editor.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK),
				"save");
		this.editor.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK),
				"load");

		RTextScrollPane sp = new RTextScrollPane(this.editor);
		sp.setFoldIndicatorEnabled(true);
		// cp.add(sp, BorderLayout.CENTER);

		this.top = new DefaultMutableTreeNode();
		DefaultTreeModel model = new DefaultTreeModel(this.top, true);
		this.tree = new JTree(model);
		this.tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.tree.setBackground(MainFrame.BACKGROUND);

		JScrollPane treeView = new JScrollPane(this.tree);
		treeView.setPreferredSize(new Dimension(100, 300));
		// cp.add(treeView, BorderLayout.EAST);

		final JTextArea output = new JTextArea(10, 100);

		output.setFont(MainFrame.FONT);
		output.setBackground(MainFrame.BACKGROUND);
		output.setForeground(Color.WHITE);
		JScrollPane outputSp = new JScrollPane(output);

		JSplitPane topPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp,
				treeView);
		topPane.setBorder(null);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topPane, outputSp);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(500);

		cp.add(splitPane, BorderLayout.CENTER);

		PrintStream out = new PrintStream(new TextAreaOutputStream(output));
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
	 * @param action
	 */
	protected void addActionListener(String action, Action listener) {
		this.editor.getActionMap().put(action, listener);
	}

	/**
	 * 
	 * @param listener
	 */
	protected void addTreeWillExpandListener(TreeWillExpandListener listener) {
		this.tree.addTreeWillExpandListener(listener);
	}

	/**
	 * 
	 * @return
	 */
	protected String getSource() {
		return this.editor.getText();
	}

	/**
	 * 
	 * @param source
	 */
	protected void setSource(String source) {
		this.editor.setText(source);
	}
	
	/**
	 * 
	 * @param parent
	 * @param item
	 */
	public void addItem(DefaultMutableTreeNode parent, Object item) {
		if (parent != null) {
			DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
			model.insertNodeInto(new DefaultMutableTreeNode(item, true),
					parent, parent.getChildCount());
		} else {
			DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
			model.insertNodeInto(new DefaultMutableTreeNode(item, true),
					this.top, this.top.getChildCount());
		}
	}

	/**
	 * 
	 */
	public void clearItems() {
		this.top.removeAllChildren();
		((DefaultTreeModel) this.tree.getModel()).reload();
	}
}