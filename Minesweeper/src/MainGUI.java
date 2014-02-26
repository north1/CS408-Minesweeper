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
	private JPanel secondPanel;
	
	// Menus
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem newGame;
	private JMenuItem createBoard;
	private JMenuItem connectToPlayer;
	
	private ClientMain clientMain;
	
	// Applets 
	private static MineApplet mineApplet;
	private static MineApplet secondApplet;
	
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
		mineApplet.setClickable(true);
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
	 * Initializes a second player's board
	 * @param board The underlying board.
	 */
	public void initSecondPlayer(Board board) {
		secondPanel = new JPanel();
		
		secondApplet = new MineApplet(board, 25);
		secondApplet.setClickable(false);
		board.setMyapplet(secondApplet);
		secondPanel.add(secondApplet);
		
		add(secondPanel, BorderLayout.CENTER);
		
		secondApplet.initGraphics();
	}
	
	/**
	 * Sends a click to the applet from the second player
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public void secondClick(int x, int y) {
		if(secondApplet != null) {
			secondApplet.sendClick(x, y);
		}
	}
	
	/**
	 * Opens the dialog to connect to another player
	 */
	public void connectToPlayer() {
		clientMain = new ClientMain("127.0.0.1", 8043, this);
	}
	
	/**
	 * Runs the GUI.  Useful for JAR packaging
	 * @param args
	 */
	public static void main(String [] args) {
		MainGUI m = new MainGUI();
	}

}
