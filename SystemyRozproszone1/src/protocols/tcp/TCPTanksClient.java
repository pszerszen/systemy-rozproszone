package protocols.tcp;

import tanks.TCPTanksController;
import tanks.TanksGame;
import utils.GameMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPTanksClient implements TanksGame {
	private TCPTanksController gui;
	private ObjectInputStream inputStream;
	private Thread listenFromServer = new Thread() {
		@Override
		public void run() {
			while (true) {
				try {
					o = inputStream.readObject();
					if (o instanceof String[][]) {
						copyChanges((String[][]) o);
					} else if (o instanceof GameMessage) {
						GameMessage m = (GameMessage) o;
						switch (m.getType()) {
							case GameMessage.MESSAGE:
								display(m.getMsg());
								if (m.getMsg().endsWith("wygrał")) {
									gui.removeKeyListener(gui);
								}
								break;
							case GameMessage.MOVE:
								makeMove(m.getAuthor(), m.getMsg());
								gui.getMap().copyChanges(localMap);
								break;
							case GameMessage.PLACE:
								String[] pos = m.getMsg().split(":");
								int x = Integer.parseInt(pos[0]);
								int y = Integer.parseInt(pos[1]);
								localMap[x][y] =
										"tank" + pos[2] + " " + m.getAuthor();
								gui.getMap().copyChanges(localMap);
								break;
							case GameMessage.REMOVE:
								int[] p = getPlaceOf(m.getAuthor());
								localMap[p[0]][p[1]] = "blanc";
								gui.getMap().copyChanges(localMap);
								display(m.getAuthor() + " właśnie odłączył " +
								        "od gry.");
								break;
						}
					} else if (o == null) {
						display("Błąd przy odbieraniu");
					}
				} catch (ClassNotFoundException | IOException e) {
					display("Server zamknął połączenie");
					break;
				}
			}
		}
	};
	private String[][] localMap;
	private String nick;
	private Object o;
	private ObjectOutputStream outputStream;
	private int port;
	private String server;
	private Socket socket;

	public TCPTanksClient(TCPTanksController gui, String nick, String server,
	                      int port) {
		this.gui = gui;
		this.nick = nick;
		this.server = server;
		this.port = port;
		localMap = map;
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

	@Override
	public void copyChanges(String[][] newMap) {
		setLocalMap(newMap);
		gui.getMap().copyChanges(localMap);
	}

	/**
	 * searches out the tank named with nick
	 */
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
					if (localMap[x - 1][y].equals("goal") || localMap[x - 1][y]
							.equals("blanc")) {
						localMap[x - 1][y] = "tankn " + nick;
						localMap[x][y] = "blanc";
					}
					// stop();

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
	 * Shows a log message.
	 *
	 * @param msg
	 */
	public void display(String msg) {
		gui.appendEvent(msg);
	}

	public String getNick() {
		return nick;
	}

	public void sendMessage(GameMessage gm) {
		try {
			outputStream.writeObject(gm);
		} catch (IOException e) {
			display("Error while sending a message to server");
		}
	}

	/**
	 * Sends move just made as {@link GameMessage} to the server.
	 *
	 * @param move
	 * 		a move made; possible options: up, down, left, right, shoot
	 */
	public void sendMove(String move) {
		GameMessage gm = new GameMessage(move, nick, GameMessage.MOVE);
		try {
			outputStream.writeObject(gm);
		} catch (IOException e) {
			display("Error while sending a message to server");
		}
	}

	public void setLocalMap(String[][] newMap) {
		localMap = new String[newMap.length][];
		for (int i = 0; i < newMap.length; i++) {
			localMap[i] = newMap[i].clone();
		}
	}

	public boolean start() {
		try {
			socket = new Socket(server, port);
		} catch (IOException e) {
			display("Error connection to server");
			e.printStackTrace();
			return false;
		}
		display("Nawiazano polaczenie " + socket.getInetAddress() + ":" +
		        socket.getPort());
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			display("Error while creating IO sreams");
			e.printStackTrace();
			return false;
		}
		listenFromServer.start();

		try {
			outputStream.writeObject(nick);
		} catch (IOException e) {
			display("Error sending your nick: ");
			e.printStackTrace();
			disconnect();
			return false;
		}
		return true;
	}
}
