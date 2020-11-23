package kademlia.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import kademlia.exceptions.KBucketUpdateException;
import kademlia.exceptions.NodeNotFoundException;

/**
 * User: Piotrek Date: 16.11.13 Time: 13:24
 */
public class KBucket implements Iterable<NodeTriple> {
	/**
	 * Copies the list at initialization, to make itself robust against changes of
	 * the aggregate.
	 * <p/>
	 * The most-recently seen Node will be at tail of the list, i.e. the highest
	 * index The least-recently seen Node will be at the head of the list,
	 * i.e. the
	 * lowest index
	 */
	private class KBucketIterator implements Iterator<NodeTriple> {
		private NodeTriple[] myAggregate;
		private Comparator<NodeTriple> myComparator;
		private int x = 0;

		private KBucketIterator(NodeTriple[] myAggregate,
		                        Comparator<NodeTriple> myComparator)
				throws CloneNotSupportedException {
			this.myAggregate = new NodeTriple[myAggregate.length];
			this.myComparator = myComparator;

			for (int i = 0; i < myAggregate.length; i++) {
				if (myAggregate[i] == null) {
					this.myAggregate[i] = null;
				} else {
					this.myAggregate[i] = myAggregate[i].clone();
				}
			}

			sort();
		}

		@Override
		public boolean hasNext() {
			return (x < myAggregate.length && myAggregate[x] != null);
		}

		@Override
		public NodeTriple next() {
			return myAggregate[x++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void sort() {
			Arrays.sort(myAggregate, myComparator);
		}

		@Override
		public String toString() {
			String result = "";
			for (NodeTriple aMyAggregate : myAggregate) {
				if (aMyAggregate == null) {
					result += "null\n";
				} else {
					result += aMyAggregate.toString() + "\n";
				}
			}
			result += "\n\n";
			return result;
		}
	}

	public static final int K = 20;
	private final Node node;

	public NodeTriple[] getkBucket() {
		return kBucket;
	}

	private NodeTriple[] kBucket = new NodeTriple[K];

	public KBucket(Node node) {
		this.node = node;
	}

	public void addOrUpdateUnknownNodeTriple(NodeTriple nodeTriple)
			throws CloneNotSupportedException {
		if (nodeTriple.getNodeID().equals(node.getId())) {
			throw new KBucketUpdateException(
					"Tried to update my bucket with itself!");
		}
		int emptyBucket = -1;
		for (int i = 0; i < kBucket.length; i++) {
			if (kBucket[i] == null) {
				emptyBucket = i;
				continue;
			}
			if (nodeTriple.getNodeID().equals(kBucket[i].getNodeID())) {
				updateKnownNodeTriple(kBucket[i]);
				return;
			}
		}
		if (emptyBucket > -1) {
			kBucket[emptyBucket] = nodeTriple;
			kBucket[emptyBucket].setLastSeen(node.getClock().getTime());
		} else {
			NodeTriple leastRecentlySeen = this.iterator().next();
			if (node.sendPing(leastRecentlySeen.getAddress())) {
				updateKnownNodeTriple(leastRecentlySeen);
			} else {
				emptyBucket = removeNodeTriple(leastRecentlySeen);
				kBucket[emptyBucket] = nodeTriple.clone();
				kBucket[emptyBucket].setLastSeen(node.getClock().getTime());
			}
		}
	}

	public NodeTriple getNodeTriple(ID demandedNode) {
		for (NodeTriple aKBucket : kBucket) {
			if (aKBucket == null) {
				continue;
			}
			if (demandedNode.equals(aKBucket.getNodeID())) {
				return aKBucket;
			}
		}
		throw new NodeNotFoundException(
				"The requested node is not in this bucket!");
	}

	public Iterator<NodeTriple> iterator(Comparator<NodeTriple> cmp)
			throws CloneNotSupportedException {
		return new KBucketIterator(kBucket, cmp);
	}

	@Override
	public Iterator<NodeTriple> iterator() {
		try {
			return iterator(NodeTriple.LEAST_RECENTLY_SEEN_FIRST_COMPARATOR);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void print() {
		Iterator<NodeTriple> it = iterator();
		while (it.hasNext()) {
			NodeTriple nodeTriple = it.next();
			System.out.print(nodeTriple.getNodeID());
			if (it.hasNext()) {
				System.out.print(", ");
			}
		}
	}

	public int removeNodeTriple(NodeTriple nt) {
		for (int i = 0; i < kBucket.length; i++) {
			if (kBucket[i] == null) {
				continue;
			}
			if (kBucket[i].getNodeID().equals(nt.getNodeID())) {
				kBucket[i] = null;
				return i;
			}
		}
		throw new IllegalArgumentException(
				"NodeTriple wasn't found in this bucket");
	}

	public void updateKnownNodeTriple(NodeTriple nodeTriple) {
		getNodeTriple(nodeTriple.getNodeID()).setLastSeen(
				node.getClock().getTime());
	}
}
