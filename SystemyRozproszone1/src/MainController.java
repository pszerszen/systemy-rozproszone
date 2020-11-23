import messenger.TCPMsgController;
import messenger.TCPMsgServerController;
import messenger.UDPMsgController;
import messenger.UDPServerGUI;
import tanks.TCPTanksController;
import tanks.TCPTanksServerController;
import tanks.UDPTanksController;
import tanks.UDPTanksServerController;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainController extends JFrame implements ActionListener {
	private static final long serialVersionUID = 79790521982737735L;
	private int choice;
	private CustomizerPanel panel;
	private boolean TCP, start;

	public MainController() {
		super("Wybór Aplikacji");
		panel = new CustomizerPanel();
		panel.addActionListener(this);
		add(panel);
		setContentPane(panel);
		setSize(270, 290);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Executes adequate application depending on special number {@link #choice} .
	 * <br> First digit says if it's Communicator or Tanks, <br> second defines
	 * used protocol (TCP/UDP), <br> third if we're setting up a Server or
	 * using an
	 * application as a Client.
	 */
	private void execute() {
		try {
			switch (choice) {
				case 111:
					new TCPMsgServerController(60666);
					break;
				case 112:
					new TCPMsgController(panel.getTextFieldHost().getText(),
					                     60666,
					                     panel.getTextFieldName().getText());
					break;
				case 121:
					new UDPServerGUI(60667);
					break;
				case 122:
					new UDPMsgController(panel.getTextFieldName().getText(),
					                     panel.getTextFieldHost().getText(),
					                     60668);
					break;
				case 211:
					new TCPTanksServerController(61666);
					break;
				case 212:
					new TCPTanksController(panel.getTextFieldHost().getText(),
					                       61666,
					                       panel.getTextFieldName().getText());
					break;
				case 221:
					new UDPTanksServerController(61667);
					break;
				case 222:
					new UDPTanksController(61668,
					                       panel.getTextFieldHost().getText(),
					                       panel.getTextFieldName().getText());
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates special number ({@link #choice}) and
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Launch")) {
			int[] app = new int[3];
			app[0] = (panel.getComboBoxApp().getSelectedIndex() + 1) * 100;
			TCP = panel.getBtnGroupProtocol().getSelection().getActionCommand()
					.equals("TCP");
			start = panel.getBtnGroupStart().getSelection().getActionCommand()
					.equals("Start");
			app[1] = TCP ? 10 : 20;
			app[2] = start ? 1 : 2;
			choice = app[0] + app[1] + app[2];
			setVisible(false);
			execute();
		}
	}

	public static void main(String[] args) {
		new MainController();
	}
}
