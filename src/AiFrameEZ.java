import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AiFrameEZ extends JFrame {
	private ViewController viewController;
	private int aiLevel;

	private JPanel contentPane;


	/**
	 * Create the frame.
	 * @param viewController2 
	 * @param gameOption 
	 */
	public AiFrameEZ(int i, ViewController vc) {
		this.viewController = vc;
		initialize();
		AiGameViewEZ a=new AiGameViewEZ(i, this);
		getContentPane().add(a);
		
	}

	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 738, 635);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	public void switchToMainMenuFrame() {
		viewController.switchToStartUpView(this);
		
	}

}