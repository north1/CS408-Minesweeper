import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MineApplet extends Applet implements MouseListener {

	// Graphics objects
	private Graphics bufferGraphics;
	private Image offscreen;
	private int scale;

	private Board board;

	/**
	 * Primary constructor
	 * 
	 * @param board
	 *            The board to be played on this applet
	 */
	public MineApplet(Board board, int scale) {
		System.out.println("Constructor");
		newBoard(board);
		this.scale = scale;
		init();
		repaint();
	}

	/**
	 * Sets a new board
	 * 
	 * @param board
	 *            The new board to be played
	 */
	public void newBoard(Board board) {
		this.board = board;
		this.board.setupBoardRandom(15); //TEMPORARY
		//this.board.unhideAll(); //TEMPORARY
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
<<<<<<< HEAD
		//System.out.println("updateImage");
=======
>>>>>>> 62afe9b1590f70b152b6908aa670748bf8dc39d4
		// Paint the spaces
		for(int i = 0; i < board.getHeight(); i++) {
			for(int j = 0; j < board.getWidth(); j++) {
				if(board.isHidden(j, i)) {
					bufferGraphics.setColor(Color.BLUE);
				} else if (board.getSpace(j, i) == -1){
					bufferGraphics.setColor(Color.RED);
				} else {
					bufferGraphics.setColor(Color.YELLOW);
					//how the fuck do I position these
					bufferGraphics.drawChars((""+board.getSpace(j, i)).toCharArray(), 0, 1, j*scale, i*scale);
					bufferGraphics.setColor(Color.GRAY);
				}
				bufferGraphics.fillRect(j * scale, i * scale, scale, scale);
			}
		}
		
		bufferGraphics.setColor(Color.BLACK);
		// Paint the gridlines
		for(int i = 0; i < board.getHeight(); i++) {
			bufferGraphics.drawLine(0, i * scale, board.getWidth() * scale, i * scale);
		}
		for(int i = 0; i < board.getWidth(); i++) {
			bufferGraphics.drawLine(i * scale, 0, i * scale, board.getHeight() * scale);
		}
		bufferGraphics.drawLine(0, board.getHeight() * scale - 1, board.getWidth() * scale - 1, board.getHeight() * scale - 1);
		bufferGraphics.drawLine(board.getWidth() * scale - 1, 0, board.getWidth() * scale - 1, board.getHeight() * scale - 1);
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
	

	@Override
	public void mousePressed(MouseEvent arg0) {
		int x = arg0.getX() / scale;
		int y = arg0.getY() / scale;
		
		if(arg0.getButton() == MouseEvent.BUTTON1) {
			// Left Click.  Reveal space
			if(board.isHidden(x, y)) {
				board.leftClick(x, y);
				updateImage();
				repaint();
			}
		} else if (arg0.getButton() == MouseEvent.BUTTON2) {
			// Right Click.  Rotate mark
			//TODO Add Flags and Question Marks
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
