package kademlia.core.command;

import kademlia.core.NodeTriple;

/**
 * User: Piotrek Date: 16.11.13 Time: 15:17
 */
public class PingCommand extends Command {
	public PingCommand(NodeTriple sender) {
		super(sender);
	}

	@Override
	public Command clone() throws CloneNotSupportedException {
		return new PingCommand(sender.clone());
	}
}
