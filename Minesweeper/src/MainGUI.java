import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class MainGUI extends JFrame {
	
	private static final long serialVersionUID = 5009798814042290230L;
	
	// Panels
	private JPanel mainPanel;
	
	// Menus
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	
	// Applets 
	private static MineApplet mineApplet;
	/**
	 * Default constructor for the GUI
	 */
	public MainGUI() {
		initGUI();
	}
	
	/**
	 * Initializes frame, layout, menus, etc.
	 */
	public void initGUI() {
		setLayout(new BorderLayout(3, 3));
		
		mainPanel = new JPanel();	
		
		// Set up board and applet
		Board board = new Board();
		mineApplet = new MineApplet(board, 25);
		mainPanel.add(mineApplet);
		
		add(mainPanel, BorderLayout.CENTER);
		
		setTitle("Competitive Minesweeper");
		pack();
		setVisible(true);		
		
		mineApplet.initGraphics();
	}
	
	/**
	 * Runs the GUI.  Useful for JAR packaging
	 * @param args
	 */
	public static void main(String [] args) {
		MainGUI m = new MainGUI();
	}

}
