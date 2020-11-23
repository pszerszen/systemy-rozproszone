package kademlia.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kademlia.core.command.Command;
import kademlia.core.command.FindNodeCommand;
import kademlia.core.command.FindNodeResponse;
import kademlia.core.command.FindValueCommand;
import kademlia.core.command.FindValueResponse;
import kademlia.core.command.PingCommand;
import kademlia.core.command.PingResponse;
import kademlia.core.command.Response;
import kademlia.core.command.StoreValueCommand;
import kademlia.core.command.StoreValueResponse;
import kademlia.network.Address;
import kademlia.network.INetwork;
import kademlia.network.NetworkAccess;
import kademlia.network.Subscriber;
import kademlia.utils.LogicalClock;

/**
 * User: Piotrek Date: 16.11.13 Time: 13:29
 */
//@SuppressWarnings("unused")
public class Node {
	public final static int ALPHA = 3;
	private final LogicalClock clock = new LogicalClock();
	private final ID id;
	private final KeyValueStore keyValueStore = new KeyValueStore();
	private KBucket[] kBuckets = new KBucket[160];
	private NetworkAccess netAccess;

	public Node(ID id) {
		this.id = id;
	}

	private FindNodeResponse answerFindNodeCommand(FindNodeCommand cmd)
			throws CloneNotSupportedException {
		List<NodeTriple> responseList = findKNearestNodesToGivenID(
				cmd.getDemandedNodeID());
		return new FindNodeResponse(responseList);
	}

	private FindValueResponse answerFindValueCommand(FindValueCommand cmd)
			throws CloneNotSupportedException {
		if (keyValueStore.isKeyStored(cmd.getKey())) {
			return new FindValueResponse(keyValueStore.getValue(cmd.getKey()));
		} else {
			return new FindValueResponse(findKNearestNodesToGivenID(
					cmd.getKey()));
		}
	}

	private PingResponse answerPing() {
		return new PingResponse(true);
	}

	/**
	 * Removes the own NodeTriple from a response list
	 */
	private List<NodeTriple> deleteOneselfFromList(
			List<NodeTriple> listToRemoveFrom) {
		listToRemoveFrom.remove(generateNodeTriple());
		return listToRemoveFrom;
	}

	@SuppressWarnings("unused")
	private List<NodeTriple> findAlphaNearestNodesToGivenID(ID demandedNodeID)
			throws CloneNotSupportedException {
		return findNearestNodesToGivenID(demandedNodeID, ALPHA);
	}

	private int findKForNodeID(ID nodeID) {
		ID xorDiff = ID.applyXOR(id, nodeID);
		return xorDiff.getRightOffsetOfMostSignificantBit();
	}

	private List<NodeTriple> findKNearestNodesToGivenID(ID demandedNodeID)
			throws CloneNotSupportedException {
		return findNearestNodesToGivenID(demandedNodeID, KBucket.K);
	}

	private KBucket getKBucket(ID nodeID) {
		return getKBucket(findKForNodeID(nodeID));
	}

	private Response sendCommand(Address receiver, Command cmd) {
		return netAccess.getNetwork().sendCommand(receiver, cmd);
	}

	private FindNodeResponse sendFindNode(Address receiver, ID demandedID) {
		FindNodeCommand cmd = new FindNodeCommand(generateNodeTriple(),
		                                          demandedID);
		return (FindNodeResponse) sendCommand(receiver, cmd);
	}

	@SuppressWarnings("unused")
	private List<NodeLookupSearchEntry> sendMultipleFindNodeRequests(
			ArrayList<NodeTriple> recipients, ID demandedID) {
		List<NodeLookupSearchEntry> resultList =
				new ArrayList<NodeLookupSearchEntry>();
		for (NodeTriple recipient : recipients) {
			FindNodeResponse resp = sendFindNode(recipient.getAddress(),
			                                     demandedID);
			resultList.addAll(NodeLookupSearchEntry.convertToNodeLookupList(
					resp.getResponseList()));
		}
		return resultList;
	}

