/**
 * PeerServer is created when two online users accept to play game together. The server is created locally on one the user device. 
 * PeerServer port is 22333.
 */

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class PeerServer implements Runnable {
	private String serverName;
	private ServerSocket peerServerSock;
	private PeerServerController peerServerCon;
	private ArrayList<PeerServerController> pcList;
	private Thread worker;
	private boolean runPeerS;
	private int port;
	private ClientController clientController;
	private ViewController viewCon;
	private boolean runningServer;

	/**
	 * Constructor
	 * Initialize PeerServer object with port 22333.
	 * @param viewCon 
	 * @param clientController 
	 */
	public PeerServer(String serverName, ClientController clientController, ViewController viewCon) {
		this.serverName = serverName;
		this.peerServerCon = null;
		port = 22333;
		runPeerS = true;
		this.clientController = clientController;
		this.viewCon = viewCon;
		try {
			peerServerSock = new ServerSocket(port);
			
			InetAddress localhost = InetAddress.getLocalHost();
	        System.out.println("PeerServer : " +
	                      (localhost.getHostAddress()).trim());
		} catch (IOException e) {
			e.printStackTrace();
			runPeerS = false;
		}
	}


	/**
	 * Run PeerServer to accept new connection to it.
	 */
	public void run() {
		 runningServer = true;
		while(runningServer) {//runPeerS
			try {
				
				Socket peerClientSock = peerServerSock.accept();
				peerServerCon = new PeerServerController(serverName, peerClientSock, this, clientController, viewCon);
				peerServerCon.start();
				if(peerClientSock != null)
					runningServer = false;
				
			} catch (IOException e) {
				e.printStackTrace();
				runningServer = false;
			}
		}
	}

	/**
	 * Create a new thread to run the PeerServer.
	 */
	public void listen() {
		worker = new Thread(this);
		worker.start();
	}


	/**
	 * Get Server IP address.
	 * @return String Server's IP address.
	 */
	public String getIp() {
		InetAddress addr = peerServerSock.getInetAddress();
		String hostAddr = "";
		try {
			hostAddr = addr.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostAddr;
	}


	/**
	 * Get Server port number.
	 * @return int Server port number.
	 */
	public int getPort() {
		return port;
	}



	public void closeSocket() {
		runningServer = false;
		try {
			this.peerServerSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}




}
