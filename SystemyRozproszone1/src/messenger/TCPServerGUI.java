package messenger;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import protocols.tcp.TCPMsgServer;

/**
 * The server as a GUI
 */
public class TCPServerGUI extends AbstractServerGui {
	private static final long serialVersionUID = -3074730651207773976L;
	private TCPMsgServer server;

	/**
	 * A thread to run the Server
	 */
	private Thread serverRunning = new Thread() {
		@Override
		public void run() {
			server.start();
			stopStart.setText("Start");
			appendEvent("Server crashed\n");
			server = null;
		}
	};

	public TCPServerGUI(int port) {
		super(port);
		server = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (server != null) {
			System.out.println("wszedl w ifa");
			server.stop();
			server = null;
			stopStart.setText("Start");
			return;
		}
		stopStart.setText("Stop");
		server = new TCPMsgServer(port, this);
		serverRunning.start();
	}

	public TCPMsgServer getServer() {
		return server;
	}

	public void setServer(TCPMsgServer server) {
		this.server = server;
	}

	/**
	 * close connection before closing an app
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if (server != null) {
			try {
				server.stop();
			} catch (Exception eClose) {}
			server = null;
		}
		dispose();
		System.exit(0);
	}
}