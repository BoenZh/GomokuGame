/**
 * ClientConnection handles the connection to and from the ClientController. Its main functionality is to receive message from the client and perform the task accordingly.
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ClientConnection  extends Thread {
	private String name;
	private ArrayList<String> requestLst;
	private Server server;
	private InputStream in;
	private OutputStream out;
	private DataOutputStream dos;
	private byte [] outBuff, inBuff;
	private Socket sock;

	/**
	 * Constructor for ClientConnection. It initialized the connection to the client's socket.
	 * @param sock Socket client's socket
	 * @param server Server the server object
	 */
	public ClientConnection(Socket sock, Server server) {
		name = null;
		requestLst = new ArrayList<String>();
		inBuff = new byte [1024];
		outBuff = new byte [1024];
		this.sock = sock;
		try {
			in = sock.getInputStream();
			out = sock.getOutputStream();
			dos = new DataOutputStream(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.server = server;
	}

	/**
	 * Receive message from Client and call server to broadcast the message.
	 */
	public void run() {
		String msg;
		boolean running = true;
		while (running) {
			try {
				msg = receiveMessaage();
				System.out.println("ClientConnection msg: " + msg);

				ArrayList<String> msgLst = new ArrayList<String>(Arrays.asList(msg.trim().split(" ")));
				String cmd = msgLst.get(0);
				if( cmd.equals("<login>")) {
					String id = msgLst.get(1);
					String pwd = msgLst.get(2);
					doLogin(id, pwd);
				} else if ( cmd.equals("<register>")) {
					String id = msgLst.get(1);
					String pwd = msgLst.get(2);
					doRegister(id, pwd);
				} else if ( cmd.equals("<request_to>")) {
					String user2 = msgLst.get(1);
					doRequestTo(user2);
				} else if( cmd.equals("<request_decline>")) {
					String user2 = msgLst.get(1);
					doRemoveRequestFrom(user2);
				} else if ( cmd.equals("<accept_to>")) {
					String user1 = msgLst.get(1);
					String ip = msgLst.get(2);
					String port = msgLst.get(3);
					sendBackPeerServerInfo(user1,ip, port);
					doInGame(name);
				} else if ( cmd.equals("<in_game>")) {
					String user = msgLst.get(1);
					doInGame(user);
				} else if (cmd.equals("<anonymous_request>")) {
					doAnonymous();
				} else if( cmd.equals("<after_game>")) {
					String id = msgLst.get(1);
					String stat = msgLst.get(2);
					doAfterGame(id, stat);
				} else if( cmd.equals("<game_result>")) {
					String playerName  = msgLst.get(1);
					String result = msgLst.get(2);
					doGameResult(playerName, result);
				} else if( cmd.equals("<remove_ingame>")){
					String playerName = msgLst.get(1);
					doRemoveFromInGame(playerName);
				} else if(cmd.equals("<request_backtoonline>")) {
					String name = msgLst.get(1);
					doBackToOnline(name);
				} else if(cmd.equals("<logout>")) {
					doLogOut();
				}

			} catch (Exception e) {
				running = false;
				server.removeFromOnlineList(this);
				server.removeFromInGameList(this);
				server.removeOnlineConnectionFromRequestLst(this);
				server.broadcastOnlinePlayer();
				server.broadcastClientRequestLst();
				server.updateServerView();
				name = null;
			}
		}		
	}

	/**
	 * Send message to Client. Called by the Server.
	 * @param msg String message to be send to Client.
	 */
	public void forwardMessage(String msg) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			outBuff = msg.getBytes();
			dos.write(outBuff, 0, msg.length());
			dos.flush();
			System.out.println("ClientConnection sending msg: "+ msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//thread two: read a message from the Client
	/**
	 * Return Client's message
	 * @return String Client's message.
	 */
	public String receiveMessaage() {
		int len;
		String msg = null;
		try {
			len = in.read(inBuff);
			msg = new String(inBuff, 0, len);
	
		} catch (IOException e) {
			return null;
		}
		return msg;
	}

	private void doLogOut() {
		
		server.removeOnlineConnectionFromRequestLst(this);
		server.removeFromOnlineList(this);
		server.updateServerView();
		
		server.broadcastOnlinePlayer();
		server.broadcastClientRequestLst();
		
		
	}

	private void doRemoveFromInGame(String playerName) {
		server.removeFromInGameList(this);
		server.updateServerView();
		server.sendUpdateToOnlineConnection();
		System.out.println("ClientConnection, doRemoveFromInGame: check? ");
		
	}

	private void doBackToOnline(String id) {
		doResetRequestLst();
		server.removeFromInGameList(this);
		server.addOnlinePlayer(this);
		String stat = server.getStat(id);
		server.broadcast(this, "<player_stat> " + stat);
		server.broadcastOnlinePlayer();
		server.broadcastClientRequestLst();
		server.updateServerView();	
	}
	
	private void doResetRequestLst() {
		for(int i=0; i<requestLst.size(); i++) {
			requestLst.remove(i);
		}
		
	}

	/**
	 * @param stat 
	 * @param id 
	 * 
	 */
	private void doAfterGame(String id, String stat) {
		if(server.checkId(id)) {
			server.updatePlayerGameStat(id, stat);
		}
	}

	/**
	 * 
	 */
	private void doAnonymous() {
		Random rand = new Random();
		int randInt = rand.nextInt(100) + 1;
		this.name = "anonymous" + randInt;
		boolean addPlayer = server.addOnlinePlayer(this);
		while ( addPlayer == false) {
			randInt = rand.nextInt(100) + 1;
			this.name = "anonymous" + randInt;
			addPlayer = server.addOnlinePlayer(this);
		}
		String stat = server.getStat(this.name);
		server.broadcast(this, "<anonymous_okay> " + this.name + 
				" " + stat);
		server.updateServerView();
		server.broadcastOnlinePlayer();


	}

	private void doLogin(String id, String pwd) {
		if( server.checkId(id) ) { //check existing id
			if(server.checkPassword(id ,pwd)) { //check password according to id
				if(server.isPlayerOnline(id)) {
					server.broadcast(this, "<already_online> " + id);
				} else {
					name = id;
					String stat = server.getStat(this.name);
					server.addOnlinePlayer(this);
					server.broadcast(this, "<login_okay> " + name + " " + stat);
					server.updateServerView();
					server.broadcastOnlinePlayer();
				}
			} else {
				server.broadcast(this, "<login_fail>");
			}
		} else {
			server.broadcast(this, "<login_fail>");
		}
	}

	private void doInGame(String user) {
		if(server.isPlayerOnline(user)) {
			server.removeOnlineConnectionFromRequestLst(this);
			server.moveConnectionToInGameLst(user);
			server.sendUpdateToOnlineConnection();
			server.updateServerView();
		}

	}

	private void sendBackPeerServerInfo(String user1, String ip, String port) {
		if(server.isPlayerOnline(user1)) {
			String msg = "<accept_from>" + " " + ip + " " + port + " " + this.name;
			server.getOnlineConnectionOf(user1).forwardMessage(msg);;
		}

	}

	private void doRegister(String id, String pwd) {
		if(server.checkId(id)) {
			server.broadcast(this, "<account_exist>");
		}
		else {
			name = id;
			server.addAccount(id, pwd);
			String stat = server.getStat(this.name);
			server.broadcast(this, "<register_okay> " + name + " " + stat);	
			server.addOnlinePlayer(this);
			server.updateServerView();
			server.broadcastOnlinePlayer();
		}
	}

	private void doRequestTo(String user2) {
		ClientConnection cc2;
		if (server.isPlayerOnline(user2)) {
			cc2 = server.getUserConnection(user2);
			if(!cc2.requestLstHasPlayer(this.name))
				cc2.addPlayerToRequestList(this.name);

			String allRequestPlayer = "";
			for(int i=0; i<cc2.getRequestLst().size(); i++) {
				allRequestPlayer += cc2.getRequestLst().get(i) + " ";
			}
			server.broadcast(cc2, "<request_from> " + allRequestPlayer);
		} else {
			server.broadcast(this, "<player_offline> " + user2);
		}
	}

	private void doRemoveRequestFrom(String user2) {
		for(int i=0; i<requestLst.size(); i++) {
			if(requestLst.get(i).equals(user2)) {
				requestLst.remove(i);
			}
		}
		String allRequestPlayer = "";
		for(int i=0; i<requestLst.size(); i++) {
			allRequestPlayer += requestLst.get(i) + " ";
		}
		server.broadcast(this, "<request_from> " + allRequestPlayer );
	}

	private void addPlayerToRequestList(String user1) {
		this.requestLst.add(user1);
	}

	private boolean requestLstHasPlayer(String user1) {
		for(int i=0; i<requestLst.size(); i++) {
			if(requestLst.get(i).equals(user1)) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<String> getRequestLst() {
		return this.requestLst;
	}

	private void doGameResult(String playerName, String result) {
		server.updatePlayerGameStat(playerName, result);
	}

	public void removeFromRequestLst(String user) {
		for(int i=0; i<requestLst.size(); i++) {
			if (requestLst.get(i).equals(user)) {
				requestLst.remove(i);
			}
		}
	}

	public Socket getSocket() {
		return this.sock;
	}

	public String getPlayerName() {
		return name;
	}

	public String getRequestPlayer() {
		String players = "";
		for(int i=0; i<requestLst.size(); i++) {
			players += requestLst.get(i) + " ";
		}
		return players;
	}

}

