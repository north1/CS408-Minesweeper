public class Board {

	// 2D Arrays for the grid
	private int[][] spaces;
	private boolean[][] hidden;

	private int width;
	private int height;

	/**
	 * Default constructor
	 */
	public Board() {
		// TODO Make adjustments for different sizes
		width = 9;
		height = 9;
		spaces = new int[height][width];
		hidden = new boolean[height][width];
		for (int i = 0; i < hidden.length; i++) {
			for (int j = 0; j < hidden[i].length; j++) {
				hidden[i][j] = true;
			}
		}
	}
	
	/**
	 * Updates the board when a click is received
	 * @param x The X coordinate of the clicked space
	 * @param y The Y coordinate of the clicked space
	 */
	public void click(int x, int y) {
	}

	/**
	 * Gives the value for a specific space on the board
	 * 
	 * @param x
	 *            The X coordinate
	 * @param y
	 *            The Y coordinate
	 * @return -1 for bomb, 0 for clear, >0 for the number of adjacent bombs
	 */
	public int getSpace(int x, int y) {
		return spaces[y][x];
	}

	/**
	 * Tells if a space is hidden or not
	 * 
	 * @param x
	 *            The X coordinate
	 * @param y
	 *            The Y coordinate
	 * @return true if hidden, false if already revealed
	 */
	public boolean isHidden(int x, int y) {
		return hidden[y][x];
	}

	/**
	 * The Board's height
	 * 
	 * @return The height of the board in spaces
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * The Board's width
	 * 
	 * @return The width of the board in spaces
	 */
	public int getWidth() {
		return width;
	}

}
