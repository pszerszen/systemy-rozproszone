package messenger;

import utils.DateHandler;
import utils.Message;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyListener;
import java.util.Date;

public class MsgPanel extends JPanel {
	private static final long serialVersionUID = 1665978433169904284L;
	private JButton btnSend;
	private JTextArea conversationTArea;
	private JTextArea message;

	public MsgPanel() {
		setSize(new Dimension(520, 400));
		setPreferredSize(new Dimension(520, 400));
		setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 11, 500, 255);
		add(scrollPane);

		conversationTArea = new JTextArea();
		conversationTArea.setWrapStyleWord(true);
		conversationTArea.setLineWrap(true);
		conversationTArea.setEditable(false);
		conversationTArea.setFont(new Font("Calibri", Font.PLAIN, 12));
		conversationTArea.setBounds(new Rectangle(10, 11, 500, 255));
		scrollPane.setViewportView(conversationTArea);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						conversationTArea.select(
								conversationTArea.getLineCount() * 10000, 0);
					}
				});

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(10, 277, 395, 112);
		add(scrollPane_1);

		message = new JTextArea();
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setFont(new Font("Calibri", Font.PLAIN, 12));
		message.setBounds(new Rectangle(10, 277, 395, 112));
		scrollPane_1.setViewportView(message);
		message.setColumns(10);

		btnSend = new JButton("Wyślij");
		btnSend.setFont(new Font("Calibri", Font.PLAIN, 20));
		btnSend.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnSend.setFocusable(false);
		btnSend.setActionCommand("send");

		btnSend.setBounds(415, 277, 95, 112);
		add(btnSend);
	}

	public void addListeners(ActionListener a, KeyListener k) {
		btnSend.addActionListener(a);
		message.addKeyListener(k);
	}

	public void append(Message message) {
		String header = "(" + DateHandler.convert(new Date()) + ") " + message
				.getAuthor();
		conversationTArea.append(header + "\n");
		conversationTArea.append(message.getMsg() + "\n");
	}

	/**
	 * Gives the value of message object.
	 *
	 * @return The message.
	 */
	public JTextArea getMessage() {
		return message;
	}
}
