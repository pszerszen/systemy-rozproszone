package kademlia.core.command;

import kademlia.core.ID;
import kademlia.core.NodeTriple;

/**
 * User: Piotrek Date: 16.11.13 Time: 16:18
 */
public class FindValueCommand extends Command {
	private final ID key;

	public FindValueCommand(NodeTriple sender, ID key) {
		super(sender);
		this.key = key;
	}

	@Override
	public Command clone() throws CloneNotSupportedException {
		return new FindValueCommand(sender.clone(), key);
	}

	public ID getKey() {
		return key;
	}
}
