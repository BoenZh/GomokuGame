/**
 * ClientController is the controller that handle connection to and from the server.
 * ClientController functionality are:
 * - Initialize connection to the server
 * - Send and receive message to and from the server
 * - Create a PeerServer on the user side
 * - Connect a user to the PeerServer
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.spec.MGF1ParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.View;

public class ClientController implements Runnable {
	private String name;
	private ArrayList<Integer> playerStat; //#game-won-tied
	private ViewController viewCon;
	private Thread worker;
	private Socket sock;
	private InputStream is;
	private OutputStream os;
	private DataOutputStream dos;
	private BufferedReader br;
	private byte [] buff1, buff2;
	private PeerServer pServer;
	private PeerServerController pConnection;
	private PeerController pController;


	/**
	 * Constructor with parameters String ip and int port. 
	 * Create new socket object with String ip and int port.
	 * Initialize InputStream, OutputStream, DataOutputSteam, BufferedReader, and byte [] buff.
	 * @param ip String of IP address
	 * @param port Port number
	 */
	public ClientController(String ip, int port) {
		try {
			pServer = null;
			pConnection = null;
			pController = null;
			name = null;
			playerStat = new ArrayList<Integer>();
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
	 * Get message from the server
	 */
	@Override
	public void run() {

		boolean run = true;

		while(run) {	

			try {
				String msg = receiveMessaage();
				System.out.println("ClientController msg: " + msg);
				ArrayList<String> msgLst = new ArrayList<String>(Arrays.asList(msg.trim().split(" ")));
				String cmd = msgLst.get(0);

				if( cmd.equals("<login_okay>")) {
					doLogin(msgLst);
				} else if( cmd.equals("<login_fail>")) {
					viewCon.displayError("Incorrect username or password.");
				} else if( cmd.equals("<register_okay>")) {
					doRegister(msgLst);
				} else if( cmd.equals("<account_exist>")) {
					viewCon.displayError("Account exists.");
				} else if ( cmd.equals("<online_player>")) {
					msgLst.remove(0);
					viewCon.getLobbyView().updateOnlinePlayerList(msgLst, name);
				} else if ( cmd.equals("<already_online>")) {
					viewCon.displayError("User is already online.");
				} else if( cmd.equals("<request_from>")) {
					msgLst.remove(0);
					viewCon.getLobbyView().updateRequestList(msgLst);
				} else if( cmd.equals("<update_requestLst>")) {
					msgLst.remove(0);
					viewCon.getLobbyView().updateRequestList(msgLst);
				} else if( cmd.equals("<accept_from>")) { 
					String psIp = msgLst.get(1);
					int psPort = Integer.parseInt(msgLst.get(2));
					String serverName = msgLst.get(3);
					sendCurrentStat("<in_game>");
					connectToPeerServer(psIp, psPort, this.name, serverName );
				} else if ( cmd.equals("<anonymous_okay>")) {
					this.name = msgLst.get(1);
					doAnonymousOkay(msgLst);
				} else if( cmd.equals("<player_stat>")) {
					doShowPlayerStat(msgLst);
				}
			} catch (Exception e) {
				e.printStackTrace();
				run = false;
				viewCon.openMainMenu();
				viewCon.disablePVPmode();
				viewCon.displayError("Sorry, our Server is offline. \nOnly AI mode is available.");
			}
		}

	}

	private void doAnonymousOkay(ArrayList<String> msgLst) {
		name = msgLst.get(1);
		String played = msgLst.get(2);
		String won = msgLst.get(3);
		String tied = msgLst.get(4);
		String statString = "Played: " + played +
				"  Won: " + won + "  Tied: " + tied;
		viewCon.getLobbyView().setUsernameLabel(msgLst.get(1));
		viewCon.getLobbyView().setPlayerStatTextField(statString);
		viewCon.openLobby();
	}

	private void doShowPlayerStat(ArrayList<String> msgLst) {
		String played = msgLst.get(1);
		String won = msgLst.get(2);
		String tied = msgLst.get(3);
		String statString = "Played: " + played +
				"  Won: " + won + "  Tied: " + tied;
		viewCon.getLobbyView().setPlayerStatTextField(statString);
		
	}

	private void doRegister(ArrayList<String> msgLst) {
		name = msgLst.get(1);
		String played = msgLst.get(2);
		String won = msgLst.get(3);
		String tied = msgLst.get(4);
		String statString = "Played: " + played +
				"  Won: " + won + "  Tied: " + tied;
		viewCon.getLobbyView().setUsernameLabel(msgLst.get(1));
		viewCon.getLobbyView().setPlayerStatTextField(statString);
		System.out.println("Open Lobby?");
		viewCon.openLobby();

	}

	private void doLogin(ArrayList<String> msgLst) {
		name = msgLst.get(1);
		String played = msgLst.get(2);
		String won = msgLst.get(3);
		String tied = msgLst.get(4);
		String statString = "Played: " + played +
				"  Won: " + won + "  Tied: " + tied;
		viewCon.getLobbyView().setUsernameLabel(msgLst.get(1));
		viewCon.getLobbyView().setPlayerStatTextField(statString);
		viewCon.openLobby();
	}

	public void sendCurrentStat(String string) {
		sendMessage("<in_game> " + name);

	}

	/**
	 * Creating the second Thread and start().
	 */
	public void doIt() {
		worker = new Thread( this );
		worker.start(); // call run() in the new thread
	}

	/**
	 * receive message from the server.
	 * @return String message.
	 */
	public String receiveMessaage() {
		int len;
		String msg = null;
		try {

			len = is.read(buff1);
			msg = new String(buff1, 0, len);

		} catch (IOException e) {
			return null;
		}
		return msg;
	}

	/**
	 * Send message to the server.
	 * @param msg String message to be send to the server.
	 */
	public void sendMessage(String msg) {
		try {
			buff2 = msg.getBytes();
			dos.write(buff2, 0 , msg.length());
			dos.flush();
			System.out.println("ClientController sending msg: "+ msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Access ViewController.
	 * @param vc ViewController object.
	 */
	public void access(ViewController vc) {
		viewCon = vc;
	}

	//************************************************************************************************
	//String status = "won"/"loss"/"tied"
	/**
	 * 
	 */
	public void sendAfterGameStatus(String name, String status) {
		sendMessage("<after_game> " + name + " " + status);
	}
	//************************************************************************************************

	/**
	 * 
	 */
	public void sendAnonymousRequest() {
		sendMessage("<anonymous_request>");

	}

	/**
	 * Send request message to another player via the Server.
	 * @param selectPlayer String user to be requested
	 */
	public void sendRequestTo(String selectPlayer) {
		sendMessage("<request_to> " + selectPlayer);
	}

	/**
	 * Send decline message to another player via the Server.
	 * @param selectPlayer String user to be decline.
	 */
	public void sendDeclineTo(String selectPlayer) {
		sendMessage("<request_decline> " + selectPlayer);
	}

	/**
	 * Send user "Sign up" credential including username and password to the server.
	 * @param userInput String user credential 
	 */
	public void sendRegisterInfo(String userInput) {
		sendMessage("<register> " + userInput);	
	}

	/**
	 * Send user "Sign in" credential including username and password to the server.
	 * @param userInput String user credential
	 */
	public void sendLoginInfo(String userInput) {
		sendMessage("<login> " + userInput);
	}

	/**
	 * Send accept message to another player via Server.
	 * @param selectPlayer String user to be accepted.
	 */
	public void sendAcceptTo(String selectPlayer) {
		String psIp = pServer.getIp();
		String psPort = "" + pServer.getPort();
		sendMessage("<accept_to> " + selectPlayer+ " "+ psIp + " " + psPort );
	}

	/**
	 * Initialize PeerServer object and run the server.
	 */
	public void setUpPeerServer() {
		pServer = new PeerServer(this.name, this, this.viewCon);
		pServer.listen();
		sendCurrentStat("<in_game>");
	}

	/**
	 * Connect to the PeerServer.
	 * @param ip String ip of the PeerServer.
	 * @param port int port number of the PeerServer.
	 * @param id String current user that want to be connect to PeerServer.
	 * @param serverName 
	 */
	//user1 which connect the peer server
	public void connectToPeerServer(String ip, int port, String name, String serverName) {
		pController = new PeerController(ip, port, name, serverName, this.viewCon, this);
		pController.doIt();
		pController.sendGotConnectedToPeerServer();
		


	}

	public void sendGameResult(String playerName, String result) {
		String cmd = "<game_result> " + playerName + " " + result;
		sendMessage(cmd);
	}

	public void doBackToMainView() {
		sendRemoveFromInGame();
		viewCon.openMainMenu();
		
		
	}

	private void sendRemoveFromInGame() {
		String cmd = ("<remove_ingame> " + this.name); 
		
	}

	public void doPlayerBackToLobby(String pName) {
		sendMessage("<request_backtoonline> " + pName);
	}

	public void doLogOut() {
		sendMessage("<logout> " + this.name);
		viewCon.openMainMenu();
		
	}
}