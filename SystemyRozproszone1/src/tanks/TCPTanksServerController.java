package tanks;

import protocols.tcp.TCPTanksServer;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class TCPTanksServerController extends AbstractTanksGUI {
	private static final long serialVersionUID = -2149220581061220855L;
	private TCPTanksServer server;
	private Thread serverRunning = new Thread() {
		@Override
		public void run() {
			server.start();
			stopStart.setText("Start");
			appendEvent("Server crashed\n");
			server = null;
		}
	};

	public TCPTanksServerController(int port) {
		super(port, "Tanks 2013 - SERVER", null);
		stopStart.addActionListener(this);
		server = null;
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"D:\\eclipse\\workspaceJ\\SystemyRozproszone1\\res\\etankn" + ".jpg"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (server != null) {
			server.stop();
			server = null;
			stopStart.setText("Start");
			return;
		}
		stopStart.setText("Stop");
		server = new TCPTanksServer(port, this);
		serverRunning.start();
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

	public TCPTanksServer getServer() {
		return server;
	}

	public void setServer(TCPTanksServer server) {
		this.server = server;
	}
}
