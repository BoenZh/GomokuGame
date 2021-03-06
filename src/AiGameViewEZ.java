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
import java.util.Random;

import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import java.awt.Color;
import javax.swing.JTextField;

public class AiGameViewEZ extends JPanel {
	private AiFrameEZ aiFrameEZ;
	private JPanel panel;
	private JLabel lblPlayer;
	private JLabel lblPlayer_1;
	private JButton btn_mainMenu;
	private JLabel lblId;
	private JTextArea textArea_id;
	private JTextArea textArea_player1;
	private JTextArea textArea_player2;
	private BoardForAI b=BoardForAI.getBoard(30);
	private GomokuAI ai;
	private JButton[][] array;

	private JTextArea txtArea_playerTurn;
	private JTextArea textArea_errorMsg;



	/**
	 * Create the panel.
	 * @param aiFrameEZ 
	 */
	public AiGameViewEZ(int temp, AiFrameEZ aiFrameEZ) {
		this.aiFrameEZ = aiFrameEZ;
		setLayout(null);
		panel = new JPanel(new GridLayout(30, 30));
		array = new JButton[30][30];
		for (int i = 0; i < 30; i++) {
			for(int j=0; j<30; j++) {
				array[i][j] = new JButton();
				array[i][j].setBounds(16*i,16*j, 16, 16);

				array[i][j].addActionListener( new BtnCellActionListener());
				panel.add(array[i][j]);
			}
		}


		panel.setBounds(10, 61, 500, 487);
		this.add(panel);
		panel.setLayout(null);

		lblPlayer = new JLabel("Player01");
		lblPlayer.setForeground(Color.BLACK);
		lblPlayer.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPlayer.setBounds(520, 61, 170, 31);
		add(lblPlayer);

		lblPlayer_1 = new JLabel("Player02");
		lblPlayer_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPlayer_1.setBounds(520, 228, 170, 31);
		add(lblPlayer_1);

		btn_mainMenu = new JButton("Main Menu");
		btn_mainMenu.addActionListener(new BtnQuitActionListener());
		btn_mainMenu.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_mainMenu.setBounds(520, 517, 170, 31);
		add(btn_mainMenu);

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
		textArea_player1.setLineWrap(true);
		textArea_player1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_player1.setBounds(520, 94, 170, 31);
		add(textArea_player1);

		textArea_player2 = new JTextArea();
		textArea_player2.setLineWrap(true);
		textArea_player2.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_player2.setBounds(520, 270, 170, 31);
		add(textArea_player2);

		txtArea_playerTurn = new JTextArea();
		txtArea_playerTurn.setLineWrap(true);
		txtArea_playerTurn.setForeground(Color.RED);
		txtArea_playerTurn.setEditable(false);
		txtArea_playerTurn.setFont(new Font("Tahoma", Font.BOLD, 15));
		txtArea_playerTurn.setBounds(520, 161, 170, 36);
		add(txtArea_playerTurn);

		textArea_errorMsg = new JTextArea();
		textArea_errorMsg.setWrapStyleWord(true);
		textArea_errorMsg.setForeground(Color.BLUE);
		textArea_errorMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_errorMsg.setEditable(false);
		textArea_errorMsg.setLineWrap(true);
		textArea_errorMsg.setBackground(null);
		textArea_errorMsg.setVisible(true);
		textArea_errorMsg.setBounds(520, 322, 170, 150);
		add(textArea_errorMsg);

		b.placeMove(7, 7, 1);
		array[7][7].setIcon(new ImageIcon("black.png"));
		array[7][7].setOpaque(true);

		textArea_id.setText("Player2");
		textArea_player1.setText("AI");
		textArea_player2.setText("Human");
		textArea_player1.setEditable(false);
		textArea_player2.setEditable(false);

	}


	private class BtnCellActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int check=0;
			boolean placeR=false;
			int r=0;
			int c=0;

			int x=0;
			int y=0;


			if (e.getSource() instanceof JButton) {
				x=(int) (((JButton) e.getSource()).getLocation().getX()/16);
				y=(int) (((JButton) e.getSource()).getLocation().getY()/16);

				if(b.isEmpty(x, y)){
					textArea_errorMsg.setVisible(false);
					b.placeMove(x, y, 2);
					array[x][y].setIcon(new ImageIcon("white.png"));
					array[x][y].setOpaque(true);
					check=b.getWinner();
					if(b.check(x, y, 2)){
						textArea_errorMsg.setVisible(true);
						System.out.println("human player win");
						txtArea_playerTurn.setText("Human win");
						lockBtn();
						textArea_errorMsg.setText("Human player win! \nTo return to main menu, click the \"Main Menu\" button.");

					}
					else{
						while(!placeR){
						Random rand=new Random();
						r=rand.nextInt(30);
						c=rand.nextInt(30);
						if(b.isEmpty(r, c)){

							b.placeMove(r, c, 1);
							array[r][c].setIcon(new ImageIcon("black.png"));
							array[r][c].setOpaque(true);
							//System.out.println(r);
							check=b.getWinner();
							placeR=true;
						}
						}



					if(b.check(r, c, 1)){
						textArea_errorMsg.setVisible(true);
						System.out.println("AI player win");
						txtArea_playerTurn.setText("AI win");
						lockBtn();
						textArea_errorMsg.setText("AI player win! \nTo return to main menu, click the \"Main Menu\" button.");
					}
					}

				}//end of move if
				else{
					textArea_errorMsg.setVisible(true);

					textArea_errorMsg.setText("Invalid Move! Try another location.");

				}


			}
		}
	}

	//************************************************************************************************
	private class BtnQuitActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			textArea_errorMsg.setVisible(true);
			textArea_errorMsg.setText("");	
			for (int i = 0; i < 30; i++) {
				for(int j=0; j<30; j++) {
					array[i][j].setEnabled(false);
					
				}
			}
			b.clear();
			aiFrameEZ.switchToMainMenuFrame();
		}
	}
	//************************************************************************************************

	public void lockBtn(){
		for (int i = 0; i < 30; i++)
			for(int j=0; j<30; j++)
				array[i][j].setEnabled(false);

	}

	public void unlockBtn(){
		for (int i = 0; i < 30; i++)
			for(int j=0; j<30; j++)
				array[i][j].setEnabled(true);

	}




	//****************************************************************


}
