package kademlia.network;

import kademlia.core.command.Command;
import kademlia.core.command.Response;

/**
 * User: Piotrek Date: 16.11.13 Time: 17:35
 */
public interface INetwork {
	public void logoff(Subscriber sub);

	public Address logon(Subscriber sub);

	public Response sendCommand(Address receiver, Command cmd);
}
