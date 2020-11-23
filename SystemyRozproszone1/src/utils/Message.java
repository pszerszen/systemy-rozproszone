package utils;

import java.io.Serializable;
import java.net.DatagramPacket;

public class Message implements Serializable {
	private static final long serialVersionUID = 5145018755185814383L;
	/**
	 * RegEx binding Message parts.
	 */
	protected static final String SPLIT = "SSSPPPLLLIIITTT";
	/**
	 * Different types of the message
	 */
	public static final int WHO_IS_IN = 0, MESSAGE = 1, LOGOUT = 2, CONNECT = 3;
	protected String author;
	protected String msg;
	protected int type;

	/**
	 * Constructor using classical one, but setting type as MESSAGE.
	 *
	 * @param msg
	 * @param author
	 */
	public Message(String msg, String author) {
		this(msg, author, MESSAGE);
	}

	/**
	 * Classical constructor.
	 *
	 * @param msg
	 * 		message text
	 * @param author
	 * 		the author of the message
	 * @param type
	 * 		type of the message
	 */
	public Message(String msg, String author, int type) {
		this.msg = msg;
		this.author = author;
		this.type = type;
	}

	public String getAuthor() {
		return author;
	}

	public String getMsg() {
		return msg;
	}

	public int getType() {
		return type;
	}

	/**
	 * Prepares {@link Message} object to easier send as {@link DatagramPacket}
	 */
	@Override
	public String toString() {
		return author + SPLIT + msg + SPLIT + type;
	}

	/**
	 * "Decodes" the {@link Message} object encrypted as String by
	 * Message.toString()
	 *
	 * @param msg
	 * 		Encrypted message by Message.toString() method
	 *
	 * @return "decoded" {@link Message} object
	 */
	public static Message fromString(String msg) {
		try {
			String[] t = msg.split(SPLIT);
			return new Message(t[1], t[0], Integer.parseInt(t[2].trim()));
		} catch (Exception e) {
			e.printStackTrace();
			return new Message("Wiadomość dotarła uszkodzona...", "SYSTEM");
		}
	}
}
