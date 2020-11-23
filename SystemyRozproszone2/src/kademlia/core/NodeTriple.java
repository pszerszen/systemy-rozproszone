package kademlia.core;

import java.util.Comparator;

import kademlia.network.Address;

/**
 * User: Piotrek Date: 15.11.13 Time: 19:04
 */
public class NodeTriple implements Cloneable {
	public static final Comparator<NodeTriple>
			LEAST_RECENTLY_SEEN_FIRST_COMPARATOR =
			new Comparator<NodeTriple>() {
				@Override
				public int compare(NodeTriple o1, NodeTriple o2) {
					if (o1 == null && o2 == null) {
						return 0;
					} else if (o1 == null) {
						return +1;
					} else if (o2 == null) {
						return -1;
					} else {
						return ((Long) o1.getLastSeen()).compareTo(
								o2.getLastSeen());
					}
				}
			};
	public static final Comparator<NodeTriple>
			MOST_RECENTLY_SEEN_FIRST_COMPARATOR = new Comparator<NodeTriple>() {
		@Override
		public int compare(NodeTriple o1, NodeTriple o2) {
			if (o1 == null && o2 == null) {
				return 0;
			} else if (o1 == null) {
				return +1;
			} else if (o2 == null) {
				return -1;
			} else {
				return ((Long) o1.getLastSeen()).compareTo(
						o2.getLastSeen()) * -1;
			}
		}
	};
	private Address address;
	private long lastSeen;
	private ID nodeID;

	public NodeTriple(Address address, ID nodeID, long lastSeen) {
		if (address == null) {
			throw new NullPointerException("Address was null");
		}
		if (nodeID == null) {
			throw new NullPointerException("NodeID was null");
		}
		this.address = address;
		this.nodeID = nodeID;
		this.lastSeen = lastSeen;
	}

	private boolean equals(NodeTriple nodeTriple) {
		return nodeID.equals(nodeTriple.getNodeID());
	}

	@Override
	public NodeTriple clone() throws CloneNotSupportedException {
		return new NodeTriple(address, nodeID, lastSeen);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NodeTriple && equals((NodeTriple) obj);
	}

	public Address getAddress() {
		return address;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public ID getNodeID() {
		return nodeID;
	}

	@Override
	public int hashCode() {
		return nodeID.integerValue();
	}

	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}

	@Override
	public String toString() {
		return "NodeTriple: " + nodeID + "(" + nodeID
				.integerValue() + ")\tLast seen: " + getLastSeen();
	}

	public static Comparator<NodeTriple> NEAREST_TO_GIVEN_ID_COMPARATOR(
			final ID demandedID) {
		return new Comparator<NodeTriple>() {
			@Override
			public int compare(NodeTriple o1, NodeTriple o2) {
				if (o1 == null && o2 == null) {
					return 0;
				} else if (o1 == null) {
					return +1;
				} else if (o2 == null) {
					return -1;
				}
				ID distance1 = o1.getNodeID().applyXOR(demandedID);
				ID distance2 = o2.getNodeID().applyXOR(demandedID);
				if (distance1.equals(distance2)) {
					return 0;
				}
				if (distance1.integerValue() < distance2.integerValue()) {
					return -1;
				} else {
					return +1;
				}
			}
		};
	}
}
