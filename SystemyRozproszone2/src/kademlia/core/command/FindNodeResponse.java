package kademlia.core.command;

import java.util.ArrayList;
import java.util.List;

import kademlia.core.NodeTriple;

/**
 * User: Piotrek Date: 16.11.13 Time: 16:14
 */
public class FindNodeResponse extends Response {
	private final List<NodeTriple> responseList;

	public FindNodeResponse(List<NodeTriple> responseList) {
		super();
		this.responseList = responseList;
	}

	public List<NodeTriple> getResponseList() {
		return responseList;
	}

	@Override
	public Response clone() throws CloneNotSupportedException {
		ArrayList<NodeTriple> newList = new ArrayList<NodeTriple>();
		for (NodeTriple nodeTriple : responseList) {
			newList.add(nodeTriple.clone());
		}
		return new FindNodeResponse(newList);
	}
}
