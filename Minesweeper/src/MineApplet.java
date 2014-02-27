import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class MineApplet extends Applet implements MouseListener {

	// Graphics objects
	private Graphics bufferGraphics;
	private Image offscreen;
	private int scale;
	private char[][] marks;
	private boolean clickable;

	private MainGUI mainGUI;

	private Board board;
	/**
	 * Primary constructor
	 * 
	 * @param board
	 *            The board to be played on this applet
	 */
	public MineApplet(Board board, int scale, MainGUI mainGUI) {
		this.mainGUI = mainGUI;
		newBoard(board);
		this.scale = scale;
		init();
		repaint();
		setClickable(false);
	}
	
	/**
	 * Starts a new 1 player game
	 */
	public void newGame() {
		mainGUI.newGame();
	}

	/**
	 * Sets a new board
	 * 
	 * @param board
	 *            The new board to be played
	 */
	public void newBoard(Board board) {
		this.board = board;
		// this.board.setupBoardRandom(15); // TEMPORARY
		// this.board.unhideAll(); //TEMPORARY
		marks = new char[board.getHeight()][board.getWidth()];
		
	}

	/**
	 * Initializes the applet properties
	 */
	public void init() {
		if (board == null) {
			return;
		}
		setPreferredSize(new Dimension(board.getWidth() * scale + 1,
				board.getHeight() * scale + 1));
		addMouseListener(this);
	}

	/**
	 * Initializes Graphics objects and properties
	 */
	public void initGraphics() {
		try {
			offscreen = createImage(board.getWidth() * scale, board.getHeight()
					* scale);
			bufferGraphics = offscreen.getGraphics();
			updateImage();
			repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the images that is to be displayed. This is the main drawing
	 * method
	 */
	public void updateImage() {
		// Paint the spaces
		for (int i = 0; i < board.getHeight(); i++) {
			for (int j = 0; j < board.getWidth(); j++) {
				if (board.isHidden(j, i)) {
					bufferGraphics.setColor(Color.BLUE);
					bufferGraphics.fillRect(j * scale, i * scale, scale, scale);
					if (marks[i][j] == 'q') { // question mark
						bufferGraphics.setColor(Color.WHITE);
						bufferGraphics.drawString("?",
								(int) (j * scale + scale / 2.5), (int) (i
										* scale + scale / 1.5));
					} else if (marks[i][j] == 'f') { // flag
						bufferGraphics.setColor(Color.WHITE);
						bufferGraphics.drawString("!",
								(int) (j * scale + scale / 2),
								(int) (i * scale + scale / 1.5));
					}
				} else if (board.getSpace(j, i) == -1) { // uncovered bomb
					bufferGraphics.setColor(Color.RED);
					bufferGraphics.fillRect(j * scale, i * scale, scale, scale);
				} else {
					bufferGraphics.setColor(Color.GRAY); // uncovered non bomb
					bufferGraphics.fillRect(j * scale, i * scale, scale, scale);
					if (board.getSpace(j, i) > 0) {
						bufferGraphics.setColor(Color.YELLOW);
						bufferGraphics.drawString(board.getSpace(j, i) + "",
								(int) (j * scale + scale / 2.5), (int) (i
										* scale + scale / 1.5));
					}
				}
			}
		}

		bufferGraphics.setColor(Color.BLACK);
		// Paint the gridlines
		for (int i = 0; i < board.getHeight(); i++) {
			bufferGraphics.drawLine(0, i * scale, board.getWidth() * scale, i
					* scale);
		}
		for (int i = 0; i < board.getWidth(); i++) {
			bufferGraphics.drawLine(i * scale, 0, i * scale, board.getHeight()
					* scale);
		}
		bufferGraphics.drawLine(0, board.getHeight() * scale - 1,
				board.getWidth() * scale - 1, board.getHeight() * scale - 1);
		bufferGraphics.drawLine(board.getWidth() * scale - 1, 0,
				board.getWidth() * scale - 1, board.getHeight() * scale - 1);
	}

	/**
	 * Draws the offscreen object to the applet
	 */
	public void paint(Graphics g) {
		g.drawImage(offscreen, 0, 0, this);
	}

	/**
	 * Simply calls the paint method
	 */
	public void update(Graphics g) {
		paint(g);
	}

	/**
	 * Sets the applet to be clickable or not
	 * 
	 * @param clickable
	 */
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	/**
	 * Tells if the applet is clickable
	 * 
	 * @return If the applet responds to click or not
	 */
	public boolean isClickable() {
		return clickable;
	}

	/**
	 * Sends a click to the board. Used for multiplayer
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param left
	 *            True for left click, false for right
	 */
	public void clicked(int x, int y, boolean left) {
		if (left) {
			if (board.isHidden(x, y)) {
				board.leftClick(x, y);
				updateImage();
				repaint();
			}
		} else {
			// Right Click. Rotate mark
			if (marks[y][x] == 'q') {
				marks[y][x] = 'e'; // question -> empty
			} else if (marks[y][x] == 'f') {
				marks[y][x] = 'q'; // flag -> question
			} else {
				marks[y][x] = 'f'; // empty -> flag
			}
			updateImage();
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		int x = arg0.getX() / scale;
		int y = arg0.getY() / scale;

		if (isClickable()) {
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				clicked(x, y, true);
				// Send click to server
				try {
					if (mainGUI.getClient() != null) {
						if (mainGUI.getClient().isConnectedToPlayer()) {
							mainGUI.getClient().client
									.sendToServer("gamedata click " + x + " "
											+ y);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else if (arg0.getButton() == MouseEvent.BUTTON3) {
				clicked(x, y, false);
				try {
					if (mainGUI.getClient() != null) {
						if (mainGUI.getClient().isConnectedToPlayer()) {
							mainGUI.getClient().client
									.sendToServer("gamedata rightclick " + x
											+ " " + y);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

}
