/**
 * PeerConnection is the connection between PeerController and the Server.
 * Its functionality are:
 * - Send and receive message from the PeerController(Client).
 * - (More in the future).
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PeerServerController extends Thread {

	private String serverName;
	private String peerName;
	private String numPlayer;
	private boolean ready;
	private PeerServer peerServer;
	private InputStream in;
	private OutputStream out;
	private DataOutputStream dos;
	private byte [] outBuff, inBuff;
	private Socket sock;
	private ClientController clientController;
	private ViewController viewCon;
	private boolean gameover;
	private boolean readingMsg;

	/**
	 * Constructor of PeerConnection. It initializes the connection to the ClientController via socket.
	 * @param serverName 
	 * @param peerSock Socket of PeerController.
	 * @param peerServer PeerServer object
	 * @param viewCon 
	 * @param clientController 
	 */
	public PeerServerController(String serverName, Socket peerSock, PeerServer peerServer, ClientController clientController, ViewController viewCon) {
		this.serverName = serverName;
		this.clientController = clientController;
		this.viewCon = viewCon;
		this.numPlayer = null;
		this.gameover = false;
		peerName = null;
		ready = false;
		inBuff = new byte [1024];
		outBuff = new byte [1024];
		this.sock = peerSock;
		try {
			in = sock.getInputStream();
			out = sock.getOutputStream();
			dos = new DataOutputStream(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.peerServer = peerServer;
		readingMsg = true;
	}

	/**
	 * Send message to ClientController.
	 * @param msg
	 */
	public void forwardMessage(String msg) {
		try {
			outBuff = msg.getBytes();
			dos.write(outBuff, 0, msg.length());
			dos.flush();
			System.out.println("PeerServerController sending msg: " + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receive message from Client Controller.
	 * @return
	 */
	public String receiveMessaage() {
		int len;
		String msg = null;
		try {
			if(gameover == false) {
				len = in.read(inBuff);
				msg = new String(inBuff, 0, len);
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
	 * Continuously receiving message from ClientController.
	 * Received message includes the command that show the purpose of the message.
	 * Ex: "<ready> user1" 
	 * Each command has different functionality.
	 */
	public void run() {
		while (readingMsg) {
			try {
				String msg = receiveMessaage();
				System.out.println("PeerServerController msg: " + msg);
				if(msg != null) {
					ArrayList<String> msgLst = new ArrayList<String>(Arrays.asList(msg.trim().split(" ")));
					String cmd = msgLst.get(0);

					if(cmd.equals("<connected_to_peerServer>")){
						peerName = msgLst.get(1);
						setRandomPlayer();
					} else if( cmd.equals("<player_make_moved>")){
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
				System.out.println("PeerServerController, readingMsg: " + readingMsg);
				e.printStackTrace();
				
			}
		}
	}

	private void doOtherPlayerSurrender(int otherPlayer) {
		this.gameover = true;
		this.readingMsg = false;
		viewCon.getGameView().showOtherPlayerSurrender(otherPlayer);
		String playerName = serverName;
		clientController.sendAfterGameStatus(playerName, "won");

	}

	private void doOtherPlayerDisconnected() {
		//only send won when disconnected while in gaming
		if(gameover == false) {
			viewCon.getGameView().showPlayerDiconnect();
			String playerName = this.serverName;
			clientController.sendGameResult(playerName, "won");
		}

	}

	private void doOtherPlayerMakedMove(int otherPlayer, int otherRow, int otherCol) {
		viewCon.getGameView().setPlayerTurn(true);
		viewCon.getGameView().doClickButton(otherPlayer, otherRow, otherCol);

	}


	private void setRandomPlayer() {
		Random rand = new Random();
		int randNum = rand.nextInt(2) + 1;
		this.numPlayer = "" + randNum;


		System.out.println(this.numPlayer.equals("1"));
		if(this.numPlayer.equals("1")){

			viewCon.initGameView(this.serverName, 1, this.serverName, this.peerName);
			viewCon.getGameView().access(this);
			viewCon.getGameView().access(clientController);
			sendPeerClientTheirNumPlayer("2");

		}
		else{

			viewCon.initGameView(this.serverName, 2, this.peerName, this.serverName);
			viewCon.getGameView().access(this);
			viewCon.getGameView().access(clientController);
			sendPeerClientTheirNumPlayer("1");

		}


	}

	private void sendPeerClientTheirNumPlayer(String peerPlayerNum) {
		forwardMessage("<set_numPlayer>" + " " + peerPlayerNum );
	}



	/**
	 * Return String name of the PeerConnection.
	 */
	public String getPlayerName() {
		return this.serverName;
	}

	public boolean checkReady() {

		return this.ready;
	}

	public void sendPlayerMoved(int currentPlayerNum, int row, int col) {
		forwardMessage("<player_make_moved>" + " " + currentPlayerNum +
				" " + row +" " + col);
	}

	public void sendSurrender(int currentPlayerNum) {
		forwardMessage("<player_surrender> " + currentPlayerNum);

	}

	public void setGameOver(boolean b) {
		this.gameover = b;
		this.readingMsg = false;

	}


	public ClientController getClientController() {
		return clientController;

	}

	public void closeSocket() {
		this.sock = null;
		peerServer.closeSocket();

	}

	public boolean isReading() {
		// TODO Auto-generated method stub
		return this.readingMsg;
	}

	public void doBackToLobby() {
		clientController.doPlayerBackToLobby(serverName);
		viewCon.openLobby();

	}
}
