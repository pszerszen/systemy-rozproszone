package messenger;

import protocols.tcp.TCPMsgClient;
import utils.Message;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author Piotr Szerszeń
 */
public class TCPMsgController extends JFrame
		implements ActionListener, KeyListener, WindowListener {
	private static final long serialVersionUID = -663535385837056327L;
	private TCPMsgClient client;
	private boolean connected;
	private MsgPanel panel;

	/**
	 * constructor
	 *
	 * @param host
	 * @param port
	 * @param nick
	 */
	public TCPMsgController(String host, int port, String nick) {
		super("Komunikator (" + nick + ")");
		setSize(550, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(this);
		panel = new MsgPanel();
		panel.addListeners(this, this);
		client = new TCPMsgClient(this, nick, port, host);
		add(panel, BorderLayout.CENTER);
		setVisible(true);
		if (!client.start()) {
			return;
		}
		connected = true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("send")) {
			if (connected) {
				Message m = new Message(panel.getMessage().getText(),
				                        client.getNick(), Message.MESSAGE);
				client.sendMessage(m);
				panel.getMessage().setText("");
			}
		}
	}

	/**
	 * @return {@link #panel} value
	 */
	public MsgPanel getPanel() {
		return panel;
	}

	/**
	 * @return {@link #connected} value
	 */
	@SuppressWarnings(value = "unused")
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			actionPerformed(new ActionEvent(panel.getMessage(), 2, "send"));
		}
	}

	/**
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		client.sendMessage(new Message("", client.getNick(), Message.LOGOUT));
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}
