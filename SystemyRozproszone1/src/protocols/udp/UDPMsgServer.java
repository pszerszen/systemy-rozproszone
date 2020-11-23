package protocols.udp;

import messenger.UDPServerGUI;
import utils.DateHandler;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class UDPMsgServer {
	private byte[] buf;
	private UDPServerGUI gui;
	private DatagramPacket in, out;
	private InetAddress multicastAddress;
	private int port;
	private DatagramSocket receiver;
	private DatagramSocket sender;
	private boolean status = true;
	private ArrayList<String> users;

	public UDPMsgServer(int port, UDPServerGUI gui) {
		this.port = port;
		this.gui = gui;
		users = new ArrayList<>();
		try {
			multicastAddress = InetAddress.getByName("230.0.0.2");
			receiver = new DatagramSocket(60668);
			sender = new DatagramSocket();
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the message contained in received packet field ({@link #in}).
	 */
	private void broadcast() {
		Message m = Message.fromString(new String(in.getData(),
		                                          StandardCharsets.UTF_8));
		gui.appendRoom(m);
		try {
			byte[] buf = m.toString().getBytes(StandardCharsets.UTF_8);
			out = new DatagramPacket(buf, buf.length, multicastAddress, port);
			sender.send(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends message from given String. It's for sending messages from SYSTEM
	 *
	 * @param msg
	 * 		SYSTEM's message
	 */
	private void broadcast(String msg) {
		Message m = new Message(msg, "SYSTEM");
		buf = m.toString().getBytes(StandardCharsets.UTF_8);
		try {
			if (buf.length > 1024) {
				throw new IOException();
			}
			out = new DatagramPacket(buf, buf.length, multicastAddress, port);
			sender.send(out);
			display(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = DateHandler.convert(new Date()) + " " + msg;
		gui.appendEvent(time + "\n");
	}

	/**
	 * receives a DatagramPacket and stores it in {@link #in}
	 */
	private void receiveMessage() {
		byte[] buf = new byte[1024];
		try {
			in = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(),
			                        60668);
			receiver.receive(in);
		} catch (IOException e) {
			display("Błąd przy odbieraniu wiadomośći.");
		}
	}

	@SuppressWarnings("unused")
	public void runn() {
		Message msgIn, msgOut;
		InetAddress clientAdress;
		int clientPort;
		status = true;
		while (status) {
			try {
				receiveMessage();
				msgIn = Message.fromString(new String(in.getData(),
				                                      StandardCharsets.UTF_8));
				switch (msgIn.getType()) {
					case Message.CONNECT:
						users.add(msgIn.getAuthor());
						broadcast(msgIn.getAuthor() + " właśnie dołączył " +
						          "do konwersacji.");
						break;
					case Message.MESSAGE:
						broadcast();
						break;
					case Message.LOGOUT:
						users.remove(msgIn.getAuthor());
						broadcast(msgIn.getAuthor() + " opuścił konwersację");
						break;
				}
				Thread.sleep(100);
			} catch (Exception e) {
				broadcast((new Message("ERROR", "SYSTEM").toString()));
				e.printStackTrace();
			}
		}
	}

	public void stopServ() {
		status = false;
		sender.close();
	}
}
