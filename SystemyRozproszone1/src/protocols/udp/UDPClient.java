package protocols.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPClient {
	private InetAddress address;
	private byte[] buf;
	private DatagramPacket packet;
	private MulticastSocket socket;

	public UDPClient(String nick, String host) throws IOException {
		socket = new MulticastSocket(60667);
		address = InetAddress.getByName(host);
		socket.joinGroup(address);
	}

	public void end() throws IOException {
		socket.leaveGroup(address);
		socket.close();
	}

	public String receive() throws IOException {
		buf = new byte[1024];
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		return new String(packet.getData());
	}
}