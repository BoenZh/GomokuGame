/**
  * Group 1
  * Members: Hengthai Liv, Van Nguyen, Boen Zhang
  * Iteration 4: AI
  * Game project: Gomoku
  * CSCI 390 - Spring 2018
  * Date: 05/16/2018
  * Language and compiler used: Java
  * Sources consulted:
		- https://docs.oracle.com/javase/8/docs/api/
		- https://stackoverflow.com
  * A program description: This is the fourth and last iteration for the Gomoku game project. For this iteration, we are working on AI. The user can choose to play offline with AI in three different levels: Easy, Medium, and Hard. Easy is random move. Medium and Hard is based on MinMax algorithm. The board size for AI is 15*15.
  * How to run:
		- Have the text file "AccountDatabase.txt" in the same folder with all the java files.
		- Compile and run MainServer.java, click ‘Start’.
		- Compile and run MainView.java
*/

import java.util.Random;

public class GomokuAI {

	private int size = 15; 
	private static BoardForAI b = BoardForAI.getBoard(20);
	private GomokuAI() {};
	private int depth;
	private static int pNumber=1;
	//private static GomokuAI ai=new GomokuAI();

	public GomokuAI(int i){
		depth=i;
	}

	public GomokuAI getAI(){
		GomokuAI ai=new GomokuAI(depth);
		return ai;
	}


	/*
	alpha: current max value for user
	beta: current min value for opponent

	if val<=alpha: our current alpha is better, val could be ignore
	if val>=beta opponent would not want to see this happen, val could be ignore

	return the minimax score for this move placement

	*/
    public int alpha_betaFind(int depth, int alpha, int beta, int p, int prex, int prey) {

        if(depth >= this.depth || 0!=b.getWinner()) {

            int ans = b.reckon(pNumber) - b.reckon(pNumber%2 + 1);

            if(depth % 2 == 0)
                ans = -ans;

            return ans;
        }

        for(int x=0; x<size; x++) {
            for(int y=0; y<size; y++) {

                if(!b.isEmpty(x, y))
                    continue;

                b.placeMove(x, y, p);
                int val = -alpha_betaFind(depth+1, -beta, -alpha, p%2+1, x, y);

                b.unMove(x, y);

                if(val >= beta)
                    return beta;

                if(val > alpha)
                    alpha = val;
            }
        }
        return alpha;
    }

		/*
		run though all open position, get and return the position with highest score from alpha_betaFinds
		return an int array, array[0]is the move for x, array[1] is the move for y

		*/

    public int[] getNext(int p) {
        int rel[] = new int[2];
        int ans = -100000000;



        for(int x=0; x<size; x++) {
            for(int y=0; y<size; y++) {

                if(!b.isEmpty(x, y))
                    continue;

                b.placeMove(x, y, p);

                int val = -alpha_betaFind(0, -100000000, 100000000, p%2 + 1, x, y);

                int ra = 50;

                if(val > ans || val == ans && ra >= 50) {
                    ans = val;
                    rel[0] = x;
                    rel[1] = y;
                }
                b.unMove(x, y);
            }
        }
        return rel;
    }




}
