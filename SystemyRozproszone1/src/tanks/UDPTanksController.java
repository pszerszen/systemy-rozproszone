package tanks;

import protocols.udp.UDPTanksClient;
import utils.GameMessage;
import utils.Message;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;

public class UDPTanksController extends AbstractTanksGUI {
	private static final long serialVersionUID = -632399774363866100L;
	private UDPTanksClient client;
	private boolean connected;

	public UDPTanksController(int port, String host, String nick)
			throws UnknownHostException {
		super(port, "Tanks 2013 - " + nick, nick);
		stopStart.setVisible(false);
		addKeyListener(this);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"D:\\eclipse\\workspaceJ\\SystemyRozproszone1\\res\\tankn" +
				"" +
				".jpg"));

		client = new UDPTanksClient(host, this, nick, port);
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
			try {
				client.sendMove(move);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * @param arg0
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			client.disconnect();
			client.sendMessage(new GameMessage("logout", client.getNick(),
			                                   Message.LOGOUT));
		} catch (IOException e) {}
		System.exit(0);
	}
}
