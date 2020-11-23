package kademlia.core.command;

import kademlia.core.ID;
import kademlia.core.NodeTriple;

/**
 * User: Piotrek Date: 16.11.13 Time: 16:10
 */
public class FindNodeCommand extends Command {
	private final ID demandedNodeID;

	public FindNodeCommand(NodeTriple sender, ID demandedNodeID) {
		super(sender);
		this.demandedNodeID = demandedNodeID;
	}

	public ID getDemandedNodeID() {
		return demandedNodeID;
	}

	@Override
	public Command clone() throws CloneNotSupportedException {
		return new FindNodeCommand(sender.clone(), demandedNodeID);
	}
}
