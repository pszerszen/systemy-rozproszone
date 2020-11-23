package tanks;

import protocols.udp.UDPTanksServer;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class UDPTanksServerController extends AbstractTanksGUI {
	private static final long serialVersionUID = -4217166902157250705L;
	private UDPTanksServer server;
	private Thread serverRunning = new Thread() {
		@Override
		public void run() {
			server.run();
			stopStart.setText("Start");
			appendEvent("Server crashed\n");
			server = null;
		}
	};

	public UDPTanksServerController(int port) {
		super(port, "Tanks 2013 - SERVER", null);
		stopStart.addActionListener(this);
		server = null;
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"D:\\eclipse\\workspaceJ\\SystemyRozproszone1\\res\\etankn" + ".jpg"));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (server != null) {
			server.stopServ();
			server = null;
			stopStart.setText("Start");
			return;
		}
		stopStart.setText("Stop");
		server = new UDPTanksServer(port, this);
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
