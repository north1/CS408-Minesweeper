import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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

	// Win/Lose conditions
	public boolean p1Win;
	public boolean p1Lose;
	public boolean p2Win;
	public boolean p2Lose;

	/**
	 * Default constructor for the GUI
	 */
	public MainGUI() {
		initGUI();
		p1Win = false;
		p1Lose = false;
		p2Win = false;
		p2Lose = false;
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
		createBoard.setEnabled(false);
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
				newGame();
			}
		});

		mainPanel = new JPanel();

		// Set up board and applet
		Board board = new Board();
		board.setupBoardRandom(15);
		mineApplet = new MineApplet(board, 25, this);
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
	 * Called when the server sends a new board to play
	 * 
	 * @param board
	 *            The new board to play
	 */
	public void newBoard(Board board) {
		mineApplet.newBoard(board);
		mineApplet.setClickable(true);
		board.setMyapplet(mineApplet);
		mineApplet.updateImage();
		mineApplet.repaint();
	}

	/**
	 * Initializes a second player's board
	 * 
	 * @param board
	 *            The underlying board.
	 */
	public void initSecondPlayer(Board board) {
		secondPanel = new JPanel();

		secondApplet = new MineApplet(board, 25, this);
		secondApplet.setClickable(false);
		board.setMyapplet(secondApplet);
		secondPanel.add(secondApplet);

		add(secondPanel, BorderLayout.EAST);

		secondApplet.initGraphics();

		pack();
	}

	/**
	 * Removes all elements that involve a second player
	 */
	public void removeSecondPlayer() {
		if (secondPanel != null) {
			remove(secondPanel);
			pack();
		}
	}

	/**
	 * Sends a click to the applet from the second player
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 */
	public void secondClick(int x, int y, boolean left) {
		if (secondApplet != null) {
			if (secondApplet.clicked(x, y, left)) {
				if (left) {
					endPlayerTwo(false);
				} else {
					endPlayerTwo(true);
				}
			}
		}
	}

	/**
	 * Opens the dialog to connect to another player
	 */
	public void connectToPlayer() {
		clientMain = new ClientMain("moore06.cs.purdue.edu", 8043, this);
	}

	/**
	 * Starts a new 1 player game
	 */
	public void newGame() {
		Board board = new Board();
		board.setupBoardRandom(15);
		mineApplet.newBoard(board);
		board.setMyapplet(mineApplet);
		mineApplet.updateImage();
		mineApplet.repaint();
		mineApplet.setClickable(true);
		p1Win = false;
		p1Lose = false;
		p2Win = false;
		p2Lose = false;
	}

	/**
	 * Handles actions when player 1 finishes
	 * 
	 * @param win
	 *            True if player 1 won board, false if they lost
	 */
	public void endPlayerOne(boolean win) {
		// Check to see if there is a second player
		if (getClient() != null && getClient().isConnectedToPlayer()) {
			if (win) {
				p1Win = true;
				p1Lose = false;
				if (p2Win) {
					JOptionPane
							.showMessageDialog(
									new javax.swing.JFrame(),
									"You lost!",
									"You cleared the board, but your opponent was faster.",
									JOptionPane.WARNING_MESSAGE);
				} else if (p2Lose) {
					JOptionPane
							.showMessageDialog(
									new javax.swing.JFrame(),
									"You won!",
									"You cleared the board and your opponent failed their board.",
									JOptionPane.WARNING_MESSAGE);
				}
			} else {
				p1Win = false;
				p1Lose = true;
				if (p2Win) {
					JOptionPane
							.showMessageDialog(
									new javax.swing.JFrame(),
									"You failed to clear the board, and your opponent cleared their board.",
									"You lost!", JOptionPane.WARNING_MESSAGE);
				} else if (p2Lose) {
					if (mineApplet.percentageCleared() > secondApplet
							.percentageCleared()) {
						JOptionPane
								.showMessageDialog(
										new javax.swing.JFrame(),
										"You failed to clear the board but you cleared more than your opponent.",
										"You won!", JOptionPane.WARNING_MESSAGE);
					} else if (mineApplet.percentageCleared() < secondApplet
							.percentageCleared()) {
						JOptionPane
								.showMessageDialog(
										new javax.swing.JFrame(),
										"You failed to clear the board and your opponent cleard more than you",
										"You lost!",
										JOptionPane.WARNING_MESSAGE);
					} else {
						JOptionPane
								.showMessageDialog(
										new javax.swing.JFrame(),
										"You cleared the same amount of the board as your opponent.",
										"You tied!",
										JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		} else {
			if (win) {
				JOptionPane.showMessageDialog(new javax.swing.JFrame(),
						"You won!", "You won the game.",
						JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(new javax.swing.JFrame(),
						"Game Over", "You Lost. Play again!",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/**
	 * Handles actions when player 2 finishes
	 * 
	 * @param win
	 *            True if player 2 won board, false if they lost
	 */
	public void endPlayerTwo(boolean win) {
		if (win) { // player 2 cleared the board
			p2Win = true;
			p2Lose = false;
			if (p1Win) {
				JOptionPane.showMessageDialog(new javax.swing.JFrame(),
						"You cleared the board faster than your opponent",
						"You won!", JOptionPane.WARNING_MESSAGE);
			} else if (p1Lose) {
				JOptionPane
						.showMessageDialog(
								new javax.swing.JFrame(),
								"You failed to clear the board, and your opponent cleared their board.",
								"You lost!", JOptionPane.WARNING_MESSAGE);
			}
		} else { // player 2 failed to clear board
			p2Win = false;
			p2Lose = true;
			if (p1Win) {
				JOptionPane
						.showMessageDialog(
								new javax.swing.JFrame(),
								"You cleared the board and your opponent failed to clear their board.",
								"You won!", JOptionPane.WARNING_MESSAGE);
			} else if (p1Lose) {
				if (mineApplet.percentageCleared() > secondApplet
						.percentageCleared()) {
					JOptionPane
							.showMessageDialog(
									new javax.swing.JFrame(),
									"You failed to clear the board but you cleared more than your opponent.",
									"You won!", JOptionPane.WARNING_MESSAGE);
				} else if (mineApplet.percentageCleared() < secondApplet
						.percentageCleared()) {
					JOptionPane
							.showMessageDialog(
									new javax.swing.JFrame(),
									"You failed to clear the board and your opponent cleard more than you",
									"You lost!", JOptionPane.WARNING_MESSAGE);
				} else {
					JOptionPane
							.showMessageDialog(
									new javax.swing.JFrame(),
									"You cleared the same amount of the board as your opponent.",
									"You tied!", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}

	/**
	 * Gets the client that connects to the server
	 * 
	 * @return the ClientMain object
	 */
	public ClientMain getClient() {
		return clientMain;
	}

	/**
	 * Runs the GUI. Useful for JAR packaging
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		MainGUI m = new MainGUI();
	}

}
