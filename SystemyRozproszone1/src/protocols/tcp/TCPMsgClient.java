package protocols.tcp;

import messenger.TCPMsgController;
import utils.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * the client that can be run both as a console or a gui
 */
public class TCPMsgClient {
	private TCPMsgController gui;
	private ObjectInputStream inputStream;
	/**
	 * a Thread that waits for the message from the server and append them to the
	 * gui (if exists) or in console
	 */
	private Thread listenFromServer = new Thread() {
		@Override
		public void run() {
			while (true) {
				try {
					Message msg = (Message) inputStream.readObject();
					display(msg);
					sleep(100);
				} catch (IOException | ClassNotFoundException |
						InterruptedException e) {
					display("Server zamknął połączenie: ", e);
					break;
				}
			}
		}
	};
	private String nick;
	private ObjectOutputStream outputStream;
	private int port;
	private String server;
	private Socket socket;

	/**
	 * Called when used from a gui
	 *
	 * @param gui
	 * @param nick
	 * @param port
	 * @param server
	 */
	public TCPMsgClient(TCPMsgController gui, String nick, int port,
	                    String server) {
		this.nick = nick;
		this.port = port;
		this.server = server;
		this.gui = gui;
	}

	/**
	 * When something goes wrong close IO streams and disconnect.
	 */
	private void disconnect() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {}
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {}
		}

		if (gui != null) {
			gui.setConnected(false);
		}
	}

	/**
	 * displays message in gui or console
	 *
	 * @param msg
	 */
	private void display(Message msg) {
		gui.getPanel().append(msg);
	}

	/**
	 * displays message in gui or console; this one is actually for messages from
	 * the SYSTEM
	 *
	 * @param msg
	 * @param e
	 */
	private void display(String msg, Exception e) {
		Message message = new Message(msg + e, "SYSTEM", Message.MESSAGE);
		display(message);
	}

	public String getNick() {
		return nick;
	}

	/**
	 * to send message to server
	 *
	 * @param message
	 */
	public void sendMessage(Message message) {
		try {
			outputStream.writeObject(message);
		} catch (IOException e) {
			display("Error while sending a message to server: ", e);
		}
	}

	public boolean start() {
		try {
			socket = new Socket(server, port);
		} catch (IOException e) {
			display(new Message("Error connection to server: " + e, "SYSTEM",
			                    Message.MESSAGE));
			return false;
		}
		display(new Message(
				"Nawiązano połączenie " + socket.getInetAddress() + ":" +
				socket.getPort(), "SYSTEM", Message.MESSAGE));
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			display("Error while creating IO streams: ", e);
			return false;
		}
		listenFromServer.start();

		try {
			outputStream.writeObject(nick);
		} catch (IOException e) {
			display("Error sending your nick: ", e);
			disconnect();
			return false;
		}
		return true;
	}
}
