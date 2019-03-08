
public class Board {
	int[][] board;
	//	static final int EMPTY = 0,       // Represents an empty square.
	//			WHITE = 1,      		 // A white piece.
	//			BLACK = 2;				// A black piece
	int currentPlayerNum;
	boolean gameInProgress;
	int win_r1, win_c1, win_r2, win_c2; 

	public Board(int row, int col){
		board = new int[row][col];	
	}


	public int getLocation(int row, int col) {
		return board[row][col];
	}


	public int getCurrentPlayer() {
		return currentPlayerNum;
	}


	public void setMove(int row, int col, int currentPlayerNum) {
		board[row][col] = currentPlayerNum;

	}


	public boolean isWinner(int playerNum, int row, int col) {
		if (count( playerNum, board[row][col], row, col, 1, 0 ) >= 5)
			return true;
		if (count( playerNum, board[row][col], row, col, 0, 1 ) >= 5)
			return true;
		if (count( playerNum, board[row][col], row, col, 1, -1 ) >= 5)
			return true;
		if (count( playerNum, board[row][col], row, col, 1, 1 ) >= 5)
			return true;

		/* When we get to this point, we know that the game is not
	          won.  The value of win_r1, which was changed in the count()
	          method, has to be reset to -1, to avoid drawing a red line
	          on the board. */

		win_r1 = -1;
		return false;
	}


	private int count(int playerNum, int i, int row, int col, int dirX, int dirY) {
 int ct = 1;  // Number of pieces in a row belonging to the player.
         
         int r, c;    // A row and column to be examined
         
         r = row + dirX;  // Look at square in specified direction.
         c = col + dirY;
         while ( r >= 0 && r < 30 && c >= 0 && c < 30 && board[r][c] == playerNum ) {
            // Square is on the board and contains one of the players's pieces.
            ct++;
            r += dirX;  // Go on to next square in this direction.
            c += dirY;
         }
         
         win_r1 = r - dirX;  // The next-to-last square looked at.
         win_c1 = c - dirY;  //    (The LAST one looked at was off the board or
         //    did not contain one of the player's pieces.
         
         r = row - dirX;  // Look in the opposite direction.
         c = col - dirY;
         while ( r >= 0 && r < 30 && c >= 0 && c < 30 && board[r][c] == playerNum ) {
            // Square is on the board and contains one of the players's pieces.
            ct++;
            r -= dirX;   // Go on to next square in this direction.
            c -= dirY;
         }
         
         win_r2 = r + dirX;
         win_c2 = c + dirY;
         
         // At this point, (win_r1,win_c1) and (win_r2,win_c2) mark the endpoints
         // of the line of pieces belonging to the player.
         
         return ct;
	}


	public int getPieceStatus(int row, int col) {
		return board[row][col];
	}


	public void setCurrentPlayerNum(int playerNum) {
		this.currentPlayerNum = playerNum;
		
	}


	public void updateBoard(int otherPlayer, int otherRow, int otherCol) {
		board[otherRow][otherCol] = otherPlayer;
		
	}



}
