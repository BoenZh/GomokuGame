/**
 * LobbyView panel is the next step after the authentication succeed from the StartUpView. 
 * In this view, there are list of online player(s) and list of request from other players.
 * From the online player list, user will be able to select any online player one at a time to request to play game with.
 * From the request list, user will be able to select a request from other player and then either click "Accept" to play the game with, or "Decline" to decline game request.
 */

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import org.w3c.dom.ls.LSInput;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JList;
import java.awt.Scrollbar;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTextArea;
import java.awt.Color;

public class LobbyView extends JPanel {
	private DefaultListModel<String> listModel_onlinePlayerlst;
	private DefaultListModel<String> listModel_requestedPlayer;
	private ClientController cController;
	private String selectPlayer;
	private JList<String> list_onlinePlayer;
	private JButton btnRequest;
	private JButton btnAccept;
	private JLabel lblYourId;
	private JTextArea textArea_id;
	private JLabel lblOnlinePlayer;
	private JLabel lblRequestFrom;
	private JList<String> list_requestedFrom;
	private JButton btnDecline;
	private JLabel lblPlayerstat;
	private JButton btn_logOut;

	/**
	 * Create the panel.
	 */
	public LobbyView() {
		setLayout(null);

		textArea_id = new JTextArea();
		textArea_id.setEditable(false);
		textArea_id.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_id.setBounds(90, 11, 269, 26);
		add(textArea_id);

		listModel_onlinePlayerlst = new DefaultListModel<String>();
		list_onlinePlayer = new JList<String>(listModel_onlinePlayerlst);
		list_onlinePlayer.addListSelectionListener(new List_onlinePlayerSelectionListener());
		list_onlinePlayer.setBounds(20, 83, 405, 428);
		list_onlinePlayer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_onlinePlayer.setSelectedIndex(0);
		add(list_onlinePlayer);

		btnRequest = new JButton("Request");
		btnRequest.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnRequest.addActionListener(new BtnRequestActionListener());
		btnRequest.setBounds(20, 522, 100, 30);
		btnRequest.setEnabled(false);
		add(btnRequest);

		listModel_requestedPlayer = new DefaultListModel<String>();
		list_requestedFrom = new JList<String>(listModel_requestedPlayer);
		list_requestedFrom.addListSelectionListener(new List_requestedPlayerSelectionListener());
		list_requestedFrom.setBounds(435, 83, 236, 251);
		add(list_requestedFrom);


		btnAccept = new JButton("Accept");
		btnAccept.setEnabled(false);
		btnAccept.addActionListener(new BtnAcceptActionListener());
		btnAccept.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnAccept.setBounds(435, 345, 89, 30);
		btnAccept.setEnabled(false);
		add(btnAccept);

		lblYourId = new JLabel("Your ID:");
		lblYourId.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblYourId.setBounds(20, 11, 70, 20);
		add(lblYourId);
		
		lblPlayerstat = new JLabel("");
		lblPlayerstat.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPlayerstat.setBounds(435, 11, 236, 26);
		add(lblPlayerstat);

		lblOnlinePlayer = new JLabel("Online Player");
		lblOnlinePlayer.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOnlinePlayer.setBounds(20, 58, 111, 14);
		add(lblOnlinePlayer);

		lblRequestFrom = new JLabel("Request from:");
		lblRequestFrom.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblRequestFrom.setBounds(437, 55, 100, 20);
		add(lblRequestFrom);

		btnDecline = new JButton("Decline");
		btnDecline.addActionListener(new BtnDeclineActionListener());
		btnDecline.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnDecline.setBounds(582, 345, 89, 30);
		btnDecline.setEnabled(false);
		add(btnDecline);
		
		btn_logOut = new JButton("Log Out");
		btn_logOut.addActionListener(new Btn_logOutActionListener());
		btn_logOut.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_logOut.setBounds(560, 522, 111, 30);
		add(btn_logOut);

	}

