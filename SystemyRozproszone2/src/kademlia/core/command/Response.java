package kademlia.core.command;

/**
 * User: Piotrek Date: 16.11.13 Time: 15:24
 */
public abstract class Response implements Cloneable {
	public abstract Response clone() throws CloneNotSupportedException;
}
