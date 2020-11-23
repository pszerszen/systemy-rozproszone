package messenger;

import protocols.udp.UDPMsgServer;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class UDPServerGUI extends AbstractServerGui {
	private static final long serialVersionUID = -3596560368302560127L;
	private UDPMsgServer server;
	/**
	 * A thread to run the Server
	 */
	private Thread serverRunning = new Thread() {
		@Override
		public void run() {
			server.runn();
			appendEvent("Server crashed");
			server = null;
		}
	};

	public UDPServerGUI(int port) {
		super(port);
		server = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (server != null) {
			server.stopServ();
			server = null;
			stopStart.setText("Start");
			return;
		}
		stopStart.setText("Stop");
		server = new UDPMsgServer(port, this);
		serverRunning.start();
	}

	/**
	 * close connection before closing an app
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		if (server != null) {
			try {
				server.stopServ();
			} catch (Exception eClose) {}
			server = null;
		}
		dispose();
		System.exit(0);
	}
}