	/**
	 * Action Listener for online player list.
	 * Upon selection, "Request" button is set to visible.
	 *
	 */
	private class List_onlinePlayerSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			btnRequest.setEnabled(true);

		}
	}

	/**
	 * Action Listener for requested player list.
	 * Upon selection, "Accept" and "Decline" button are set to visible.
	 * 
	 */
	private class List_requestedPlayerSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent arg0) {
			btnAccept.setEnabled(true);
			btnDecline.setEnabled(true);
		}
	}

	/**
	 * Action Listener for "Request" button.
	 * It selects the player name from the request list and then call ClientController object to sendRequestTo(selectPlayer).
	 *
	 */
	private class BtnRequestActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int index = list_onlinePlayer.getSelectedIndex();
			selectPlayer = (String) listModel_onlinePlayerlst.getElementAt(index);
			list_onlinePlayer.clearSelection();
			btnRequest.setEnabled(false);
			cController.sendRequestTo(selectPlayer);	
		}
	}

	/**
	 * Action Listener for "Accept" button.
	 * It get selected user name, then call ClientController object to sendAccepTo(selectPlayer).
	 * At the same time, it calls ClientController to setUpPeerServer(), and connect to it.
	 *
	 */
	private class BtnAcceptActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int index = list_requestedFrom.getSelectedIndex();
			selectPlayer = (String) listModel_requestedPlayer.getElementAt(index);
			
			cController.setUpPeerServer();
			cController.sendAcceptTo(selectPlayer);
			
			list_requestedFrom.clearSelection();
			btnAccept.setEnabled(false);
			btnDecline.setEnabled(false);
		}
	}

	/**
	 * Action listener for "Decline" button. 
	 * It get the selected user name and call Client Controller object to do sendDecLine(selectplayer).
	 *
	 */
	private class BtnDeclineActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int index = list_requestedFrom.getSelectedIndex();
			selectPlayer = (String) listModel_requestedPlayer.getElementAt(index);
			list_requestedFrom.clearSelection();
			btnDecline.setEnabled(false);
			btnAccept.setEnabled(false);
			cController.sendDeclineTo(selectPlayer);
		}
	}
	private class Btn_logOutActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			cController.doLogOut();
		}
	}

	/**
	 * Initialize ClientController object.
	 * @param cc ClientController object.
	 */
	public void access(ClientController cc) {
		cController = cc;
	}

	/**
	 * Displayer user name in the view, on top of the request player list.
	 * @param name String user name
	 */
	public void setUsernameLabel(String name) {
		textArea_id.setText(name);

	}

	/**
	 * Update the online player list. When list is empty, "Request" button will be unable to click.
	 * @param lst ArrayList<String> list containing all the online players.
	 * @param currentUser String the user himself, which be excluded from the list in order to avoid showing himself on the online player list.
	 */
	public void updateOnlinePlayerList(ArrayList<String> lst, String currentUser) {
		listModel_onlinePlayerlst.removeAllElements();
		for(int i=0; i<lst.size(); i++) {
			if(lst.get(i).equals(currentUser) == false)
				listModel_onlinePlayerlst.addElement(lst.get(i));
		}
		if(listModel_onlinePlayerlst.size() <= 0 ) {
			btnRequest.setEnabled(false);
		}
	}

	/**
	 * Update the requested list to show all the players that requested to the user. When the list is empty, "Accept" and "Decline" button will be unable to click.
	 * @param requestLst ArrayList<String> list of requested player to the user.
	 */
	public void updateRequestList(ArrayList<String> requestLst) {
		if(requestLst.size() <= 0) {
			btnAccept.setEnabled(false);
			btnDecline.setEnabled(false);
		}
		listModel_requestedPlayer.removeAllElements();
		for(int i=0; i<requestLst.size(); i++) {
			listModel_requestedPlayer.addElement(requestLst.get(i));
		}
	}

	public void setPlayerStatTextField(String statString) {
		lblPlayerstat.setText(statString);
		
	}
}
