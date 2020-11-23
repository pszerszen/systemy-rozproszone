package tanks;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class AbstractTanksGUI extends JFrame
		implements ActionListener, WindowListener, KeyListener {
	private static final long serialVersionUID = 5658697091312432746L;
	protected TanksPanel map;
	protected String nick;
	protected int port;
	protected JButton stopStart;
	private JTextArea event;
	private JScrollPane scrollPane;

	public AbstractTanksGUI(int port, String title, String nick) {
		super(title);
		this.port = port;
		setSize(600, 700);
		map = new TanksPanel(nick);
		map.setBounds(0, 0, 500, 600);
		event = new JTextArea(10, 40);
		event.setFocusable(false);
		event.setSize(new Dimension(400, 100));
		event.setPreferredSize(new Dimension(400, 100));
		event.setFont(new Font("Calibri", Font.PLAIN, 12));
		event.setEditable(false);
		event.setWrapStyleWord(true);
		event.setLineWrap(true);
		scrollPane = new JScrollPane(event);
		map.add(scrollPane);
		scrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		stopStart = new JButton("Start");
		stopStart.setFocusable(false);
		stopStart.setFont(new Font("Calibri", Font.PLAIN, 14));
		map.add(stopStart);
		setContentPane(map);

		addWindowListener(this);
		map.addKeyListener(this);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {}

	/**
	 * Appends a new event log to #event window.
	 *
	 * @param str
	 * 		appended message
	 */
	public void appendEvent(String str) {
		event.append(str + "\n");
		event.setCaretPosition(event.getText().length() - 1);
	}

	public TanksPanel getMap() {
		return map;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	/**
	 * close connection before closing an app
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}
}
