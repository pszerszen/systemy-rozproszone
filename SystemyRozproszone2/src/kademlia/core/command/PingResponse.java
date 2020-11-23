package kademlia.core.command;

/**
 * User: Piotrek Date: 16.11.13 Time: 15:25
 */
public class PingResponse extends Response {
	private boolean response;

	public PingResponse(boolean response) {
		this.response = response;
	}

	@Override
	public Response clone() throws CloneNotSupportedException {
		return new PingResponse(response);
	}

	public boolean isResponse() {
		return response;
	}
}
