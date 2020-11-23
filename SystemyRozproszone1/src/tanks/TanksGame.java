package tanks;

/**
 * An interface containing prime game map. Used by other classes creating Tanks
 * game.
 *
 * @author Piotr Szerszeń
 */
public interface TanksGame {
	/**
	 * Fields names signed by single letter for {@link #map} to be easier to read.
	 */
	public static String B = "blanc", D = "darkwall", R = "redwall", G = "goal";
	/**
	 * Prime game map
	 */
	public String[][] map = new String[][]{{B, B, B, R, D, R, B, B, B},
	                                       {B, B, D, R, B, B, D, R, B},
	                                       {D, R, B, B, R, B, B, B, R},
	                                       {B, B, B, R, R, R, B, D, B},
	                                       {B, B, R, R, G, R, R, B, B},
	                                       {D, R, B, R, R, R, B, B, R},
	                                       {B, B, D, B, R, B, R, D, B},
	                                       {B, B, B, B, R, D, B, B, B},
	                                       {B, B, B, D, B, B, B, B, B}
	};

	/**
	 * Will change a local map (not {@link #map}, but some field in implementing
	 * class) looking up to newMap
	 *
	 * @param newMap
	 * 		new version of the map
	 */
	public abstract void copyChanges(String[][] newMap);

	/**
	 * It's suppoused to give parameters of tank with specified nick on localMap
	 *
	 * @param nick
	 * 		what we're looking for
	 *
	 * @return parameters as 2-elemental int array
	 */
	public abstract int[] getPlaceOf(String nick);

	/**
	 * Manages changes on localMap caused by #move made by #nick.
	 *
	 * @param nick
	 * 		Author of the move
	 * @param move
	 * 		Name of the move
	 */
	public abstract void makeMove(String nick, String move);
}