	private StoreValueResponse storeValue(Command cmd) {
		if (!keyValueStore.isKeyStored(((StoreValueCommand) cmd).getKey())) {
			keyValueStore.putValue(((StoreValueCommand) cmd).getKey(),
			                       ((StoreValueCommand) cmd).getValue());
			return new StoreValueResponse(true);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void updateKBucket(NodeTriple senderNodeTriple)
			throws CloneNotSupportedException {
		KBucket currentKBucket = getKBucket(senderNodeTriple.getNodeID());
		currentKBucket.addOrUpdateUnknownNodeTriple(senderNodeTriple);
	}

	private List<NodeTriple> findNearestNodesToGivenID(ID demandedNodeID,
	                                                   int amount)
			throws CloneNotSupportedException {
		List<NodeTriple> responseList = new ArrayList<NodeTriple>(amount);
		int kOfNearestBucket = findKForNodeID(demandedNodeID);

		Iterator<NodeTriple> it = getKBucket(kOfNearestBucket).iterator(
				NodeTriple.NEAREST_TO_GIVEN_ID_COMPARATOR(demandedNodeID));
		while (it.hasNext() && responseList.size() < amount) {
			responseList.add(it.next());
		}
		int i = kOfNearestBucket;
		while (--i >= 0) {
			it = getKBucket(i).iterator();
			while (it.hasNext()) {
				responseList.add(it.next());
			}
		}
		i = kOfNearestBucket;
		while (responseList.size() < amount && ++i < ID.maxLength) {
			it = getKBucket(i).iterator();
			while (it.hasNext()) {
				responseList.add(it.next());
			}
		}
		Collections.sort(responseList,
		                 NodeTriple.NEAREST_TO_GIVEN_ID_COMPARATOR(
				                 demandedNodeID));
		responseList = responseList.subList(0, Math.min(responseList.size(),
		                                                amount));

		if (responseList.size() > amount) {
			throw new AssertionError(
					"Tried to return more NodeTriples (" + responseList
							.size() + ") than demanded (" + amount + ") - " +
					"constraint wasn't " +
					"hold!");
		}
		return responseList;
	}

	public NodeTriple generateNodeTriple() {
		return new NodeTriple(netAccess.getAddress(), id, clock.getTime());
	}

	public Subscriber generateSubscriber() {
		return new Subscriber(netAccess.getAddress(), this);
	}

	public LogicalClock getClock() {
		return clock;
	}

	public ID getId() {
		return id;
	}

	public KBucket getKBucket(int i) {
		if (i >= 0 && i < ID.maxLength) {
			if (kBuckets[i] == null) {
				kBuckets[i] = new KBucket(this);
			}
			return kBuckets[i];
		}
		throw new IllegalArgumentException(
				"Bucket index must be smaller than " + ID.maxLength);
	}

	public void networkLogoff() {
		netAccess.getNetwork().logoff(generateSubscriber());
		netAccess = null;
	}

	public void networkLogon(INetwork iNetwork) {
		Address tempAddress = iNetwork.logon(new Subscriber(this));
		netAccess = new NetworkAccess(iNetwork, tempAddress);
	}

	public void nodeLookup(ID demandedID) throws CloneNotSupportedException {

		// TODO Need sortable flexible list of NodeLookupSearchEntries
		List<NodeTriple> startingNodes = findNearestNodesToGivenID(demandedID,
		                                                           ALPHA);
		Set<NodeLookupSearchEntry> receivedNodesSet =
				new HashSet<NodeLookupSearchEntry>();
		for (NodeTriple startingNode : startingNodes) {
			FindNodeResponse response = sendFindNode(startingNode.getAddress(),
			                                         demandedID);
			receivedNodesSet.addAll(
					NodeLookupSearchEntry.convertToNodeLookupList(
							deleteOneselfFromList(response.getResponseList()
							                     )));
		}

		Comparator<NodeLookupSearchEntry> comparator =
				NodeLookupSearchEntry.NEAREST_TO_GIVEN_ID_COMPARATOR(
						demandedID);
		while (true) {
			List<NodeLookupSearchEntry> receivedNodesList =
					new ArrayList<NodeLookupSearchEntry>(receivedNodesSet);
			Collections.sort(receivedNodesList, comparator);
			List<NodeLookupSearchEntry> kNearestReceivedNodesList =
					receivedNodesList.subList(0, Math.min(KBucket.K,
					                                      receivedNodesList
							                                      .size()));
			Set<NodeLookupSearchEntry> receivedNodesInCurrentRoundSet =
					new HashSet<NodeLookupSearchEntry>();
			int numberQueried = 0;
			Iterator<NodeLookupSearchEntry> it =
					kNearestReceivedNodesList.iterator();
			while (it.hasNext() && numberQueried < Node.ALPHA) {
				NodeLookupSearchEntry next = it.next();
				if (!next.isAlreadyQueried()) {
					next.setAlreadyQueried();
					numberQueried++;
					FindNodeResponse response = sendFindNode(
							next.getFoundTriple().getAddress(), demandedID);
					receivedNodesInCurrentRoundSet.addAll(
							NodeLookupSearchEntry.convertToNodeLookupList(
									deleteOneselfFromList(
											response.getResponseList())));
				}
			}
			if (!it.hasNext() && numberQueried == 0) {
				break;
			}
			List<NodeLookupSearchEntry> receivedNodesInCurrentRoundList =
					new ArrayList<NodeLookupSearchEntry>(
							receivedNodesInCurrentRoundSet);
			Collections.sort(receivedNodesInCurrentRoundList, comparator);
			if (receivedNodesInCurrentRoundList.isEmpty() || comparator
					                                                 .compare(
					receivedNodesInCurrentRoundList.get(0),
					kNearestReceivedNodesList.get(0)) >= 0) {
				while (it.hasNext()) {
					NodeLookupSearchEntry next = it.next();
					if (!next.isAlreadyQueried()) {
						next.setAlreadyQueried();
						FindNodeResponse response = sendFindNode(
								next.getFoundTriple().getAddress(),
								demandedID);
						receivedNodesInCurrentRoundSet.addAll(
								NodeLookupSearchEntry.convertToNodeLookupList(
										deleteOneselfFromList(
												response.getResponseList())));
					}
				}
			}
			receivedNodesSet.addAll(receivedNodesInCurrentRoundSet);
			// TODO If return value is false, one could directly stop recursion
			receivedNodesList = null;
		}

		System.out.println("Found these nodes:");
		for (NodeLookupSearchEntry entry : receivedNodesSet) {
			System.out.println(
					entry.getFoundTriple().getNodeID().integerValue());
		}
	}

	public void printKBuckets() {
		System.out.println("KBuckets for Node " + getId().toString());
		for (int i = 0; i < ID.maxLength; i++) {
			if (kBuckets[i] == null) {
				continue;
			}
			System.out.print("KBucket " + i + ":\t");
			kBuckets[i].print();
			System.out.println();
		}
		System.out.println();
	}

	public Response receiveCommand(Command cmd)
			throws CloneNotSupportedException {
		updateKBucket(cmd.getSender());
		if (cmd instanceof PingCommand) {
			return answerPing();
		} else if (cmd instanceof FindNodeCommand) {
			return answerFindNodeCommand((FindNodeCommand) cmd);
		} else if (cmd instanceof FindValueCommand) {
			return answerFindValueCommand((FindValueCommand) cmd);
		} else if (cmd instanceof StoreValueCommand) {
			return storeValue(cmd);
		}
		throw new RuntimeException(
				"The command class didn't match any known commands");
	}

	public boolean sendPing(Address receiver) {
		Command cmd = new PingCommand(generateNodeTriple());
		PingResponse resp = (PingResponse) sendCommand(receiver, cmd);
		return resp.isResponse();
	}

	public long whenHaveYouLastSeen(ID demandedNode) {
		return getKBucket(demandedNode).getNodeTriple(demandedNode)
				.getLastSeen();
	}
}
