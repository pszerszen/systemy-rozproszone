package kademlia.core.command;

import kademlia.core.NodeTriple;

/**
 * User: Piotrek Date: 16.11.13 Time: 15:11
 */
public abstract class Command implements Cloneable {
	protected NodeTriple sender;

	protected Command(NodeTriple sender) {
		this.sender = sender;
	}

	public abstract Command clone() throws CloneNotSupportedException;

	public NodeTriple getSender() {
		return sender;
	}
}
