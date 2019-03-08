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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;


import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import java.awt.Color;


public class BoardForAI {
	private Cell board[][];
	private static int size;
	private int player;
	private int r,c;
	private int winner;
	private String name="";
	private String name2="";
	private static final BoardForAI b = new BoardForAI(30);
	private static final BoardForAI b20 = new BoardForAI(20);



	private int check;

	public static BoardForAI getBoard(int size02){
		size = size02;
		if(size02 == 30)
			return b;
		return b20;
	}

	public int getCurrentPlayer(){
		return player;
	}

	public void setCurrentPlayer(int p){
		player=p;
	}

	public void setR(int row){
		r=row;
	}
	public void setC(int col){
		c=col;
	}

	public void setPlayer1(String p){
		name=p;
	}
	public String getPlayer1(){
		return name;
	}

	public void setPlayer2(String p){
		name2=p;
	}
	public String getPlayer2(){
		return name2;
	}


	public BoardForAI(int size) {
		super();
		this.size = size;
		winner=0;
		board = new Cell[size][size];



		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				board[r][c] = new Cell(r, c);

		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++)
				board[r][c].initialize();



	}

	public BoardForAI(BoardForAI other) {
		this(other.size);
		for (int r = 0; r < size; r++)
			for (int c = 0; c < size; c++) {
				board[r][c].owner = other.board[r][c].owner;
			}
	}


	/**
	 * Clears the board and replaces all values with 0
	 */
	public void clear() {
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[i].length; j++) {
				board[i][j].owner = 0;
			}
	}


	/**
	 * Resets the board, calls Clear() and wipes the board then replaces the beginning peices
	 */
	public void reset() {
		clear();

	}

	public boolean isEmpty(int x,int y){
		return this.spaceOwner(x, y)==0;
	}

	public void unMove(int x,int y){
		board[x][y].owner=0;
		this.setWinner(0);
	}

	/**
	 * Display calls the helper method display, whihc is a to string method
	 */
	public void display() {
		display(board);
	}

	/**
	 * Helper method for Display, prints to screen a given board
	 *
	 * @param b passed in board to be printed
	 */
	public void display(Cell[][] b) {
		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++)
				System.out.print(b[r][c].owner + " ");
			System.out.println();
		}

	}

	public boolean noSpace(int[][] b, int p) {
		//Checking no moves left game is over
		return false;
	}







	/**
	 * once surrender is intiatd creates a new board
	 */
	public void surrender() {
		reset();
	}



	/**
	 * Places move is checkspace is true and flips pieces
	 *
	 * @param r row in the game matrix
	 * @param c column in the game matrix
	 * @param p Given player
	 * @return Places move on board or says illegal
	 */
	public int placeMove(int r, int c, int p) {
		int row = r;
		int col = c;
		int playr = p;
		Cell temp;
		if (checkSpace(row, col, playr)) {

			board[row][col].owner = playr;
			temp=board[row][col];
			//System.out.println(r+"   "+c);

			if(check(r,c,p)){
				setWinner(playr);
				System.out.println("winner is :"+playr);
			}
		} else {
			wrongPlace();
			System.out.println("Move is unavailable");
			return 0;
		}
		//display();
		return 1;
	}

	public boolean check(int r,int c,int p){
		Cell temp;

		int playr=p;
		boolean result=false;

		for(int i=0;i<size;i++){
			for (int j=0;j<size;j++){
				temp=board[i][j];
				if(checkWinN(temp,playr)||checkWinNW(temp,playr)||checkWinW(temp,playr)||checkWinSW(temp,playr)
						||checkWinS(temp,playr)||checkWinSE(temp,playr)||checkWinE(temp,playr)||checkWinNE(temp,playr)){
					setWinner(playr);
					return true;}

			}
		}

		return false;
	}

	public boolean checkWinN(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;


		if(temp.row>(size-5))
			return false;
		else{
			for(int i=0;i<5;i++){
				if(temp.owner==playr)
					count++;
				temp=temp.N;

			}
			if(count==5)
				return true;
			else
				return false;
		}
	}

	public boolean checkWinNW(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;


		if(temp.row>(size-5)||temp.col>(size-5))
			return false;
		else{
			for(int i=0;i<5;i++){
				if(temp.owner==playr)
					count++;
				temp=temp.NW;

			}
			if(count==5)
				return true;
			else
				return false;
		}
	}

	public boolean checkWinW(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;

		if(temp.col>(size-5))
			return false;
		else{
			for(int i=0;i<5;i++){
				if(temp.owner==playr)
					count++;

				temp=temp.W;



			}
			if(count==5)
				return true;
			else
				return false;
		}
	}
	public boolean checkWinSW(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;

		if(temp.row<4||temp.col>(size-5))
			return false;
		else{

			for(int i=0;i<5;i++){
				if(temp.owner==playr)
					count++;
				temp=temp.SW;

			}
			if(count==5)
				return true;
			else
				return false;
		}
	}

	public boolean checkWinS(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;

		if(temp.row<4)
			return false;
		else{
			for(int i=0;i<5;i++){
				if(temp.owner==playr)
					count++;
				temp=temp.S;

			}
			if(count==5)
				return true;
			else
				return false;
		}
	}
	public boolean checkWinSE(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;

		if(temp.row<4||temp.col<4)
			return false;
		else{

			for(int i=0;i<5;i++){
				if(temp.owner==playr)
					count++;
				temp=temp.SE;
			}

			if(count==5)
				return true;
			else
				return false;
		}

	}

	public boolean checkWinE(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;


		if(temp.col<4){
			//System.out.println("hi");
			return false;
		}
		else{
			for(int i=0;i<5;i++){
				if(temp.owner==playr){

					count++;
					//System.out.println(count);
				}

				temp=temp.E;

			}
			if(count==5)
				return true;
			else
				return false;
		}
	}

	public boolean checkWinNE(Cell c, int p){

		int playr = p;
		int count=0;
		Cell temp=c;

		if(temp.row>(size-5)||temp.col<4)
			return false;
		else{

			for(int i=0;i<5;i++){
				if(temp.owner==playr)
					count++;
				temp=temp.NE;

			}
			if(count==5)
				return true;
			else
				return false;
		}
	}

	public void displayWin(int p){
		System.out.println("winner is "+p);
	}


	public boolean checkSpace(int r, int c, int pNum) {
		//check that space is an actual empty space
		if (spaceOwner(r, c) != 0 && spaceOwner(r,c) != 3)
			return false;

		return true;
	}

	public void setWinner(int p){
		winner=p;
	}
	public int getWinner(){
		return winner;
	}
	public String wrongPlace(){
		String s="invaild place";
		return s;
	}







	/**
	 * Determine who is the owner of the current passed  space
	 *
	 * @param r row in the game matrix
	 * @param c column in the game matrix
	 * @return The owner of the current space, or -1 if its off the board
	 */
	public int spaceOwner(int r, int c) {
		if (r >= 0 && r < size && c >= 0 && c < size)
			return board[r][c].owner;
		return -1;
	}

	public int cellOwner(Cell c) {
		if (c == null)
			return -1;
		return c.owner;
	}

	/*
    scan though the board, find out the highest score for given player number base on board status
	 */
	public int reckon(int input) {

		int dx[] = {1, 0, 1, 1};
		int dy[] = {0, 1, 1, -1};
		int ans = 0;

		for(int x=0; x<size; x++) {
			for (int y = 0; y < size; y++) {
				if (board[x][y].owner != input)
					continue;

				int num[][] = new int[2][100];

				for (int i = 0; i < 4; i++) {
					int sum = 1;
					int flag1 = 0, flag2 = 0;

					int tx = x + dx[i];
					int ty = y + dy[i];
					while (tx >= 0 && tx < size
							&& ty >= 0 && ty < size
							&& board[tx][ty].owner == input) {
						tx += dx[i];
						ty += dy[i];
						++sum;
					}

					if(tx >= 0 && tx < size
							&& ty >= 0 && ty < size
							&& board[tx][ty].owner == 0)
						flag1 = 1;

					tx = x - dx[i];
					ty = y - dy[i];
					while (tx >= 0 && tx < size
							&& ty >= 0 && ty < size
							&& board[tx][ty].owner == input) {
						tx -= dx[i];
						ty -= dy[i];
						++sum;
					}

					if(tx >= 0 && tx < size
							&& ty >= 0 && ty < size
							&& board[tx][ty].owner == 0)
						flag2 = 1;

					if(flag1 + flag2 > 0)
						++num[flag1 + flag2 - 1][sum];
				}

				//case 5
				if(num[0][5] + num[1][5] > 0)
					ans = Math.max(ans, 100000);
				//case: 4
				else if(num[1][4] > 0
						|| num[0][4] > 1
						|| (num[0][4] > 0 && num[1][3] > 0))
					ans = Math.max(ans, 10000);
				//case double 3
				else if(num[1][3] > 1)
					ans = Math.max(ans, 5000);
				//case single 3 and dead 3
				else if(num[1][3] > 0 && num[0][3] > 0)
					ans = Math.max(ans, 1000);
				//case dead 4
				else if(num[0][4] > 0)
					ans = Math.max(ans, 500);
				//case single 3
				else if(num[1][3] > 0)
					ans = Math.max(ans, 200);
				//case double 2
				else if(num[1][2] > 1)
					ans = Math.max(ans, 100);
				//case dead 3
				else if(num[0][3] > 0)
					ans = Math.max(ans, 50);
				//case double 2
				else if(num[1][2] > 1)
					ans = Math.max(ans, 10);
				//case single 2
				else if(num[1][2] > 0)
					ans = Math.max(ans, 5);
				//case dead 2
				else if(num[0][2] > 0)
					ans = Math.max(ans, 1);

			}
		}

		return ans;
	}









	//black is 1, white is 2, plane 0
	public static void start() {
		BoardForAI b = null;
		if(size == 30)
			 b = new BoardForAI(size);
		if(size == 20)
			 b = new BoardForAI(size);


		Scanner scan = new Scanner(System.in);


		int r, c;
		int p1 = 0;
		int p2 = 1;
		int count = 0;

		// b.display();

	}//end start()

	private class Cell {
		private int row, col;
		private Cell N, NW, W, SW, S, SE, E, NE;

		private int owner;

		public Cell(int r, int c) {
			row = r;
			col = c;
			owner = 0;



		}

		//This method must run on each cell for the neighbor functionalitiy to work
		public void initialize() {
			//set all references to neighbors
			if (row < size - 1)
				N = board[row + 1][col];
			if (row > 0)
				S = board[row - 1][col];
			if (col < size - 1)
				W = board[row][col + 1];
			if (col > 0)
				E = board[row][col - 1];

			if (row < size - 1 && col < size - 1)
				NW = board[row + 1][col + 1];
			if (row < size - 1 && col > 0)
				NE = board[row + 1][col - 1];
			if (row > 0 && col < size - 1)
				SW = board[row - 1][col + 1];
			if (row > 0 && col > 0)
				SE = board[row - 1][col - 1];





		}
	}



}
