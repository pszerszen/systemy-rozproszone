package protocols.tcp;

import messenger.TCPMsgServerController;
import utils.DateHandler;
import utils.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 * The server that can be run both as a console application or a GUI
 */
public class TCPMsgServer {
	/**
	 * One instance of this thread will run for each client
	 */
	private class ClientThread extends Thread {
		private String date;
		/**
		 * my unique id (easier for deconnection)
		 */
		private int id;
		private ObjectInputStream inputStream;
		private Message message;
		private String nick;
		private ObjectOutputStream outputStream;
		/**
		 * the socket where to listen/talk
		 */
		private Socket socket;

		public ClientThread(Socket socket) {
			id = ++TCPMsgServer.uniqueId;
			this.socket = socket;
			try {
				outputStream = new ObjectOutputStream(
						this.socket.getOutputStream());
				inputStream = new ObjectInputStream(
						this.socket.getInputStream());

				nick = (String) inputStream.readObject();
				display(nick + " właśnie dołączył.");
			} catch (IOException | ClassNotFoundException e) {
				display("Błąd przy dodawaniu użytkownika.");
				return;
			}
			date = new Date().toString() + "\n";
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
			} catch (Exception ignored) {}
		}

		/**
		 * Write a {@link Message} to the Client output stream
		 */
		private boolean writeMsg(Message msg) {
			if (!socket.isConnected()) {
				closeAll();
				return false;
			}
			try {
				outputStream.writeObject(msg);
			} catch (IOException e) {
				display("Błąd przy wysyłaniu wiadomości do " + nick);
				display(e.toString());
			}
			return true;
		}

		/**
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			if (!socket.isConnected()) {
				closeAll();
				return false;
			}
			try {
				outputStream.writeObject(msg);
			} catch (IOException e) {
				display("Błąd przy wysyłaniu wiadomości do " + nick);
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
					message = (Message) inputStream.readObject();
					sleep(100);
				} catch (ClassNotFoundException | IOException |
						InterruptedException e) {
					display("Błąd przy odbieraniu wiadomości.");
					break;
				}

				switch (message.getType()) {
					case Message.MESSAGE:
						broadcast(message);
						break;
					case Message.LOGOUT:
						display(nick + " odłączył od rozmowy.");
						status = false;
						break;
					case Message.WHO_IS_IN:
						writeMsg(
								"List of the users connected at " + new Date() + "\n");
						for (int i = 0; i < all.size(); ++i) {
							ClientThread ct = all.get(i);
							writeMsg(
									(i + 1) + ") " + ct.nick + " since " + ct.date);
						}
						break;
				}
			}
		}
	}

	/**
	 * a unique ID for each connection
	 */
	private static int uniqueId;
	/**
	 * an ArrayList to keep the list of the Client
	 */
	private ArrayList<ClientThread> all;
	private TCPMsgServerController gui;
	private int port;
	private boolean status;

	/**
	 * server constructor that receive the port to listen to for connection as
	 * parameter with gui
	 *
	 * @param port
	 * @param gui
	 */
	public TCPMsgServer(int port, TCPMsgServerController gui) {
		this.gui = gui;
		this.port = port;
		all = new ArrayList<>();
	}

	/**
	 * to broadcast a message to all Clients
	 */
	private synchronized void broadcast(Message message) {
		gui.appendRoom(message);
		for (int i = all.size(); --i >= 0; ) {
			ClientThread ct = all.get(i);
			if (!ct.writeMsg(message)) {
				all.remove(i);
				display("Disconnected Client " + ct.nick + " removed from " +
				        "list" + "" +
				        ".");
			}
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
	 * for a client who log-off using the LOGOUT message
	 *
	 * @param id
	 */
	@SuppressWarnings(value = "unused")
	public synchronized void remove(int id) {
		for (int i = 0; i < all.size(); ++i) {
			ClientThread ct = all.get(i);
			if (ct.id == id) {
				all.remove(i);
				return;
			}
		}
	}

	public void start() {
		status = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (status) {
				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept();
				if (!status) {
					break;
				}
				ClientThread t = new ClientThread(socket);
				all.add(t);
				t.start();
			}
			try {
				serverSocket.close();
				for (int i = 0; i < all.size(); ++i) {
					ClientThread tc = all.get(i);
					try {
						tc.inputStream.close();
						tc.outputStream.close();
						tc.socket.close();
					} catch (IOException ignored) {}
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
