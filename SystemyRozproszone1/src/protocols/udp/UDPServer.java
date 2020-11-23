package protocols.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import utils.Message;

public class UDPServer extends Thread {
	// private InetAddress address;
	private byte[] buf;
	private InetAddress multicastAddress;
	private DatagramPacket packet;
	private DatagramSocket receiver;
	private DatagramSocket sender;

	public UDPServer(String nick) throws SocketException, UnknownHostException {
		// address = InetAddress.getByName(host);
		multicastAddress = InetAddress.getByName("230.0.0.2");
		sender = new DatagramSocket();
		receiver = new DatagramSocket(60667);
	}

	public void broadcast() {
		try {
			sender.send(packet);
		} catch (IOException e) {
			System.out.println("cos nie poszlo z rozsylaniem pakietu");
			e.printStackTrace();
		}
	}

	public void broadcast(String msg) {
		buf = msg.getBytes();
		try {
			if (buf.length > 1024)
				throw new IOException();
			sender.send(new DatagramPacket(buf, buf.length, multicastAddress,
					60667));
		} catch (IOException e) {
			System.out.println("za dlugi ten message...");
			e.printStackTrace();
		}
	}

	public void receiveMessage() throws IOException {
		byte[] buf = new byte[1024];
		packet = new DatagramPacket(buf, buf.length);
		receiver.receive(packet);
	}

	@Override
	public void run() {
		while (true)
			try {
				receiveMessage();
				broadcast();
			} catch (Exception e) {
				broadcast((new Message("ERROR", "SYSTEM").toString()));
			}
	}
}
