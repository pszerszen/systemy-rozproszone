package utils;

public class GameMessage extends Message {
	/**
	 * the other types of Message exceptionally for the game
	 */
	public static final int MOVE = 4, PLACE = 5, REMOVE = 6, CLOSE = 7;
	private static final long serialVersionUID = -5477574722611030491L;

	/**
	 * A specific Game Message<br> author: "SYSTEM"<br> type: MESSAGE
	 *
	 * @param msg
	 */
	public GameMessage(String msg) {
		this(msg, "SYSTEM", MESSAGE);
	}

	/**
	 * A specific Game Message<br> type: MESSAGE
	 *
	 * @param msg
	 * @param author
	 */
	public GameMessage(String msg, String author) {
		this(msg, author, MESSAGE);
	}

	/**
	 * Pretty much same as {@link Message} constructor
	 *
	 * @param msg
	 * @param author
	 * @param type
	 */
	public GameMessage(String msg, String author, int type) {
		super(msg, author, type);
	}

	/**
	 * "Decodes" the {@link GameMessage} object encrypted as String by
	 * Message.toString()
	 *
	 * @param msg
	 * 		Encrypted message by Message.toString() method
	 *
	 * @return "decoded" {@link Message} object
	 */
	public static GameMessage fromString(String msg) {
		try {
			String[] t = msg.split(SPLIT);
			return new GameMessage(t[1], t[0], Integer.parseInt(t[2].trim()));
		} catch (Exception e) {
			e.printStackTrace();
			return new GameMessage("Wiadomość dotarła uszkodzona...", "SYSTEM");
		}
	}
}
