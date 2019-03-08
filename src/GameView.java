/**
 * GameView panel is the next step after the match making between two online players is confirmed. 
 * At this current stage, this panel only shows the name of the current player. Further development is expected.
 */

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Currency;

import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class GameView extends JPanel {
	private ClientController clientController;
	private PeerController peerController;
	private PeerServerController peerServerController;
	private Board board;
	private int currentPlayerNum;
	private boolean myTurn;
	private boolean winning;
	private String playerName;

	private JPanel panel;
	private JLabel lblPlayer;
	private JLabel lblPlayer_1;
	private JButton btnSurrender;
	private JLabel lblId;
	private JTextArea textArea_id;
	private JTextArea textArea_player1;
	private JTextArea textArea_player2;
	private JButton[][] buttonArray;
	private JTextPane textPane_errorMsg;



	/**
	 * Create the panel.
	 */
	public GameView() {
		winning = false;
		board = new Board(30, 30);
		myTurn = false;
		clientController = null;
		peerController = null;
		peerServerController = null;
		currentPlayerNum = -1;
		playerName = null;


		setLayout(null);

		panel = new JPanel(new GridLayout(30, 30));

		buttonArray = new JButton[30][30];
		for (int i = 0; i < 30; i++) {
			for(int j=0; j<30; j++) {
				buttonArray[i][j] = new JButton();
				buttonArray[i][j].setBounds(16*i,16*j, 16, 16);

				buttonArray[i][j].addActionListener( new BtnCellActionListener());
				panel.add(buttonArray[i][j]);
			}
		}

		panel.setBounds(10, 53, 485, 485);
		this.add(panel);
		panel.setLayout(null);

		lblPlayer = new JLabel("Player01");
		lblPlayer.setForeground(Color.BLACK);
		lblPlayer.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPlayer.setBounds(505, 53, 159, 31);
		add(lblPlayer);

		lblPlayer_1 = new JLabel("Player02");
		lblPlayer_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPlayer_1.setBounds(505, 253, 170, 31);
		add(lblPlayer_1);

		btnSurrender = new JButton("Surrender");
		btnSurrender.addActionListener(new BtnQuitActionListener());
		btnSurrender.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSurrender.setBounds(505, 507, 159, 31);
		add(btnSurrender);

		lblId = new JLabel("Your ID:");
		lblId.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblId.setBounds(10, 11, 76, 31);
		add(lblId);

		textArea_id = new JTextArea();
		textArea_id.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_id.setEditable(false);
		textArea_id.setBounds(78, 11, 322, 31);
		add(textArea_id);

		textArea_player1 = new JTextArea();
		textArea_player1.setEditable(false);
		textArea_player1.setLineWrap(true);
		textArea_player1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_player1.setBounds(505, 95, 159, 31);
		add(textArea_player1);

		textArea_player2 = new JTextArea();
		textArea_player2.setEditable(false);
		textArea_player2.setLineWrap(true);
		textArea_player2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_player2.setBounds(505, 295, 159, 31);
		add(textArea_player2);

		textPane_errorMsg = new JTextPane();
		textPane_errorMsg.setForeground(Color.BLUE);
		textPane_errorMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textPane_errorMsg.setBackground(null);
		textPane_errorMsg.setEditable(false);
		textPane_errorMsg.setBounds(505, 158, 159, 101);
		add(textPane_errorMsg);



	}


	//****************************************************************

	private void showPlayerTurn(boolean myTurn) {
		if(myTurn) 
			textPane_errorMsg.setText("Your Turn!");
		else
			textPane_errorMsg.setText("Waiting for other player turn!");
	}


	//****************************************************************



	private class BtnCellActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {	
			if (e.getSource() instanceof JButton) {
				int col = (int) (((JButton) e.getSource()).getLocation().getX()/16);
				int row = (int) (((JButton) e.getSource()).getLocation().getY()/16);
				System.out.println("GameView, x and y: " + col +" " + row);
				if(myTurn){
					textPane_errorMsg.setText("");
					if(doClickButton(currentPlayerNum, row, col)){

						if(peerController == null)
							peerServerController.sendPlayerMoved(currentPlayerNum, row, col);
						else 
							peerController.sendPlayerMoved(currentPlayerNum, row, col);
						myTurn = false;
						if(winning == false)
							showPlayerTurn(myTurn);
					}
				}
				else
					textPane_errorMsg.setText("Waiting for other player turn!");

			}
		}
	}

	private void disableBoard() {
		for(int row=0; row<30; row++){
			for(int col=0; col<30; col++){
				if(board.getPieceStatus(row, col) == 0){
					buttonArray[col][row].setEnabled(false);
				}
			}
		}		
	}
	
	private void gameOver(String msg) {
		textPane_errorMsg.setText(msg);
		disableBoard();
		btnSurrender.setText("Back To Lobby");
	}


	private void doWinning(int playerNum) {
		//checking winning player and send the result to the server
		if (playerNum == currentPlayerNum){
			gameOver("You won the game!");
			clientController.sendAfterGameStatus(playerName, "won");
		}
		if(playerNum != currentPlayerNum) {
			gameOver("Player0" + playerNum + " won the game!");
			clientController.sendAfterGameStatus(playerName, "loss");
		}
		
		//set the controller's boolean GameOver to true
		if(peerController != null ) {
			peerController.setGameOver(true);
		} else if( peerServerController != null) {
			peerServerController.setGameOver(true);
		}
		
	}


	private void doSurrender() {
		disableBoard();
		textPane_errorMsg.setText("You've surrender. You lossed the game.");
		if(peerController == null) {
			peerServerController.setGameOver(true);
			peerServerController.sendSurrender(currentPlayerNum);
			peerServerController.getClientController().sendAfterGameStatus(playerName, "loss");
		} else {
			peerController.setGameOver(true);
			peerController.sendSurrender(currentPlayerNum);
			peerController.getClientController().sendAfterGameStatus(playerName, "loss");
		}
		//change button text
		btnSurrender.setText("Back To Lobby");	
	}


	private void doTied() {
		clientController.sendGameResult(playerName, "tied");
		btnSurrender.setText("Back To Lobby");
		
	}


	//************************************************************************************************
	private class BtnQuitActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(btnSurrender.getText().equals("Surrender")) {
				doSurrender();
				
			} else if(btnSurrender.getText().equals("Back To Lobby")) {
				
				
				if(peerController != null) {
					System.out.println("GameView, checking isReading() : " + peerController.isReading());
					peerController.closeSocket();
					peerController.doBackToLobby();
					
				} else {
					System.out.println("GameView, checking isReading() : " + peerServerController.isReading());
					peerServerController.closeSocket();
					peerServerController.doBackToLobby();
				}
				
				System.out.println("Yeah!!! Socket is closed");	
				}
			}

		

		}
	
	public boolean doClickButton(int playerNum, int row, int col) {
		//check piece status of location x,y
		if(board.getLocation(row,col) != 0 ){
			if(board.getCurrentPlayer() == 2){
				textPane_errorMsg.setText("Please click an empty square.");
	
			} else{
				textPane_errorMsg.setText("Please click an empty square.");
			}
			return false;
		}
	
		//set icon of the button
		if(playerNum == 1){
			this.buttonArray[col][row].setIcon(new ImageIcon("white.png"));
		} else if (playerNum == 2){
			this.buttonArray[col][row].setIcon(new ImageIcon("black.png"));
	
		}
	
		//set cell location and broadcast to other player
		board.setMove(row,col, playerNum);
		System.out.println("GameView, setMove: " + row + " " + col + " playernum: " + playerNum );
	
		if(board.isWinner(playerNum, row,col)){
			winning = true;
			doWinning(playerNum);
		}
	
		boolean emptySpace = false;     // Check if the board is full.
		for (int i = 0; i < 30; i++)
			for (int j = 0; j < 30; j++)
				if (board.getPieceStatus(i,j) == 0)
					emptySpace = true;
		if (emptySpace == false) {
			gameOver("The game ends in a draw.");
			doTied();
			
		}
	
	
		return true;
	}


	public void setPlayerTurn(boolean b) {
		showPlayerTurn(b);
		this.myTurn = b;
	}


	//************************************************************************************************

	public void setPlayername(String name, int playerNum, String player1, String player2) {
		textArea_id.setText(name);
		textArea_player1.setText(player1);
		textArea_player2.setText(player2);
		this.playerName = name;
		this.currentPlayerNum = playerNum;
		board.setCurrentPlayerNum(playerNum);
		if(currentPlayerNum == 1) {
			myTurn = true;
			showPlayerTurn(myTurn);
		}else {
			myTurn = false;
			showPlayerTurn(myTurn);
		}

	}

	public Board getBoard() {
		return this.board;
	}


	public void access(PeerController peerController2) {
		this.peerController = peerController2;
	}


	public void access(PeerServerController peerServerController2) {
		this.peerServerController = peerServerController2;
	}


	public void access(ClientController clientController2) {
		this.clientController = clientController2;

	}


	public void showPlayerDiconnect() {
		if(currentPlayerNum == 1)
			this.textPane_errorMsg.setText("Player02 got disconnect. \nIn this case, you won the game.");
		if(currentPlayerNum == 2)
			this.textPane_errorMsg.setText("Player01 got disconnect. \nIn this case, you won the game.");
		btnSurrender.setText("Back To Lobby");
		disableBoard();

	}


	public void showOtherPlayerSurrender(int otherPlayer) {
		disableBoard();
		btnSurrender.setText("Back To Lobby");
		textPane_errorMsg.setText("Player0"+otherPlayer+" has surrendered. \nYou won the game");
		
	}
}
