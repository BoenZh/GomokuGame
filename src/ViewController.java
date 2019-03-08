/**
 * ViewController is the controller that manage all the view classes including MainView StartUpView LobbyView and GameView. 
 * ViewController function includes:
 * - Switching to LobbyView
 * - Switching to MainView
 * - Update error message in StartUpView
 * - Initialize GameView
 * - Return LobbyView Object if needed
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.text.View;

public class ViewController {
	private MainView mView;
	private StartUpView suView;
	private LobbyView lbView;
	private GameView gView;
	private AiFrame aiFrame;
	private AiFrameEZ aiFrameEZ;

	/**
	 * Empty Constructor.
	 */
	public ViewController() {
//		Board b= new Board(30);
//		gView = new GameView(b);
	}

	/**
	 * Access MainView, StartUpView, and LobbyView.
	 * @param window MainView object
	 * @param sView	StartUpView object
	 * @param lbView2 LobbyView object
	 */
	public void access(MainView window, StartUpView sView, LobbyView lbView2) {
		this.mView = window;
		this.suView = sView;
		this.lbView = lbView2;
	}
	
	public void access(GameView view) {
		gView = view;
	}
	
	/**
	 * Open LobbyView view in MainView view.
	 */
	public void openLobby() {
		mView.setupLobbyView();
	}

	/**
	 * Open StartUpView view in MainView view.
	 */
	public void openMainMenu() {
		mView.setupStartUpView();

	}

	/**
	 * Display error message in StartUpView view.
	 * @param errorMsg String error messages.
	 */
	public void displayError(String errorMsg) {
		suView.displayError(errorMsg);	
	}

	/**
	 * For next iteration.
	 */
	public void openGameBoard() {
	}

	/**
	 * Initialize GameView object with given user name.
	 * @param  
	 * @param name String name of user
	 * @param peerName 
	 * @param player 
	 * 
	 */
	public void initGameView(String playerName, int playerNum , String player1, String player2) {
		gView = new GameView();
		gView.setPlayername(playerName, playerNum, player1, player2);
		mView.setUpGameView(this.gView);
	}

	/**
	 * Return LobbyView panel.
	 * @return LobbyView lobby view panel.
	 */
	public LobbyView getLobbyView() {
		return lbView;
	}

	/**
	 * Disable PVP mode in StartUpView panel
	 */
	public void disablePVPmode() {
		suView.disablePVPmode();
		
	}

	public GameView getGameView() {
		// TODO Auto-generated method stub
		return this.gView;
	}

	public void switchToAiFrame(String gameOption) {
		aiFrame = new AiFrame(2, this);
		aiFrame.setVisible(true);
		this.mView.getFrame().setVisible(false);	
	}
	
	public void switchToAiFrameEZ(String gameOption) {
		aiFrameEZ = new AiFrameEZ(1, this);
		aiFrameEZ.setVisible(true);
		this.mView.getFrame().setVisible(false);		
	}
	
	
	public void switchToAiFrameMD(String gameOption) {
		aiFrame = new AiFrame(1, this);
		aiFrame.setVisible(true);
		this.mView.getFrame().setVisible(false);
	}

	public void switchToStartUpView(AiFrameEZ aiFrameEZ02) {
		aiFrameEZ02.setVisible(false);
		openMainMenu();
		this.mView.getFrame().setVisible(true);
	}

	public void switchToStartUpView(AiFrame aiFrame02) {
		aiFrame02.setVisible(false);
		openMainMenu();
		this.mView.getFrame().setVisible(true);
	}
}

