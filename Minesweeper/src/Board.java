import sun.nio.cs.Surrogate.Generator;

import java.util.Queue;
import java.util.LinkedList;

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
<<<<<<< HEAD
	public void leftClick(int x, int y) {
		//System.out.println("Clicked: (" + x + ", " + y + ")");
		hidden[y][x] = false;
		
		if (getSpace(x, y) == -1) {
			gameOver();	
		} else {
			uncoverCluster(x, y);
		}
	}
	
	public void rightClick(int x, int y) {
		//add a flag to this space
=======
	public void click(int x, int y) {
>>>>>>> 62afe9b1590f70b152b6908aa670748bf8dc39d4
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
	 * Designate a bomb at these coordinates
	 * 
	 * @param x
	 * @param y
	 * 			
	 */
	public void setBomb(int x, int y){
		spaces[y][x] = -1;
	}
	
	
	/**
	 * Call after setting all bombs. Will populate non-bomb spaces with adjacency values.
	 */
	public void setAdjNums(){
		for (int i = 0; i < spaces.length; i++) {
			for (int j = 0; j < spaces[i].length; j++) {
				if (spaces[j][i] != -1) {
					int count = 0;
					try{
						if (spaces[j-1][i-1] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					try{
						if (spaces[j][i-1] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					try{
						if (spaces[j+1][i-1] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					try{
						if (spaces[j-1][i] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					try{
						if (spaces[j+1][i] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					try{
						if (spaces[j-1][i+1] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					try{
						if (spaces[j][i+1] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					try{
						if (spaces[j+1][i+1] == -1) { count++; }
					} catch(IndexOutOfBoundsException e) { /*ignore*/ }
					
					spaces[j][i] = count;
				}
			}
		}
	}
	
	/**
	 * Based on this article:
	 * http://www.techuser.net/minecascade.html
	 * 
	 */
	private void uncoverCluster(int x, int y) {
		if (spaces[y][x] > 0) {return;}
		
		Queue<int[]> q = new LinkedList<int[]>();
		int[] point = {x,y};
		q.add(point);
		
		while (!q.isEmpty()) {
			int cur[] = q.poll();
			int adj[][] = getAdjacent(cur[0],cur[1]);
			boolean foundAdjacentMine = false;
			for (int i = 0; i < 8; i++) {
				try {
					if (spaces[adj[i][0]][adj[i][1]] == -1){
						foundAdjacentMine = true;
					}
				} catch(IndexOutOfBoundsException e) { /*ignore*/ }
			}
			if (!foundAdjacentMine) {
				System.out.println("in the thing");
				for (int i = 0; i < 8; i++) {
					try {
						hidden[adj[i][0]][adj[i][1]] = false;
						q.add(adj[i]);
					} catch(IndexOutOfBoundsException e) { /*ignore */ }
				}
			}
		}
		
	}
	
	private int[][] getAdjacent(int i, int j) {
		
		int adj[][] = new int[8][2];
		adj[0][0] = j-1;
		adj[0][1] = i-1;
		adj[1][0] = j;
		adj[1][1] = i-1;
		adj[2][0] = j+1;
		adj[2][1] = i-1;
		adj[3][0] = j-1;
		adj[3][1] = i;
		adj[4][0] = j+1;
		adj[4][1] = i;
		adj[5][0] = j-1;
		adj[5][1] = i+1;
		adj[6][0] = j;
		adj[6][1] = i+1;
		adj[7][0] = j+1;
		adj[7][1] = i+1;
		
		return adj;
	}
	
	/**
	 * set up a random board
	 * @param numMines - the number of mines to lay randomly
	 */
	public void setupBoardRandom(int numMines) {
		for (int i = 0; i < numMines; i++) {
			int randomX = (int)(Math.random() * (spaces.length));
			int randomY = (int)(Math.random() * (spaces.length));
			setBomb(randomX, randomY);
		}
		setAdjNums();
		
	}
	
	
	/**
	 * Occurs when a bomb is clicked on.
	 */
	private void gameOver() {
		//TODO: failed game condition
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
	 * this is for testing purposes, and making sure that boards are generating properly.
	 */
	public void unhideAll() {
		for (int i = 0; i < hidden.length; i++) {
			for (int j = 0; j < hidden[i].length; j++) {
				hidden[i][j] = false;
			}
		}
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
