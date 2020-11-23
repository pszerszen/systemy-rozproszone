package protocols.udp;

import tanks.TanksGame;
import tanks.UDPTanksServerController;
import utils.DateHandler;
import utils.GameMessage;
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
import java.util.Random;

public class UDPTanksServer implements TanksGame {
	private ArrayList<String> all;
	private byte[] buf;
	private UDPTanksServerController gui;
	private DatagramPacket in, out;
	private String[][] localMap;
	private InetAddress multicastAddress;
	private DatagramSocket personalSocket;
	private int places = 4;
	private int port;
	private DatagramSocket receiver;
	private DatagramSocket sender;
	private boolean status = true;

	public UDPTanksServer(int port, UDPTanksServerController gui) {
		this.port = port;
		this.gui = gui;
		all = new ArrayList<>();
		localMap = map;
		try {
			multicastAddress = InetAddress.getByName("230.0.0.3");
			receiver = new DatagramSocket(61668);
			sender = new DatagramSocket();
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decodes a GameMessage, MAKES A MOVE and sends the move type message with
	 * multicast
	 */
	private void broadcast() {
		GameMessage gm = GameMessage.fromString(new String(in.getData(),
		                                                   StandardCharsets.UTF_8));
		makeMove(gm.getAuthor(), gm.getMsg());
		try {
			byte[] buf = gm.toString().getBytes(StandardCharsets.UTF_8);
			out = new DatagramPacket(buf, buf.length, multicastAddress, port);
			sender.send(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void broadcast(GameMessage gm) {
		buf = gm.toString().getBytes(StandardCharsets.UTF_8);
		try {
			if (buf.length > 1024) {
				throw new IOException();
			}
			out = new DatagramPacket(buf, buf.length, multicastAddress, port);
			sender.send(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void broadcast(String msg) {
		GameMessage m = new GameMessage(msg);
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

	private void display(String msg) {
		String time = DateHandler.convert(new Date()) + " " + msg;
		gui.appendEvent(time + "\n");
	}

	/**
	 * Places a tank with specified nick and returns it's position.
	 *
	 * @param nick
	 * 		tanks nick
	 *
	 * @return position of tanks
	 */
	private int[] placeTank(String nick) {
		boolean notSet = true;
		int i = -1, j = -1;
		switch (places) {
			case 4:
				if (localMap[0][0].equals("blanc")) {
					localMap[0][0] = "tankn " + nick;
					i = 0;
					j = 0;
				} else {
					do {
						Random r = new Random();
						i = r.nextInt() % localMap.length;
						j = r.nextInt() % localMap.length;
						if (localMap[i][j].equals("blanc")) {
							localMap[i][j] = "tankn " + nick;
							notSet = false;
						}
					} while (notSet);
				}
				break;
			case 3:
				if (localMap[8][0].equals("blanc")) {
					localMap[8][0] = "tanks " + nick;
					i = 8;
					j = 0;
				} else {
					do {
						Random r = new Random();
						i = r.nextInt() % localMap.length;
						j = r.nextInt() % localMap.length;
						if (localMap[i][j].equals("blanc")) {
							localMap[i][j] = "tanks " + nick;
							notSet = false;
						}
					} while (notSet);
				}
				break;
			case 2:
				if (localMap[0][8].equals("blanc")) {
					localMap[0][8] = "tankn " + nick;
					i = 0;
					j = 8;
				} else {
					do {
						Random r = new Random();
						i = r.nextInt() % localMap.length;
						j = r.nextInt() % localMap.length;
						if (localMap[i][j].equals("blanc")) {
							localMap[i][j] = "tankn " + nick;
							notSet = false;
						}
					} while (notSet);
				}
				break;
			case 1:
				if (localMap[8][8].equals("blanc")) {
					localMap[8][8] = "tanks " + nick;
					i = 8;
					j = 8;
				} else {
					do {
						Random r = new Random();
						i = r.nextInt() % localMap.length;
						j = r.nextInt() % localMap.length;
						if (localMap[i][j].equals("blanc")) {
							localMap[i][j] = "tanks " + nick;
							notSet = false;
						}
					} while (notSet);
				}
				break;
		}
		places--;
		gui.getMap().copyChanges(localMap);
		return new int[]{i, j};
	}

	/**
	 * receives a DatagramPacket and stores it in {@link #in}
	 */
	private void receiveMessage() {
		byte[] buf = new byte[1024];
		try {
			in = new DatagramPacket(buf, buf.length);
			receiver.receive(in);
		} catch (IOException e) {
			display("Blad przy odbieraniu wiadomoci.");
		}
	}

	/**
	 * for a client who log-off using the LOGOUT message
	 *
	 * @param nick
	 */
	private void remove(String nick) {
		int[] p = getPlaceOf(nick);
		localMap[p[0]][p[1]] = "blanc";
		gui.getMap().copyChanges(localMap);
		if (all.remove(nick)) {
			places++;
		}
	}

	/**
	 * Sends single message to single receiver on port 61669. If message is type
	 * GameMessage.CLOSE it is sign for receiver to stop listening for this
	 * kind of
	 * messages on this port.
	 *
	 * @param gm
	 * 		A GameMessage object to send
	 *
	 * @throws IOException
	 */
	private void send(GameMessage gm, InetAddress address, int port)
			throws IOException {
		byte[] buf = gm.toString().getBytes(StandardCharsets.UTF_8);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address,
		                                           port);
		personalSocket.send(packet);
	}

	private void spreadWonMessage(String nick) {
		broadcast(nick + " wygrał");
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

	@Override
	public synchronized void makeMove(String nick, String move) {
		int[] position = getPlaceOf(nick);
		int x = position[0], y = position[1];
		String name = localMap[x][y].split(" ")[0];
		String direction = Character.toString(name.charAt(4));

		switch (move) {
			case "up":                                        // /\
				if (!direction.equals("n")) {
					localMap[x][y] = "tankn " + nick;
				} else if (!((x - 1) < 0)) {
					if (localMap[x - 1][y].equals("goal")) {
						spreadWonMessage(nick);
					} else if (localMap[x - 1][y].equals("blanc")) {
						localMap[x - 1][y] = "tankn " + nick;
						localMap[x][y] = "blanc";
					}
				}
				break;
			case "down":                                    // \/
				if (!direction.equals("s")) {
					localMap[x][y] = "tanks " + nick;
				} else if ((x + 1) < localMap.length) {
					if (localMap[x + 1][y].equals("goal")) {
						spreadWonMessage(nick);
					} else if (localMap[x + 1][y].equals("blanc")) {
						localMap[x + 1][y] = "tanks " + nick;
						localMap[x][y] = "blanc";
					}
				}
				break;
			case "left":                                    // <
				if (!direction.equals("w")) {
					localMap[x][y] = "tankw " + nick;
				} else if (!((y - 1) < 0)) {
					if (localMap[x][y - 1].equals("goal")) {
						spreadWonMessage(nick);
					} else if (localMap[x][y - 1].equals("blanc")) {
						localMap[x][y - 1] = "tankw " + nick;
						localMap[x][y] = "blanc";
					}
				}
				break;
			case "right":                                    // >
				if (!direction.equals("e")) {
					localMap[x][y] = "tanke " + nick;
				} else if ((y + 1) < localMap.length) {
					if (localMap[x][y + 1].equals("goal")) {
						spreadWonMessage(nick);
					} else if (localMap[x][y + 1].equals("blanc")) {
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

	public void run() {
		GameMessage msgIn;
		InetAddress clientAddress;
		int clientPort;
		status = true;
		while (status) {
			try {
				receiveMessage();
				clientAddress = in.getAddress();
				clientPort = in.getPort();
				msgIn = GameMessage.fromString(new String(in.getData(),
				                                          StandardCharsets.UTF_8));
				switch (msgIn.getType()) {
					//case someone wanna play
					case Message.CONNECT:
						if (places > 0) {
							all.add(msgIn.getAuthor());
							// placing a dude on map
							int[] pos = placeTank(msgIn.getAuthor());
							String dir = (places + 1) % 2 == 0 ? "n" : "s";

							//broadcasting his/her position to everybody
							broadcast(new GameMessage(
									pos[0] + ":" + pos[1] + ":" +
									dir, msgIn.getAuthor(), GameMessage.PLACE));

							//sending positions of other players to newby
							personalSocket = new DatagramSocket();
							for (String player : all) {
								int[] temp = getPlaceOf(player);
								String place = localMap[temp[0]][temp[1]].split(
										" ")[0];
								GameMessage m = new GameMessage(
										temp[0] + ":" + temp[1] + ":" + place
												.substring(4), player,
										GameMessage.PLACE);
								send(m, clientAddress, 61669);
							}

							//informing newby that addition went successfully
							GameMessage m = new GameMessage(
									"jesteś na pozycji: " + pos[0] + ":" +
									pos[1] +
									"\nGrasz czerwonym.\n");
							send(m, clientAddress, clientPort);
							send(new GameMessage("close", "SYSTEM",
							                     GameMessage.CLOSE),
							     clientAddress, 61669);
							broadcast(msgIn.getAuthor() + " właśnie dołączył" +
							          " " +
							          "do" + " " + "gry" +
							          ".");
						}
						break;
					case GameMessage.MOVE:
						broadcast();
						gui.getMap().copyChanges(localMap);
						break;
					case GameMessage.LOGOUT:
						display(msgIn.getAuthor() + " właśnie odłączył od " +
						        "gry");
						GameMessage gm = new GameMessage("out",
						                                 msgIn.getAuthor(),
						                                 GameMessage.REMOVE);
						broadcast(gm);
						remove(msgIn.getAuthor());
				}
			} catch (Exception e) {
				broadcast((new GameMessage("ERROR", "SYSTEM").toString()));
				e.printStackTrace();
			}
		}
	}

	public void setLocalMap(String[][] newMap) {
		localMap = new String[newMap.length][];
		for (int i = 0; i < newMap.length; i++) {
			localMap[i] = newMap[i].clone();
		}
	}

	public void stopServ() {
		status = false;
		sender.close();
	}
}
