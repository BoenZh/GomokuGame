/**
 * Server has the functionality such as:
 * - When started, it read in a text file database of the existing account and store them.
 * - Verify user credential.
 * - Check user status (online, offline, in-game)
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.jws.Oneway;

public class Server implements Runnable {
	private ServerSocket serverSock;
	private String serverIp;
	private ServerView gui;
	private HashMap<String, ArrayList<String>> accountMap;
	private ArrayList<ClientConnection> clientList;
	private ArrayList<ClientConnection> onlineList;
	private ArrayList<ClientConnection> inGameList;
	private Thread worker;
	private boolean runServer;


	/**
	 * Initialize Server , ArrayList, HashMap, and read in a text file.
	 * Text file of account database will be store in HashMap with user name as the key
	 * and password as his the key's object.
	 */
	public Server() {
		clientList = new ArrayList<ClientConnection>();
		onlineList = new ArrayList<ClientConnection>();
		inGameList = new ArrayList<ClientConnection>();
		accountMap = new HashMap<String, ArrayList<String>>();

		try {
			serverSock = new ServerSocket(12345);
			InetAddress addr = InetAddress.getLocalHost();
			serverIp = addr.getHostAddress() +"";
			System.out.println("Server Ip: " + serverIp);

			Scanner fScan = new Scanner ( new File("AccountDatabase.txt"));
			while(fScan.hasNext()) {
				String line = fScan.nextLine();
				ArrayList<String> partLst = new ArrayList<String>(Arrays.asList(line.trim().split(" ")));
				String id = partLst.get(0);
				partLst.remove(0);
				accountMap.put(id, partLst);
			}
			fScan.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Creating server fails.");
		}
		runServer = true;
	}

	/**
	 * Accepting client connection and then create a new ClientConnection object.
	 * Adding ClientConnection to the arrayList.
	 * start run() in ClientConnection object
	 */
	public void run() {
		while(runServer) {
			try {
				Socket clientSock = serverSock.accept();
				if(runServer == false)
					break;
				ClientConnection c = new ClientConnection(clientSock, this);
				clientList.add(c);
				c.start();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}

	/**
	 * Creating the second Thread and start().
	 */
	public void listen() {
		worker = new Thread(this);
		worker.start();
	}

	/**
	 * Get access to ServerView object.
	 * @param gui2 
	 */
	public void access(ServerView gui2) {
		this.gui = gui2;
		gui.showIp(serverIp);
	}

	/**
	 * Add account to the HashMap of account database.
	 * @param acc String user name.
	 * @param pwd String password.
	 */
	public void addAccount(String acc, String pwd) {
		ArrayList<String> dataLst = new ArrayList<String>();
		dataLst.add(pwd);
		for(int i=0; i<3; i++) {
			dataLst.add("0");
		}
		accountMap.put(acc, dataLst);
	}

	/**
	 * Write the account database into a text file.
	 */
	public void exportAccountDatabase() {
		PrintWriter writer;
		Iterator<String> accIter = accountMap.keySet().iterator(); 
		try {
			writer = new PrintWriter("AccountDatabase.txt");
			while(accIter.hasNext()) {
				String id = accIter.next();
				String pwd = accountMap.get(id).get(0);
				String played = accountMap.get(id).get(1);
				String won = accountMap.get(id).get(2);
				String tied = accountMap.get(id).get(3);
				writer.println(id + " " + pwd + " " + played + " "
						+ won + " " + tied);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get array list of ClientConnection object.
	 * @return ArrayList<ClientConnection> list of ClientConnection. 
	 */
	public ArrayList<ClientConnection> getClientList() {
		return clientList;
	}

	public ClientConnection getUserConnection(String user) {
		for(int i=0; i<onlineList.size(); i++) {
			if(onlineList.get(i).getPlayerName().equals(user)) {
				return onlineList.get(i);
			}
		}
		return null;
	}

	public ArrayList<ClientConnection> getOnlineClientList() {
		return onlineList;
	}

	/**
	 * Stop the accepting client socket.
	 */
	public void stopServer() {
		runServer = false;
		try {
			serverSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Start Server thread, thread that handle incoming client socket.
	 */
	public void startServer() {
		listen();
	}

	/**
	 * Verify user name with the account database.
	 * @param msg String user name to be verified
	 * @return true if the user name exists in the database, 
	 * 				else return false.
	 */
	public boolean checkId(String msg) {
		Iterator<String> accIter = accountMap.keySet().iterator(); 
		while(accIter.hasNext()) {
			String id = accIter.next();
			if(id.equals(msg)) {
				return true;	
			}
		}
		return false;
	}

	/**
	 * Verify input id and password to the server database.
	 * @param id String input user name.
	 * @param pwd String input password
	 * @return true if the inputs match with the database,
	 * 			else return false.
	 */
	public boolean checkPassword(String id, String pwd) {
		String correctPwd = accountMap.get(id).get(0);
		if(correctPwd.equals(pwd))
			return true;
		return false;
	}

	/**
	 * Update ClientConnetion object in the ServerView.
	 */
	public void updateServerView() {
		gui.updateConnectionField();

	}

	public boolean isPlayerOnline(String id) {
		for(int i=0; i<onlineList.size(); i++) {
			if(onlineList.get(i).getPlayerName().equals(id))
				return true;
		}
		return false;
	}

	public boolean addOnlinePlayer(ClientConnection cc) {
		String temp = cc.getPlayerName();
		boolean online = false;
		for(int i=0; i<onlineList.size(); i++) {
			if(temp.equals(onlineList.get(i).getPlayerName()))
				online = true;
		}
		if(online == false) {
			onlineList.add(cc);
			return true;
		}

		return false;

	}

	public void removeFromOnlineList(ClientConnection cc) {
		for(int i=0; i < onlineList.size(); i++) {
			ClientConnection temp = onlineList.get(i);
			if(temp == cc) {
				onlineList.remove(i);
			}
		}
	}

	/**
	 * 
	 * @param clientConnection
	 */
	public void removeFromInGameList(ClientConnection cc) {

		for(int i=0; i < inGameList.size(); i++) {
			ClientConnection temp = inGameList.get(i);
			if(temp == cc) {
				inGameList.remove(i);
			}
		}

	}

	public void removeOnlineConnectionFromRequestLst(ClientConnection cc) {
		String removeUser = cc.getPlayerName();
		for(int i=0; i<onlineList.size(); i++) {
			onlineList.get(i).removeFromRequestLst(removeUser);
		}

	}

	/**
	 * Send message to the Client.
	 * @param cc ClientConnection object.
	 * @param msg String message to be sent.
	 */
	public void broadcast(ClientConnection cc, String msg) {
		cc.forwardMessage(msg);
	}

	/**
	 */
	public void broadcastOnlinePlayer() {
		String players = "";
		for(int i=0; i<onlineList.size(); i++) {
			if(i == onlineList.size()-1)
				players +=  onlineList.get(i).getPlayerName();
			else
				players += onlineList.get(i).getPlayerName() + " ";		
		}

		for(int i=0; i<onlineList.size(); i++) {
			ClientConnection c = onlineList.get(i);
			broadcast(c, "<online_player> " + players);		
		}
	}

	/**
	 */
	public void broadcastClientRequestLst() {
		for(int i=0; i<onlineList.size(); i++) {
			ClientConnection cc = onlineList.get(i);
			String requestPlayer = cc.getRequestPlayer();
			broadcast(cc, "<update_requestLst> " + requestPlayer);
		}

	}

	/**
	 * @param user1
	 * @return
	 */
	public ClientConnection getOnlineConnectionOf(String user1) {
		for(int i=0; i<onlineList.size(); i++) {
			if(onlineList.get(i).getPlayerName().equals(user1))
				return onlineList.get(i);
		}
		return null;

	}

	/**
	 * @param user
	 */
	public void moveConnectionToInGameLst(String user) {
		ClientConnection cc = getOnlineConnectionOf(user);
		inGameList.add(cc);
		removeFromOnlineList(cc);

	}

	/** 
	 */
	public void sendUpdateToOnlineConnection() {
		broadcastOnlinePlayer();
		broadcastClientRequestLst();	
	}

	/**
	 * 
	 * @return ArrayList<ClientConection> inGameList
	 */
	public ArrayList<ClientConnection> getIngameList() {
		return inGameList;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public String getStat(String id) {
		String playerStat = "";

		if(checkId(id)){
			ArrayList<String> stat = accountMap.get(id);
			//skip password by setting i=1
			for(int i=1; i<stat.size(); i++) {
				playerStat += stat.get(i) + " ";
			}
			return playerStat;
		}
		
		return "n/a n/a n/a";
	}

	/**
	 * 
	 * @param id
	 * @param stat
	 */
	public void updatePlayerGameStat(String id, String stat) {
		ArrayList<String> oldStat = accountMap.get(id);
		ArrayList<String> newStat = new ArrayList<String>();
		newStat.add(oldStat.get(0)); //add pwd
		if(stat.equals("won")) {
			Integer.parseInt(oldStat.get(1));
			newStat.add(Integer.parseInt(oldStat.get(1)) + 1 + ""); //+1 to played
			newStat.add(Integer.parseInt(oldStat.get(2)) + 1 + ""); //+1 to won
			newStat.add(Integer.parseInt(oldStat.get(3)) + ""); //same tied
		} else if (stat.equals("loss")) {
			newStat.add(Integer.parseInt(oldStat.get(1)) + 1 + ""); //+1 to played
			newStat.add(Integer.parseInt(oldStat.get(2)) + ""); //same won
			newStat.add(Integer.parseInt(oldStat.get(3)) + ""); // same tied
		}else if (stat.equals("tied")) {
			newStat.add(Integer.parseInt(oldStat.get(1)) + 1 + ""); //+1 to played
			newStat.add(Integer.parseInt(oldStat.get(2)) + ""); //same won
			newStat.add(Integer.parseInt(oldStat.get(3)) + 1 + ""); //+1 to tied	
		}

		accountMap.remove(id);
		accountMap.put(id, newStat);
	}





}
