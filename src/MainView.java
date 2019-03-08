/**
 * MainView is the main class that initialized the window frame, StartUpView panel, LobbyView panel, ViewController object, and ClientController object.
 * MainView class has the functions including:
 * - Set up the start up view
 * - Set up the lobby view 
 * - Set up the game view
 */

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainView {
	private static ClientController client;
	private static ViewController viewController;
	private static StartUpView sView;
	private static LobbyView lbView;
	private static GameView gameView = null;

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//create JFrame
					MainView window = new MainView();

					//initialize Client Controller object 
					client = new ClientController("152.117.216.74", 12345);

					//ControllerView to access MainView, StartUpView, and LobbyView
					viewController = new ViewController();
					viewController.access(window, sView, lbView);

					//StartUpview to access ViewController and ClientController
					sView.access(viewController);
					sView.access(client);

					//LobbyView to access ViewController
					//lbView.access(viewController);
					lbView.access(client);

					//Add StartUpView to frame
					window.frame.add(sView);
					sView.setVisible(true);

					//set frame to visible
					window.frame.setVisible(true);

					//ClientController to access ViewController
					//ClientController will be listening for msg from server
					client.access(viewController);
					client.doIt();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		sView = new StartUpView();
		lbView = new LobbyView();

		frame = new JFrame();
		frame.setBounds(100, 100, 700, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Adding LobbyView panel to the frame and set it to visible. 
	 */
	public void setupLobbyView() {
		if(gameView != null)
			this.gameView.setVisible(false);
		sView.setVisible(false);
		frame.add(lbView);
		lbView.setVisible(true);
		frame.revalidate();
	}

	/**
	 * Set StartUpView panel to default view and make it visible on the frame.
	 */
	public void setupStartUpView() {
		lbView.setVisible(false);
		if(gameView != null)
			this.gameView.setVisible(false);
		sView.setDefaultView();
		sView.setVisible(true);
	}

	/**
	 * Set LobbyView panel to invisible, then add GameView panel to the frame and set it to visible.
	 * @param gView
	 */
	public void setUpGameView(GameView gView) {
		gameView = gView;
		lbView.setVisible(false);
		frame.add(gView);
		gView.setVisible(true);
		frame.revalidate();

	}

	public JFrame getFrame() {
		return frame;

	}
}
