import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
	private JMenuItem newGame;
	private JMenuItem createBoard;
	private JMenuItem connectToPlayer;
	
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
		
		// Menu Stuff
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		newGame = new JMenuItem("New Game");
		createBoard = new JMenuItem("Create Board");
		connectToPlayer = new JMenuItem("Connect to Player");
		menu.add(newGame);
		menu.add(createBoard);
		menu.add(connectToPlayer);
		menuBar.add(menu);
		connectToPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connectToPlayer();
			}
		});
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Board board = new Board();
				mineApplet.newBoard(board);
				board.setMyapplet(mineApplet);
				mineApplet.updateImage();
				mineApplet.repaint();
			}
		});
		
		mainPanel = new JPanel();	
		
		// Set up board and applet
		Board board = new Board();
		mineApplet = new MineApplet(board, 25);
		board.setMyapplet(mineApplet);
		mainPanel.add(mineApplet);
		
		add(mainPanel, BorderLayout.CENTER);
		setJMenuBar(menuBar);
		
		setTitle("Competitive Minesweeper");
		pack();
		setVisible(true);		
		
		mineApplet.initGraphics();
	}
	
	/**
	 * Opens the dialog to connect to another player
	 */
	public void connectToPlayer() {
		System.out.println("Connect to player");
		JFrame connectFrame = new JFrame("Connect to player");
		JPanel connectPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JButton refreshButton = new JButton("Refresh list");
		JButton connectButton = new JButton("Connect");
		
		connectFrame.setLayout(new BorderLayout(3, 3));
		
		connectPanel.setPreferredSize(new Dimension(800, 500));
		
		buttonPanel.add(connectButton);
		buttonPanel.add(refreshButton);
		buttonPanel.setPreferredSize(new Dimension(800, 50));
		
		connectFrame.add(connectPanel, BorderLayout.CENTER);
		connectFrame.add(buttonPanel, BorderLayout.SOUTH);
		connectFrame.pack();
		connectFrame.setVisible(true);
	}
	
	/**
	 * Runs the GUI.  Useful for JAR packaging
	 * @param args
	 */
	public static void main(String [] args) {
		MainGUI m = new MainGUI();
	}

}
