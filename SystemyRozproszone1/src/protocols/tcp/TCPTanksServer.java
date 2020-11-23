package protocols.tcp;

import tanks.TCPTanksServerController;
import tanks.TanksGame;
import utils.DateHandler;
import utils.GameMessage;
import utils.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class TCPTanksServer implements TanksGame {
	private class ClientThread extends Thread {
		private ObjectInputStream inputStream;
		private GameMessage message;
		private String nick;
		private ObjectOutputStream outputStream;
		private Socket socket;

		public ClientThread(Socket socket) {
			this.socket = socket;
			try {
				outputStream = new ObjectOutputStream(
						this.socket.getOutputStream());
				inputStream = new ObjectInputStream(
						this.socket.getInputStream());

				nick = (String) inputStream.readObject();
				display(nick + " wlasnie dolaczyl.");
			} catch (IOException | ClassNotFoundException e) {
				display("Blad przy dodawaniu uzytkownika.");
			}
		}

		/**
		 * try to close everything
		 */
		private void closeAll() {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {}
		}

		/**
		 * Write a String to the Client output stream
		 */
		private synchronized boolean writeMsg(GameMessage msg) {
			if (!socket.isConnected()) {
				closeAll();
				return false;
			}
			try {
				outputStream.writeObject(msg);
			} catch (IOException e) {
				display("Blad przy wysylaniu wiadomosci do " + nick);
				display(e.toString());
			}
			return true;
		}

		/**
		 * what will run forever*
		 */
		@Override
		public void run() {
			boolean status = true;
			while (status) {
				try {
					message = (GameMessage) inputStream.readObject();
					sleep(100);
					if (message == null) {
						status = false;
						broadcast("Nie wczytano obiektu poprawnie...");
						remove(nick);
					}
					switch (message.getType()) {
						case Message.LOGOUT:
							display(nick + " wlasnie odlaczyl od gry");
							broadcast(new GameMessage("out",
							                          message.getAuthor(),
							                          GameMessage.REMOVE));
							status = false;
							remove(nick);
							break;
						case GameMessage.MOVE:
							makeMove(nick, message.getMsg());
							gui.getMap().copyChanges(localMap);
							broadcast(message);
							break;
					}
				} catch (ClassNotFoundException | IOException |
						InterruptedException e) {
					display("Blad przy odbieraniu wiadomosci.");
					break;
				}
			}
		}
	}

	private ArrayList<ClientThread> all;
	private TCPTanksServerController gui;
	private String[][] localMap;
	private int places = 4;
	private int port;
	private boolean status;

	public TCPTanksServer(int port, TCPTanksServerController gui) {
		this.port = port;
		this.gui = gui;
		all = new ArrayList<>();
		localMap = map;
	}

	/**
	 * Sends the message to every client on this Server
	 *
	 * @param gm
	 * 		GameMessage object to send
	 */
	private synchronized void broadcast(GameMessage gm) {
		for (ClientThread ct : all) {
			if (!ct.writeMsg(gm)) {
				remove(ct.nick);
				display("Disconnected Client " + ct.nick + " removed from " +
				        "list" + ".");
			}
		}
	}

	/**
	 * Sends the message to every client on this Server
	 *
	 * @param message
	 * 		message to send within GameMessage object
	 */
	private synchronized void broadcast(String message) {
		GameMessage gm = new GameMessage(message);
		for (ClientThread ct : all) {
			if (!ct.writeMsg(gm)) {
				remove(ct.nick);
				display("Disconnected Client " + ct.nick + " removed from " +
				        "list" + ".");
			}
		}
	}

	/**
	 * calls {@link TCPTanksServerController#appendEvent(String)} giving the same
	 * argument but with time of message
	 *
	 * @param msg
	 * 		Message to display
	 */
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
	 * for a client who log-off using the LOGOUT message
	 *
	 * @param nick
	 */
	private synchronized void remove(String nick) {
		int[] p = getPlaceOf(nick);
		localMap[p[0]][p[1]] = "blanc";
		gui.getMap().copyChanges(localMap);
		for (int i = 0; i < all.size(); ++i) {
			ClientThread ct = all.get(i);
			if (ct.nick.equals(nick)) {
				all.remove(i);
				places++;
				return;
			}
		}
	}

	private void spreadWonMessage(String nick) {
		broadcast(nick + " wygrał");
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

	public void setLocalMap(String[][] newMap) {
		localMap = new String[newMap.length][];
		for (int i = 0; i < newMap.length; i++) {
			localMap[i] = newMap[i].clone();
		}
	}

	/**
	 * During adding new player: sets connection, if there are places avaliable,
	 * places a new tank, broadcasts the GameMessage about where to place new tank
	 * (and whom is this tank), informs new player where is he/she on map, then
	 * starts to listening for the messages from he/she.
	 */
	public void start() {
		status = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (status) {
				if (places > 0) {
					display("Server waiting for Clients on port " + port + "" +
					        ".");

					Socket socket = serverSocket.accept();
					if (!status) {
						break;
					}
					ClientThread t = new ClientThread(socket);
					all.add(t);
					int[] pos = placeTank(t.nick);
					String dir = (places + 1) % 2 == 0 ? "n" : "s";
					broadcast(new GameMessage(pos[0] + ":" + pos[1] + ":" +
					                          dir, t.nick, GameMessage.PLACE));
					for (ClientThread ct : all) {
						int[] temp = getPlaceOf(ct.nick);
						String place = localMap[temp[0]][temp[1]].split(" ")[0];
						GameMessage m = new GameMessage(
								temp[0] + ":" + temp[1] + ":" + place.substring(
										4), ct.nick, GameMessage.PLACE);
						t.writeMsg(m);
					}
					t.writeMsg(new GameMessage(
							"jestes na pozycji: " + pos[0] + ":" + pos[1] +
							"\nGrasz czerwonym.\n"));
					t.start();
				}
			}
			try {
				serverSocket.close();
				for (int i = 0; i < all.size(); ++i) {
					ClientThread tc = all.get(i);
					tc.closeAll();
				}
			} catch (Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		} catch (IOException e) {
			String msg = DateHandler.convert(
					new Date()) + " Exception on new ServerSocket: " + e +
			             "\n";
			display(msg);
		}
	}

	/**
	 * For the GUI to stop the server
	 */
	public void stop() {
		status = false;
		try {
			new Socket("127.0.0.1", port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
