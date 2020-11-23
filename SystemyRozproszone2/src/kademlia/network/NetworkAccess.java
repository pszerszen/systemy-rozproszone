package kademlia.network;

/**
 * NetworkAccess is for the node, so it knows, what its network and address in
 * that network are.
 * <p/>
 *
 * @author Piotrek
 */
public class NetworkAccess {
	private Address address;
	private INetwork network;

	public NetworkAccess(INetwork network, Address address) {
		this.network = network;
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	public INetwork getNetwork() {
		return network;
	}
}
