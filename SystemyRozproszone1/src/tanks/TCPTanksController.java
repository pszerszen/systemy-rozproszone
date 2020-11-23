package tanks;

import protocols.tcp.TCPTanksClient;
import utils.GameMessage;
import utils.Message;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class TCPTanksController extends AbstractTanksGUI {
	private static final long serialVersionUID = -8373923553957306912L;
	private TCPTanksClient client;
	private boolean connected;

	public TCPTanksController(String host, int port, String nick) {
		super(port, "Tanks 2013 - " + nick, nick);
		stopStart.setVisible(false);
		addKeyListener(this);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"D:\\eclipse\\workspaceJ\\SystemyRozproszone1\\res\\tankn" +
				"" +
				".jpg"));
		client = new TCPTanksClient(this, nick, host, port);
		if (!client.start()) {
			return;
		}
		connected = true;
	}

	@SuppressWarnings(value = "unused")
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * Sends message with a move to server, depending on just pressed key. Only
	 * arrows and space buttons works.
	 *
	 * @param e
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (connected) {
			String move;
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					move = "up";
					break;
				case KeyEvent.VK_DOWN:
					move = "down";
					break;
				case KeyEvent.VK_LEFT:
					move = "left";
					break;
				case KeyEvent.VK_RIGHT:
					move = "right";
					break;
				case KeyEvent.VK_SPACE:
					move = "shoot";
					break;
				default:
					return;
			}
			client.sendMove(move);
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		client.sendMessage(new GameMessage(null, client.getNick(),
		                                   Message.LOGOUT));
		System.exit(0);
	}
}
