package kademlia.exceptions;

public class KBucketUpdateException extends RuntimeException {
	private static final long serialVersionUID = -8901760276961026930L;

	public KBucketUpdateException(String message) {
		super(message);
	}
}
