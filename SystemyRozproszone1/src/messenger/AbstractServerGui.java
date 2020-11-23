package messenger;

import utils.Message;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;

/**
 * @author Piotr Szerszeń
 */
public abstract class AbstractServerGui extends JFrame
		implements ActionListener, WindowListener {
	private static final long serialVersionUID = 1L;
	protected JTextArea chat, event;
	protected int port;
	/**
	 * A thread to run the Server
	 */

	protected JButton stopStart;

	protected AbstractServerGui(int port) {
		super("Chat Server");
		this.port = port;

		JPanel north = new JPanel();

		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);

		JPanel center = new JPanel(new GridLayout(2, 1));
		chat = new JTextArea(80, 80);
		chat.setEditable(false);
		center.add(new JScrollPane(chat));
		event = new JTextArea(80, 80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));
		add(center);

		addWindowListener(this);
		setSize(400, 600);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {}

	/**
	 * @param str
	 */
	public void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(event.getText().length() - 1);
	}

	/**
	 * append message to the two JTextArea position at the end
	 *
	 * @param message
	 */
	public void appendRoom(Message message) {
		String header = "(" + new Date() + ") " + message.getAuthor();
		chat.append(header + "\n");
		chat.append(message.getMsg());
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	/**
	 * close connection before closing an app
	 */
	@Override
	public void windowClosing(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}