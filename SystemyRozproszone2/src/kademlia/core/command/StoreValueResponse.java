package kademlia.core.command;

/**
 * @author Piotrek
 */
public class StoreValueResponse extends Response {
	private final boolean stored;

	public StoreValueResponse(boolean stored) {
		this.stored = stored;
	}

	@Override
	public Response clone() throws CloneNotSupportedException {
		return new StoreValueResponse(stored);
	}

	public boolean isStored() {
		return stored;
	}
}
