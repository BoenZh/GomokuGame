/**
 * ServerView is the view that list all the online players. View can start and stop the Server.
 */

import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

public class ServerView {
	private Server server;
	private JFrame frmServerviewpanel;
	private JLabel lblConnectionTraffic;
	private JTextArea textArea_ConnectionTraffic;
	private JLabel lblActiveConnections;
	private JButton btnStartServer;
	private JScrollPane scrollPane;
	private JTextArea textArea_NumConnection;
	private JButton btnStop;
	private JLabel lblipLabel;
	private JTextField textField_serverIp;

	/**
	 * Create the application.
	 */
	public ServerView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmServerviewpanel = new JFrame();
		frmServerviewpanel.setTitle("ServerViewPanel");
		frmServerviewpanel.setBounds(100, 100, 450, 300);
		frmServerviewpanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmServerviewpanel.getContentPane().setLayout(null);
		
		lblConnectionTraffic = new JLabel("Connection Traffic");
		lblConnectionTraffic.setBounds(10, 11, 134, 14);
		frmServerviewpanel.getContentPane().add(lblConnectionTraffic);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 29, 276, 196);
		frmServerviewpanel.getContentPane().add(scrollPane);
		
		textArea_ConnectionTraffic = new JTextArea();
		scrollPane.setViewportView(textArea_ConnectionTraffic);
		textArea_ConnectionTraffic.setLineWrap(true);
		textArea_ConnectionTraffic.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textArea_ConnectionTraffic.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		lblActiveConnections = new JLabel("Active Connections");
		lblActiveConnections.setBounds(10, 236, 96, 14);
		frmServerviewpanel.getContentPane().add(lblActiveConnections);
		
		btnStartServer = new JButton("Start");
		btnStartServer.addActionListener(new BtnStartServerActionListener());
		btnStartServer.setBounds(296, 167, 108, 23);
		frmServerviewpanel.getContentPane().add(btnStartServer);
		
		textArea_NumConnection = new JTextArea();
		textArea_NumConnection.setEditable(false);
		textArea_NumConnection.setBounds(116, 235, 74, 17);
		frmServerviewpanel.getContentPane().add(textArea_NumConnection);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new BtnStopActionListener());
		btnStop.setBounds(296, 202, 108, 23);
		btnStop.setEnabled(false);
		frmServerviewpanel.getContentPane().add(btnStop);
		
		lblipLabel = new JLabel("Server IP:");
		lblipLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblipLabel.setBounds(296, 35, 128, 14);
		frmServerviewpanel.getContentPane().add(lblipLabel);
		
		textField_serverIp = new JTextField();
		textField_serverIp.setHorizontalAlignment(SwingConstants.LEFT);
		textField_serverIp.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField_serverIp.setEditable(false);
		textField_serverIp.setBackground(Color.white);
		textField_serverIp.setBounds(296, 60, 128, 31);
		frmServerviewpanel.getContentPane().add(textField_serverIp);
		textField_serverIp.setColumns(10);
		
		
	}
	
	/**
	 * Set JFrame to visible.
	 */
	public void setFrameVisible() {
		this.frmServerviewpanel.setVisible(true);
	}
	
	/**
	 * Start the server to listen for message from client.
	 *
	 */
	private class BtnStartServerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			server.startServer();
			updateConnectionField();		
			btnStartServer.setEnabled(false);
			btnStop.setEnabled(true);
		}
	}
	private class BtnStopActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			server.exportAccountDatabase();
			server.stopServer();
			System.exit(0);
			btnStop.setEnabled(false);
		}
	}

	/**
	 * Get access to the Server object.
	 * @param server the Server object.
	 */
	public void access(Server server) {
		this.server = server;
		
	}


	/**
	 * Update IP addresses and number of connection currently connecting to the server.
	 */
	public void updateConnectionField() {
		textArea_ConnectionTraffic.setText("");
		
		 ArrayList<ClientConnection> onlineList;
		 onlineList = server.getOnlineClientList();
		 String client = "";
		 for( int i=0; i < onlineList.size(); i++) {
			 client += onlineList.get(i).getPlayerName() + " ";
			 InetAddress addr = onlineList.get(i).getSocket().getInetAddress();
			 String hostAddr = addr.getHostAddress();
			 client += hostAddr + " Online\n";
		 }
//		 textArea_ConnectionTraffic.setText(client);
//		 textArea_NumConnection.setText("" + onlineList.size());
		 
		 ArrayList<ClientConnection> ingameList;
		 ingameList = server.getIngameList();
		
		 for( int i=0; i < ingameList.size(); i++) {
			 client += ingameList.get(i).getPlayerName() + " ";
			 InetAddress addr = ingameList.get(i).getSocket().getInetAddress();
			 String hostAddr = addr.getHostAddress();
			 client += hostAddr + " In-Game\n";
		 }
		 textArea_ConnectionTraffic.setText(client);
		 int numConnection = ingameList.size() + onlineList.size();
		 textArea_NumConnection.setText("" + numConnection);
		 
		 
	}

	public void showIp(String serverIp) {
		textField_serverIp.setText("" + serverIp);
		
	}
}
