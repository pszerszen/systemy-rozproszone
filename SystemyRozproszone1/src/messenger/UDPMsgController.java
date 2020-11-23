package messenger;

import protocols.udp.UDPMsgClient;
import utils.Message;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class UDPMsgController extends JFrame
		implements ActionListener, KeyListener, WindowListener {

	private static final long serialVersionUID = -3484481727238616734L;
	private UDPMsgClient client;
	private boolean connected;
	private MsgPanel panel;

	public UDPMsgController(String nick, String host, int port)
			throws IOException {
		super("Komunikator (" + nick + ")");
		setSize(550, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(this);
		panel = new MsgPanel();
		panel.addListeners(this, this);
		client = new UDPMsgClient(nick, host, this, port);
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
				try {
					Message m = new Message(panel.getMessage().getText(),
					                        client.getNick(), Message.MESSAGE);
					client.sendMessage(m);
				} catch (IOException e1) {}
				panel.getMessage().setText("");
			}
		}
	}

	public MsgPanel getPanel() {
		return panel;
	}

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
			e.consume();
			actionPerformed(new ActionEvent(panel.getMessage(), 2, "send"));
		}
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			client.disconnect();
			client.sendMessage(new Message("logout", client.getNick(),
			                               Message.LOGOUT));
		} catch (IOException e) {}
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
}
