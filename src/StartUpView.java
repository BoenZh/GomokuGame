/**
 * StartUpView panel shows up fist of all in the game application. This view shows options for user to pick between AI or Player VS Player (PVP). 
 * In AI mode, a drop down of three option is available. They are easy, medium, and hard. User will choose the option and then click Ok to proceed.
 * In PVP mode, a different drop down menu is available. They are "Sign in", "Sign up", and "Anonymous". 
 * After selecting one of the option, if it is "Sign in", user will enter their existing account and click Sign in button.
 * Else if it is the "Sign up", user will then enter their new account info and click Sign up button. 
 * Else if it is the "Anonymous". user will then click OK button to proceed.
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.Style;

import java.awt.Font;

import javax.print.attribute.SetOfIntegerSyntax;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JMenuBar;
import java.awt.List;
import javax.swing.JPasswordField;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class StartUpView extends JPanel {
	private String gameOption;
	private ViewController viewController;
	private ClientController clientController;

	private JLabel lblNewLabel;
	private JLabel lblSelectGameType;
	private JButton btnAi;
	private JButton btnPvp;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JTextField textField_ID;
	private JLabel lblVer;
	private JButton btnSignIn_Up;
	private JLabel lblSelectOption;
	private JComboBox dropDownOption;
	private JPasswordField passwordField;
	private JPasswordField passwordFieldConfirm;
	private JButton btnOptionConfirm;
	private JTextArea textArea_errorMsg;

	/**
	 * Create the panel.
	 */
	public StartUpView() {
		gameOption = null;
		setLayout(null);

		lblNewLabel = new JLabel("GOMOKU ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 38));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 663, 39);
		add(lblNewLabel);

		lblSelectGameType = new JLabel("Select Game type:");
		lblSelectGameType.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblSelectGameType.setBounds(10, 61, 235, 24);
		add(lblSelectGameType);

		btnAi = new JButton("AI");
		btnAi.addActionListener(new BtnAiButtonActionListener());
		btnAi.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnAi.setBounds(169, 86, 187, 53);
		add(btnAi);

		btnPvp = new JButton("PVP");
		btnPvp.addActionListener(new BtnPvpActionListener());
		btnPvp.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnPvp.setBounds(403, 86, 212, 53);
		add(btnPvp);

		dropDownOption = new JComboBox();
		dropDownOption.addActionListener(new DropDownOptionActionListener());
		dropDownOption.setBounds(169,149,243 ,31);
		dropDownOption.setVisible(false);
		add(dropDownOption);

		lblUsername = new JLabel("Username: ");
		lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblUsername.setBounds(20, 210, 89, 24);
		add(lblUsername);

		lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblPassword.setBounds(20, 260, 89, 23);
		add(lblPassword);

		textField_ID = new JTextField();
		textField_ID.setBounds(169, 208, 357, 31);
		textField_ID.setEditable(false);
		add(textField_ID);
		textField_ID.setColumns(10);

		lblVer = new JLabel("Confirm Password:");
		lblVer.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblVer.setBounds(20, 294, 138, 36);
		lblVer.setVisible(false);
		add(lblVer);

		btnSignIn_Up = new JButton("Sign in");
		btnSignIn_Up.addActionListener(new BtnSignIn_UpActionListener());
		btnSignIn_Up.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnSignIn_Up.setBounds(526, 343, 89, 31);
		btnSignIn_Up.setEnabled(false);
		add(btnSignIn_Up);

		lblSelectOption = new JLabel("Select option:");
		lblSelectOption.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblSelectOption.setBounds(10, 150, 99, 23);
		add(lblSelectOption);

		passwordField = new JPasswordField();
		passwordField.setEditable(false);
		passwordField.setBounds(169, 252, 357, 31);
		add(passwordField);

		passwordFieldConfirm = new JPasswordField();
		passwordFieldConfirm.setEditable(false);
		passwordFieldConfirm.setBounds(169, 294, 357, 31);
		passwordFieldConfirm.setVisible(false);
		add(passwordFieldConfirm);

		btnOptionConfirm = new JButton("OK");
		btnOptionConfirm.addActionListener(new BtnOptionConfirmActionListener());
		btnOptionConfirm.setBounds(526, 149, 89, 31);
		btnOptionConfirm.setVisible(false);
		add(btnOptionConfirm);

		textArea_errorMsg = new JTextArea();
		textArea_errorMsg.setLineWrap(true);
		textArea_errorMsg.setFont(new Font("Tahoma", Font.PLAIN, 15));
		textArea_errorMsg.setForeground(Color.RED);
		textArea_errorMsg.setEditable(false);
		textArea_errorMsg.setBackground(null);
		textArea_errorMsg.setVisible(false);
		textArea_errorMsg.setBounds(169, 343, 335, 72);
		add(textArea_errorMsg);

	}

	/**
	 * Action listener for button to confirm one of the following option: Easy, Medium,
	 * Hard, and Anonymous. 
	 * After clicked, a game board panel will open. (For next iteration)
	 */
	private class BtnOptionConfirmActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {		
			if(gameOption.equals("Hard")) {
				viewController.switchToAiFrame(gameOption);
			}
			if(gameOption.equals("Easy"))
				viewController.switchToAiFrameEZ(gameOption);
			if(gameOption.equalsIgnoreCase("Medium"))
				viewController.switchToAiFrameMD(gameOption);
				
				
			if(gameOption.equals("Anonymous")) {
				clientController.sendAnonymousRequest();
			}
		}
	}

	/**
	 * Action listener for AI button. 
	 * After clicked, a drop down menu of level options becomes visible. 
	 */
	private class BtnAiButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			dropDownOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Easy", "Medium", "Hard" }));
			dropDownOption.setVisible(true);		
			dropDownOption.setSelectedIndex(0);
			setFieldDefault();
			btnOptionConfirm.setVisible(true);
		}
	}

	/**
	 * Action listener for PVP button.
	 * After clicked, a drop down menu of choice "Sign in, Sign up, and Anonymous" becomes visible.
	 */
	private class BtnPvpActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dropDownOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {"Sign in", "Sign up", "Anonymous" }));
			dropDownOption.setSelectedIndex(0);
			dropDownOption.setVisible(true);
			btnOptionConfirm.setVisible(false);
			setupSignIpField(true);
		}
	}

	/**
	 * Action listener for drop down menu. 
	 * After selected, the selected option is saved to String gameOption. 
	 */
	private class DropDownOptionActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == dropDownOption) {
				JComboBox cb = (JComboBox)e.getSource();
				String msg = (String)cb.getSelectedItem(); 
				switch(msg) {
				case "Easy" : 
					gameOption = msg;
					
					break;
				case "Medium": 
					gameOption = msg;
					
					break;
				case "Hard":
					gameOption= msg;
					
					break;
				case "Sign in":
					setFieldDefault();
					setupSignIpField(true);
					gameOption = msg;
					break;
				case "Sign up": 
					setFieldDefault();
					setupSignUpField(true);
					String signUpIdRequirement = "Username must be longer than 2 characters."
							+ "\nPassword must be longer than 4 characters.";
					textArea_errorMsg.setText(signUpIdRequirement);
					textArea_errorMsg.setForeground(Color.black);
					gameOption = msg;
					break;
				case "Anonymous": 
					gameOption = msg;
					setFieldDefault();
					btnOptionConfirm.setVisible(true);
					break;
				default: System.out.println("Pickign Option Fails...");
				}

			}
		}
	}

	/**
	 * Action listener for sign in and sign up button.
	 * After clicked, user name and password field are sent to the server through ClientController.
	 */
	private class BtnSignIn_UpActionListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			textArea_errorMsg.setText("");
			textArea_errorMsg.setForeground(Color.red);
			//Sign up 
			if(gameOption.equals("Sign up")) {
				if ( !checkInfoInput() )	{
					textArea_errorMsg.setVisible(true);
					textField_ID.requestFocusInWindow();
					textField_ID.selectAll();
					passwordField.setText("");
					passwordFieldConfirm.setText("");
				} else {
					String userInput = textField_ID.getText() 
							+ " " + new String(passwordField.getPassword());
					clientController.sendRegisterInfo(userInput);
				}
				//sign in 
			} else if (gameOption.equals("Sign in")) {
				if( !checkInfoInput() ) {
					textArea_errorMsg.setVisible(true);
					textField_ID.requestFocusInWindow();
					textField_ID.selectAll();
					passwordField.setText("");
				} else {					
					String userInput = textField_ID.getText() 
							+ " " +  new String ( passwordField.getPassword());
					clientController.sendLoginInfo(userInput);
				} 
			}//else if sign in
		}
	}

	/**
	 * Set default view of StartUpView.
	 */
	public void setDefaultView() {
		setFieldDefault();
		dropDownOption.setVisible(false);
	}

	/**
	 * Set default view of StartUpView with drop down menu visible.
	 */
	private void setFieldDefault() {
		btnOptionConfirm.setVisible(false);
		textField_ID.setText("");
		textField_ID.setEditable(false);
		passwordField.setText("");
		passwordField.setEditable(false);
		lblVer.setVisible(false);
		passwordFieldConfirm.setText("");
		passwordFieldConfirm.setVisible(false);
		btnSignIn_Up.setEnabled(false);
		btnSignIn_Up.setText("Sign in");
		textArea_errorMsg.setText("");
		textArea_errorMsg.setVisible(false);
		textArea_errorMsg.setEditable(false);

	}

	/**
	 * Set user name and password field to default for sign up.
	 * @param bool true to enable the field.
	 */
	private void setupSignUpField(boolean bool) {
		btnOptionConfirm.setVisible(false);
		textField_ID.setEditable(bool);
		passwordField.setEditable(bool);
		lblVer.setVisible(bool);
		passwordFieldConfirm.setVisible(bool);
		passwordFieldConfirm.setEditable(bool);
		btnSignIn_Up.setText("Sign up");
		btnSignIn_Up.setEnabled(bool);

	}

	/**
	 * Set user name and password field to default for sign in.
	 * @param bool true to enable the field.
	 */
	private void setupSignIpField(boolean bool) {
		btnOptionConfirm.setVisible(false);
		textField_ID.setEditable(bool);
		passwordField.setEditable(bool);
		lblVer.setVisible(!bool);
		passwordFieldConfirm.setVisible(!bool);
		btnSignIn_Up.setText("Sign in");
		btnSignIn_Up.setEnabled(bool);

	}

	/**
	 * Return true if the user name and password meet the requirement.
	 * @return true if the user name and password meet the requirement.
	 * 				else return false.
	 */
	private boolean checkInfoInput() {
		String erroMsg = "";
		if(gameOption.equals("Sign up")) {
			if(textField_ID.getText().contains(" "))
				erroMsg += "Username can not contain whitespace. \n";
			if(textField_ID.getText().length() < 3 ) 
				erroMsg += "Username must be longer than 2 characters.\n";
			if(passwordField.getPassword().length == 0 || passwordFieldConfirm.getPassword().length == 0) 
				erroMsg += "Fill in your password.\n";
			if(passwordField.getPassword().length < 5)
				erroMsg += "Password must be longer than 4 characters.\n";
			if(!(Arrays.equals(passwordField.getPassword(), passwordFieldConfirm.getPassword())))
				erroMsg += "Passwords do not match.\n";
		}	else if (gameOption.equals("Sign in")) {
			if(textField_ID.getText().length() < 3) 
				erroMsg += "Incorrect username. \nUsername must be longer than 2 characters.\n";
			if(passwordField.getPassword().length == 0)
				erroMsg += "Enter your password.\n";
		}
		if(erroMsg.equals(""))
			return true;
		else {
			textArea_errorMsg.append(erroMsg);
		}
		return false;
	}

	/**
	 * 
	 */
	public void disablePVPmode() {
		setDefaultView();
		btnPvp.setEnabled(false);
		btnSignIn_Up.setEnabled(false);
		
	}

	/**
	 * Display error message to the text area.
	 * @param errorMsg String error message to be displayed.
	 */
	public void displayError(String errorMsg) {
		textArea_errorMsg.setVisible(true);
		textArea_errorMsg.setText(errorMsg);

	}

	/**
	 * To access ClientController
	 * @param cc ClientController object
	 */
	public void access(ClientController cc) {
		clientController = cc;
	}

	/**
	 * To access ViewController object
	 * @param vc ViewController object
	 */
	public void access(ViewController vc) {
		viewController = vc;
	}

}
