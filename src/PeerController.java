/**
 * PeerController is controller that handle the connection to and from the PeerSever. Its functionality includes:
 * - Initializes connection to PeerServer.
 * - Sending and receiving message from the PeerServer.
 * - 
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
import java.util.Arrays;

public class PeerController implements Runnable {
	private String peerName;
	private String serverName;
	private String numPlayer;
	private boolean gameover;
	private int player;
	private ClientController clientController;
	private ViewController viewCon;
	private boolean readingMsg;
	private Thread worker;
	private Socket sock;
	private InputStream is;
	private OutputStream os;
	private DataOutputStream dos;
	private BufferedReader br;
	private byte [] buff1, buff2;



	/**
	 * Constructor of PeerController. It initializes the connection to the server via Socket.
	 * @param ip String PeerServer IP address.
	 * @param port int PeerServer Port number.
	 * @param name String PeerController's name.
	 * @param serverName 
	 * @param vc ViewController view controller
	 */
	public PeerController(String ip, int port, String name, String serverName, ViewController vc, ClientController clientCon) {
		try {
			this.peerName = name;
			this.serverName = serverName;
			this.numPlayer = null;
			this.player = 0;
			this.clientController = clientCon;
			this.viewCon = vc;
			this.gameover = false;
			sock = new Socket(ip, port);
			is = sock.getInputStream();
			os = sock.getOutputStream();
			dos = new DataOutputStream(os);
			br = new BufferedReader(new InputStreamReader(is));	
			buff1 = new byte[1024];
			buff2 = new byte[1024];
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new thread and start.
	 */
	public void doIt() {
		worker = new Thread( this );
		worker.start(); // call run() in the new thread
	}

	/**
	 * Send message to the PeerServer.
	 * @param msg String message to be send.
	 */

	public void sendMessage(String msg) {
		try {
			buff2 = msg.getBytes();
			dos.write(buff2, 0 , msg.length());
			dos.flush();
			System.out.println("PeerController sending msg: " + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Receive message from the PeerServer.
	 * @return String message.
	 */
	public String receiveMessaage() {
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String msg = null;
		try {
			if(gameover == false) {
				int len = is.read(buff1);
				msg = new String(buff1, 0, len);
			}
		} catch (IOException e) {
			readingMsg = false;
			if(gameover ==  false)
				doOtherPlayerDisconnected();
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * Continuously receiving message from the PeerServer.
	 * Received message includes the command that show the purpose of the message.
	 * Ex: "<ready> user1" show that user1 is ready.
	 */
	@Override
	public void run() {
		readingMsg = true;
		while(readingMsg){
			try {
				String msg = receiveMessaage();
				System.out.println("PeerController msg: " + msg);
				if(msg != null) {
					ArrayList<String> msgLst = new ArrayList<String>(Arrays.asList(msg.trim().split(" ")));
					String cmd = msgLst.get(0);


					if( cmd.equals("<set_numPlayer>")){
						this.numPlayer  = msgLst.get(1);
						doSetUpGameView();
					} else if ( cmd.equals("<player_make_moved>")) {
						int otherPlayer = Integer.parseInt(msgLst.get(1));
						int otherRow = Integer.parseInt(msgLst.get(2));
						int otherCol = Integer.parseInt(msgLst.get(3));
						doOtherPlayerMakedMove(otherPlayer, otherRow, otherCol);
					} else if( cmd.equals("<player_surrender>")){
						int otherPlayer = Integer.parseInt(msgLst.get(1));
						doOtherPlayerSurrender(otherPlayer);
					}
				}
			} catch (Exception e) {
				readingMsg = false;
				e.printStackTrace();
			}
		}
	}

	private void doOtherPlayerSurrender(int otherPlayer) {
		this.gameover = true;
		this.readingMsg  = false;
		viewCon.getGameView().showOtherPlayerSurrender(otherPlayer);
		String playerName = peerName;
		clientController.sendAfterGameStatus(playerName, "won");

	}


	private void doOtherPlayerMakedMove(int otherPlayer, int otherRow, int otherCol) {
		viewCon.getGameView().setPlayerTurn(true);
		viewCon.getGameView().doClickButton(otherPlayer, otherRow, otherCol);
	}

	private void doOtherPlayerDisconnected() {
		//only send won when disconnected while in gaming
		if(gameover  ==  false) {
			viewCon.getGameView().showPlayerDiconnect();
			String playerName = this.peerName;
			clientController.sendGameResult(playerName, "won");
		} 
	}

	private void doSetUpGameView() {
		if(this.numPlayer.equals("1")){
			viewCon.initGameView(this.peerName, 1, this.peerName, this.serverName);
			viewCon.getGameView().access(this);
			viewCon.getGameView().access(clientController);

		}
		else{
			viewCon.initGameView(this.peerName, 2, this.serverName, this.peerName);
			viewCon.getGameView().access(this);
			viewCon.getGameView().access(clientController);
		}
	}

	public void sendGotConnectedToPeerServer() {
		sendMessage("<connected_to_peerServer> " + this.peerName);

	}

	public void sendPlayerMoved(int currentPlayerNum, int row, int col) {
		sendMessage("<player_make_moved>" + " " + currentPlayerNum +
				" " + row +" " + col);
	}

	public void sendSurrender(int currentPlayerNum) {
		sendMessage("<player_surrender> " + currentPlayerNum);

	}

	public void setGameOver(boolean b) {
		this.gameover = b;
		this.readingMsg = false;
	}


	public ClientController getClientController() {
		return this.clientController;
	}

	public void closeSocket() {
		this.sock = null;

	}

	public boolean isReading() {
		return this.readingMsg;
	}

	public void doBackToLobby() {
		clientController.doPlayerBackToLobby(peerName);
		viewCon.openLobby();

	}








}
