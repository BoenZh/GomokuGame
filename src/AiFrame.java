import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AiFrame extends JFrame {
	private ViewController viewController;
	private int aiLevel;
	private JPanel contentPane;


	/**
	 * Create the frame.
	 * @param vc 
	 * @param gameOption 
	 */
	public AiFrame(int i, ViewController vc) {
		viewController = vc;
		initialize();
		AIGameView a=new AIGameView(i, this);
		getContentPane().add(a);

	}

	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 746, 611);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	public void switchToMainMenuFrame() {
		viewController.switchToStartUpView(this);

	}

}
