package protocols.udp;

import tanks.TanksGame;
import tanks.UDPTanksController;
import utils.GameMessage;
import utils.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class UDPTanksClient implements TanksGame {
	private InetAddress address;
	private byte[] buf;
	private UDPTanksController gui;
	private DatagramPacket in, out;
	private Thread listenFromServer = new Thread() {
		@Override
		public void run() {
			try {
				while (true) {
					GameMessage gm = receive();
					switch (gm.getType()) {
						case GameMessage.MOVE:
							makeMove(gm.getAuthor(), gm.getMsg());
							gui.getMap().copyChanges(localMap);
							break;
						case GameMessage.MESSAGE:
							display(gm.getMsg());
							if (gm.getMsg().endsWith("wygrał")) {
								gui.removeKeyListener(gui);
							}
							break;
						case GameMessage.PLACE:
							String[] pos = gm.getMsg().split(":");
							int x = Integer.parseInt(pos[0]);
							int y = Integer.parseInt(pos[1]);
							localMap[x][y] =
									"tank" + pos[2] + " " + gm.getAuthor();
							gui.getMap().copyChanges(localMap);
							break;
						case GameMessage.REMOVE:
							int[] p = getPlaceOf(gm.getAuthor());
							localMap[p[0]][p[1]] = "blanc";
							gui.getMap().copyChanges(localMap);
							display(gm.getAuthor() + " właśnie odłączył " +
							        "od gry.");
							break;
					}
				}
			} catch (IOException e) {}
		}
	};
	private String[][] localMap;
	private MulticastSocket msocket;
	private InetAddress multicastAddress;
	private String nick;
	private int port;
	private DatagramSocket socket;

	public UDPTanksClient(String address, UDPTanksController gui, String nick,
	                      int port) throws UnknownHostException {
		this.address = InetAddress.getByName(address);
		this.gui = gui;
		this.nick = nick;
		this.port = port;
		multicastAddress = InetAddress.getByName("230.0.0.3");
		localMap = map;
	}

	private void display(String msg) {
		gui.appendEvent(msg);
	}

	@Override
	public void copyChanges(String[][] newMap) {
		setLocalMap(newMap);
		gui.getMap().copyChanges(localMap);
	}

	@Override
	public int[] getPlaceOf(String nick) {
		for (int i = 0; i < localMap.length; i++) {
			for (int j = 0; j < localMap.length; j++) {
				if (localMap[i][j].endsWith(nick)) {
					return new int[]{i, j};
				}
			}
		}
		return new int[]{};
	}

	/**
	 * Manages changes caused by #move made by #nick
	 *
	 * @see tanks.TanksGame#makeMove(java.lang.String, java.lang.String)
	 */
	@Override
	public void makeMove(String nick, String move) {
		int[] position = getPlaceOf(nick);
		int x = position[0], y = position[1];
		String name = localMap[x][y].split(" ")[0];
		String direction = Character.toString(name.charAt(4));

		switch (move) {
			case "up":                                        // /\
				if (!direction.equals("n")) {
					localMap[x][y] = "tankn " + nick;
				} else if (!((x - 1) < 0)) {
					if (localMap[x - 1][y].equals("goal") || localMap[x - 1][y]
							.equals("blanc")) {
						localMap[x - 1][y] = "tankn " + nick;
						localMap[x][y] = "blanc";
					}
				}
				break;
			case "down":                                    // \/
				if (!direction.equals("s")) {
					localMap[x][y] = "tanks " + nick;
				} else if ((x + 1) < localMap.length) {
					if (localMap[x + 1][y].equals("goal") || localMap[x + 1][y]
							.equals("blanc")) {
						localMap[x + 1][y] = "tanks " + nick;
						localMap[x][y] = "blanc";
					}
				}
				break;
			case "left":                                    // <
				if (!direction.equals("w")) {
					localMap[x][y] = "tankw " + nick;
				} else if (!((y - 1) < 0)) {
					if (localMap[x][y - 1].equals("goal") || localMap[x][y - 1]
							.equals("blanc")) {
						localMap[x][y - 1] = "tankw " + nick;
						localMap[x][y] = "blanc";
					}
				}
				break;
			case "right":                                    // >
				if (!direction.equals("e")) {
					localMap[x][y] = "tanke " + nick;
				} else if ((y + 1) < localMap.length) {
					if (localMap[x][y + 1].equals("goal") || localMap[x][y + 1]
							.equals("blanc")) {
						localMap[x][y + 1] = "tanke " + nick;
						localMap[x][y] = "blanc";
					}
				}
				break;
			case "shoot":                                    // BANG!
				int X;
				int Y;
				switch (direction) {
					case "n":
						X = x - 1;
						Y = y;
						if (!(X < 0)) {
							while (!(X < 0) && localMap[X][Y].equals("blanc")) {
								X--;
							}
							if (!(X < 0)) {
								if (localMap[X][Y].equals(
										"redwall") || localMap[X][Y].contains(
										"tank")) {
									localMap[X][Y] = "blanc";
								}
							}
						}
						break;
					case "s":
						X = x + 1;
						Y = y;
						if (X < localMap.length) {
							while (X < localMap.length && localMap[X][Y].equals(
									"blanc")) {
								X++;
							}
							if (X < localMap.length) {
								if (localMap[X][Y].equals(
										"redwall") || localMap[X][Y].contains(
										"tank")) {
									localMap[X][Y] = "blanc";
								}
							}
						}
						break;
					case "w":
						X = x;
						Y = y - 1;
						if (!(Y < 0)) {
							while (!(Y < 0) && localMap[X][Y].equals("blanc")) {
								Y--;
							}
							if (!(Y < 0)) {
								if (localMap[X][Y].equals(
										"redwall") || localMap[X][Y].contains(
										"tank")) {
									localMap[X][Y] = "blanc";
								}
							}
						}
						break;
					case "e":
						X = x;
						Y = y + 1;
						if (Y < localMap.length) {
							while (Y < localMap.length && localMap[X][Y].equals(
									"blanc")) {
								Y++;
							}
							if (Y < localMap.length) {
								if (localMap[X][Y].equals(
										"redwall") || localMap[X][Y].contains(
										"tank")) {
									localMap[X][Y] = "blanc";
								}
							}
						}
						break;
				}
				break;
		}
	}

	/**
	 * closes sockets
	 */
	public void disconnect() {
		try {
			sendMessage(new GameMessage("logout", nick, Message.LOGOUT));
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
	public GameMessage receive() throws IOException {
		buf = new byte[1024];
		in = new DatagramPacket(buf, buf.length);
		msocket.receive(in);
		return GameMessage.fromString(new String(in.getData(),
		                                         StandardCharsets.UTF_8));
	}

	public void sendMessage(GameMessage message) throws IOException {
		byte[] buf = message.toString().getBytes(StandardCharsets.UTF_8);
		out = new DatagramPacket(buf, buf.length, address, port);
		socket.send(out);
	}

	public void sendMove(String move) throws IOException {
		GameMessage gm = new GameMessage(move, nick, GameMessage.MOVE);
		sendMessage(gm);
	}

	public void setLocalMap(String[][] newMap) {
		localMap = new String[newMap.length][];
		for (int i = 0; i < newMap.length; i++) {
			localMap[i] = newMap[i].clone();
		}
	}

	public boolean start() {
		try {
			socket = new DatagramSocket();
			msocket = new MulticastSocket(61667);
			msocket.joinGroup(multicastAddress);
		} catch (IOException e) {
			display("Error connection to server.");
			e.printStackTrace();
			return false;
		}
		display("Nawiazano polaczenie " + multicastAddress + ":" + 61667);
		try {
			sendMessage(new GameMessage("connect", nick, Message.CONNECT));
			DatagramSocket personal = new DatagramSocket(61669);
			DatagramPacket pack;
			boolean getting = true;
			while (getting) {
				byte[] buffy = new byte[1024];
				pack = new DatagramPacket(buffy, buffy.length);
				personal.receive(pack);
				GameMessage temp = GameMessage.fromString(new String(
						pack.getData(), StandardCharsets.UTF_8));
				if (temp.getType() == GameMessage.PLACE) {
					String[] pos = temp.getMsg().split(":");
					int x = Integer.parseInt(pos[0]);
					int y = Integer.parseInt(pos[1]);
					localMap[x][y] = "tank" + pos[2] + " " + temp.getAuthor();
					gui.getMap().copyChanges(localMap);
				} else if (temp.getType() == GameMessage.CLOSE) {
					getting = false;
					personal.close();
				}
			}
		} catch (IOException e) {
			display("Error exchanging first data");
			e.printStackTrace();
			disconnect();
			return false;
		}
		listenFromServer.start();
		return true;
	}
}
