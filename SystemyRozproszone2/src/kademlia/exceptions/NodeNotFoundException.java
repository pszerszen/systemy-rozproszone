package kademlia.exceptions;

public class NodeNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7103528584043510244L;

	public NodeNotFoundException(String message) {
		super(message);
	}
}