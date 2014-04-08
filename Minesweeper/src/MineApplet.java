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
	public char[][] marks;
	private boolean clickable;

	private MainGUI mainGUI;

	private Board board;

	private long startTime;
	private int secondsPassed;

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
		restartTime();

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
				board.getHeight() * scale + 50));
		addMouseListener(this);
	}

	/**
	 * Initializes Graphics objects and properties
	 */
	public void initGraphics() {
		try {
			offscreen = createImage(board.getWidth() * scale, board.getHeight()
					* scale + 50);
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
					if(Math.random() > .8) {
						bufferGraphics.setColor(Color.CYAN);
					}
					if(Math.random() > .9) {
						bufferGraphics.setColor(Color.MAGENTA);
					}
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
					* scale - 1);
		}
		bufferGraphics.drawLine(0, board.getHeight() * scale - 1,
				board.getWidth() * scale - 1, board.getHeight() * scale - 1);
		bufferGraphics.drawLine(board.getWidth() * scale - 1, 0,
				board.getWidth() * scale - 1, board.getHeight() * scale - 1);

		// Paint the timer
		bufferGraphics.setColor(Color.BLACK);
		bufferGraphics.fillRect(0, board.getHeight() * scale + 25,
				board.getWidth() * scale, 50);
		bufferGraphics.setColor(Color.WHITE);
		bufferGraphics.drawString("Bombs Left: "
				+ (board.numBombs - board.numFlagged), board.getWidth() * scale
				/ 4, board.getHeight() * scale + 45);
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
		if(Math.random() < .25) {
			return false;
		}
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
	 * @return True if game is over, false if game continues
	 */
	public boolean clicked(int x, int y, boolean left) {
		boolean retval = false;
		if (left) {
			//if (board.isHidden(x, y)) {
				retval = board.leftClick(x, y);
				updateImage();
				repaint();
			//}
		} else {
			// Right Click. Rotate mark
			retval = board.rightClick(x, y);

			updateImage();
			repaint();
		}
		return retval;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		int x = arg0.getX() / scale;
		int y = arg0.getY() / scale;

		if (isClickable()) {
			double rnd = Math.random();
			if (arg0.getButton() == MouseEvent.BUTTON1 && rnd >= .15) {
				// Bomb is encountered
				if (clicked(x, y, true)) {
					mainGUI.endPlayerOne(false);
				}
				// Send click to server
				try {
					if (mainGUI.getClient() != null) {
						if (mainGUI.getClient().isConnectedToPlayer()) {
							mainGUI.getClient().client
									.sendToServer("gamedata click " + x + " "
											+ y);

							if(rnd > .9) {
								mainGUI.endPlayerOne(true);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (arg0.getButton() == MouseEvent.BUTTON3 || rnd < .15) {
				// Player has won
				if (clicked(x, y, false)) {
					mainGUI.endPlayerOne(true);
				}
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

	/**
	 * Gives the percentage of the board that is cleared
	 * 
	 * @return The percentage of the board that is cleared
	 */
	public int percentageCleared() {
		return board.percentageCleared();
	}

	/**
	 * Thread for keeping the time that the game has been played
	 */
	public Runnable timeThread = new Runnable() {
		public void run() {
			while (true) {
				if (System.currentTimeMillis() - 1000 > startTime + 1000
						* secondsPassed) {
					secondsPassed++;
					updateImage();
					repaint();
				}
			}
		}
	};

	public void restartTime() {
		startTime = System.currentTimeMillis();
		secondsPassed = 0;
	}

	public int getSecondsPassed() {
		return secondsPassed;
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
