package protocols.udp;

import messenger.UDPMsgController;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

/**
 * @author Piotr Szerszeń
 */
public class UDPMsgClient {
	private InetAddress address;
	private byte[] buf;
	private UDPMsgController gui;
	private DatagramPacket InMsg, OutMsg;
	private Thread listenFromServer = new Thread() {
		@Override
		public void run() {
			while (true) {
				try {
					Message msg = receive();
					display(msg);
					sleep(100);
				} catch (IOException | InterruptedException e) {
					display("Server zamknal polczenie: ", e);
					break;
				}
			}
		}
	};
	private MulticastSocket msocket;
	private InetAddress multicastAddress;
	private String nick;
	private int port;
	private DatagramSocket socket;

	public UDPMsgClient(String nick, String host, UDPMsgController gui,
	                    int port) throws IOException {
		this.nick = nick;
		this.gui = gui;
		this.port = port;
		address = InetAddress.getByName(host);
		multicastAddress = InetAddress.getByName("230.0.0.2");
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

	/**
	 * closes sockets
	 */
	public void disconnect() {
		try {
			sendMessage(new Message("logout", nick, Message.LOGOUT));
			if (msocket != null) {
				msocket.leaveGroup(multicastAddress);
				msocket.close();
			}
			if (socket != null) {
				socket.close();
			}
			if (gui != null) {
				gui.setConnected(false);
			}
		} catch (IOException e) {}
	}

	/**
	 * @return client's nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * receives message from {@link MulticastSocket} {@link #msocket}
	 *
	 * @return parsed Message object
	 *
	 * @throws IOException
	 */
	public Message receive() throws IOException {
		buf = new byte[1024];
		InMsg = new DatagramPacket(buf, buf.length);
		msocket.receive(InMsg);
		return Message.fromString(new String(InMsg.getData(),
		                                     StandardCharsets.UTF_8));
	}

	/**
	 * sends {@link Message} object
	 *
	 * @param message
	 *
	 * @throws IOException
	 */
	public void sendMessage(Message message) throws IOException {
		byte[] buf = message.toString().getBytes(StandardCharsets.UTF_8);
		OutMsg = new DatagramPacket(buf, buf.length, address, port);
		socket.send(OutMsg);
	}

	public boolean start() {
		try {
			socket = new DatagramSocket();
			msocket = new MulticastSocket(60667);
			msocket.joinGroup(multicastAddress);
		} catch (IOException e) {
			display(new Message("Error connection to server: " + e, "SYSTEM"));
			return false;
		}
		display(new Message(
				"Nawiązano połączenie " + multicastAddress + ":" + 60667,
				"SYSTEM", Message.MESSAGE));

		try {
			sendMessage(new Message("connect", nick, Message.CONNECT));
		} catch (IOException e) {
			display("Error sending your nick: ", e);
			disconnect();
			return false;
		}
		listenFromServer.start();
		return true;
	}
}