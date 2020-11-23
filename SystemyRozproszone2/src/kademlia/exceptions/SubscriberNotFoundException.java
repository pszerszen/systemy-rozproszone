package kademlia.exceptions;

/**
 * User: Piotrek Date: 15.11.13 Time: 20:18
 */
public class SubscriberNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 3119268496091836675L;

	public SubscriberNotFoundException(String message) {
		super(message);
	}
}
