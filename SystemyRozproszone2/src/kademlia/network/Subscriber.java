package kademlia.network;

import kademlia.core.Node;

public class Subscriber {
	private final Node node;
	private Address address;

	public Subscriber(Address address, Node node) {
		super();
		this.address = address;
		this.node = node;
	}

	public Subscriber(Node node) {
		super();
		this.node = node;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Node getNode() {
		return node;
	}
}
