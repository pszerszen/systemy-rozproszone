package kademlia.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: Piotrek Date: 15.11.13 Time: 19:56
 */
public class NodeLookupSearchEntry {
	private final NodeTriple foundTriple;
	private boolean alreadyQueried = false;

	public NodeLookupSearchEntry(NodeTriple foundTriple) {
		this.foundTriple = foundTriple;
	}

	public static Comparator<NodeLookupSearchEntry>
	NEAREST_TO_GIVEN_ID_COMPARATOR(
			final ID demandedID) {
		return new Comparator<NodeLookupSearchEntry>() {
			@Override
			public int compare(NodeLookupSearchEntry o1,
			                   NodeLookupSearchEntry o2) {
				return NodeTriple.NEAREST_TO_GIVEN_ID_COMPARATOR(demandedID)
						.compare(o1.getFoundTriple(), o2.getFoundTriple());
			}
		};
	}

	public static ArrayList<NodeLookupSearchEntry> convertToNodeLookupList(
			List<NodeTriple> list) {
		ArrayList<NodeLookupSearchEntry> result =
				new ArrayList<NodeLookupSearchEntry>();
		for (NodeTriple nodeTriple : list) {
			result.add(new NodeLookupSearchEntry(nodeTriple));
		}
		return result;
	}

	public NodeTriple getFoundTriple() {
		return foundTriple;
	}

	@Override
	public int hashCode() {
		return foundTriple.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NodeLookupSearchEntry && foundTriple.equals(
				((NodeLookupSearchEntry) obj).getFoundTriple());
	}

	@Override
	public String toString() {
		return "[" + foundTriple
				.toString() + "; Queried: " + alreadyQueried + "]";
	}

	public boolean isAlreadyQueried() {
		return alreadyQueried;
	}

	public void setAlreadyQueried() {
		this.alreadyQueried = true;
	}
}
