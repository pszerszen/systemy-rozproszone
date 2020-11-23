package kademlia.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import kademlia.core.ID;
import kademlia.core.KBucket;
import kademlia.core.Node;
import kademlia.core.NodeTriple;
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
import kademlia.exceptions.NodeNotFoundException;
import kademlia.exceptions.SubscriberNotFoundException;

public class SimulatedInternet implements INetwork {
	private int latestAddress;
	private ArrayList<Node> nodes;
	private Collection<Subscriber> subscribers;

	public SimulatedInternet() {
		subscribers = new ArrayList<Subscriber>();
		latestAddress = 0;
	}

	private Subscriber findSubscriber(Address address) {
		for (Subscriber subscriber : subscribers) {
			if (subscriber.getAddress().equals(address)) {
				return subscriber;
			}
		}
		throw new SubscriberNotFoundException(
				"Subscriber with such an Address wasn't found!");
	}

	private Node getNode(ID id) {
		for (Node node : nodes) {
			if (node.getId().equals(id)) {
				return node;
			}
		}
		throw new NodeNotFoundException("Requested id of Node not found.");
	}

	private void logoff(Address address) {
		subscribers.remove(findSubscriber(address));
	}

	public void findNode(ID polledNodeID, ID demandedNodeID,
	                     ID searcherNodeID) {
		Node polledNode = getNode(polledNodeID);
		Node demandedNode = getNode(demandedNodeID);
		Node searcherNode = getNode(searcherNodeID);

		for (Node node : nodes) {
			if (!node.getId().equals(polledNodeID)) {
				sendCommand(polledNode.generateNodeTriple().getAddress(),
				            new PingCommand(node.generateNodeTriple()));
			}
		}
		polledNode.printKBuckets();

		FindNodeResponse response = (FindNodeResponse) sendCommand(
				polledNode.generateNodeTriple().getAddress(),
				new FindNodeCommand(searcherNode.generateNodeTriple(),
				                    demandedNode.getId()));
		List<NodeTriple> closestNodes = response.getResponseList();

		System.out.println(Math.min(KBucket.K, nodes.size() - 1) == closestNodes
				.size());
		int originalKBucket = polledNode.getId().applyXOR(demandedNode.getId())
				.getRightOffsetOfMostSignificantBit();
		if (closestNodes.size() == KBucket.K) {
			for (NodeTriple nodeTriple : closestNodes) {
				System.out.println(nodeTriple);
			}
		} else {
			for (NodeTriple nodeTriple : polledNode.getKBucket(originalKBucket)
					.getkBucket()) {
				System.out.println(nodeTriple);
			}
		}
	}

	public void findValue(ID polledID, ID demandedKey, ID searcherID) {
		Node receiver = getNode(polledID);
		Node searcher = getNode(searcherID);

		sendCommand(receiver.generateNodeTriple().getAddress(), new PingCommand(
				searcher.generateNodeTriple()));

		FindValueResponse response = (FindValueResponse) sendCommand(
				receiver.generateNodeTriple().getAddress(),
				new FindValueCommand(searcher.generateNodeTriple(),
				                     demandedKey));

		if (response.getValue() != null) {
			System.out.println(response.getValue());
		} else if (response.getResponseList() != null) {
			for (NodeTriple nodeTriple : response.getResponseList()) {
				System.out.println(nodeTriple);
			}
		}
	}

	/**
	 * Generates given numbers of Nodes, each with unique ID.
	 *
	 * @param howMany
	 * 		number of Nodes to generate
	 */
	public ArrayList<Node> generateNodes(int howMany) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		int hm = howMany;
		HashSet<ID> set = new HashSet<ID>();
		for (int i = 0; i < hm; i++) {
			if (!set.add(new ID())) {
				hm++;
			}
		}
		for (ID id : set) {
			nodes.add(new Node(id));
		}
		return nodes;
	}

	@Override
	public void logoff(Subscriber sub) {
		logoff(sub.getAddress());
	}

	@Override
	public Address logon(Subscriber sub) {
		Address address = new Address(Integer.toString(latestAddress++));
		sub.setAddress(address);
		subscribers.add(sub);
		return address;
	}

	@Override
	public Response sendCommand(Address receiver, Command cmd) {
		Subscriber subscriber;
		try {
			subscriber = findSubscriber(receiver);
		} catch (SubscriberNotFoundException e) {
			if (cmd instanceof PingCommand) {
				return new PingResponse(false);
			} else {
				throw e;
			}
		}
		try {
			Response response;
			response = subscriber.getNode().receiveCommand(cmd.clone());
			return response.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			try {
				throw e;
			} catch (CloneNotSupportedException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		//Creating the "Internet" and 10000 nodes
		SimulatedInternet internet = new SimulatedInternet();
		internet.nodes = internet.generateNodes(10000);
		//Logging on  nodes
		for (Node node : internet.nodes) {
			node.networkLogon(internet);
		}

		// Finding some Node
		internet.findNode(internet.nodes.get(20).getId(), internet.nodes.get(
				2000).getId(), internet.nodes.get(8000).getId());

		// Storing some data on the "Net"
		internet.storeValue(internet.nodes.get(20).getId(), internet.nodes.get(
				200).getId(), "My very precious value", internet.nodes.get(666)
				                    .getId());

		// Trying to finding a value at wrong (out -> KBuckets)...
		internet.findValue(internet.nodes.get(21).getId(), internet.nodes.get(
				666).getId(), internet.nodes.get(100).getId());

		// and correct (out -> Value) Node
		internet.findValue(internet.nodes.get(20).getId(), internet.nodes.get(
				666).getId(), internet.nodes.get(100).getId());

		// Logging off nodes
		for (Node node : internet.nodes) {
			node.networkLogoff();
		}
	}

	public void storeValue(ID receiverID, ID senderID, Serializable value,
	                       ID key) {
		Node receiver = getNode(receiverID);
		Node sender = getNode(senderID);
		sendCommand(receiver.generateNodeTriple().getAddress(), new PingCommand(
				sender.generateNodeTriple()));

		StoreValueResponse response = (StoreValueResponse) sendCommand(
				receiver.generateNodeTriple().getAddress(),
				new StoreValueCommand(sender.generateNodeTriple(), key, value));

		if (response.isStored()) {
			System.out.println(value + " STORED");
		}
	}
}
