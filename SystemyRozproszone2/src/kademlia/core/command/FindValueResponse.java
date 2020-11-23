package kademlia.core.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kademlia.core.NodeTriple;

/**
 * User: Piotrek Date: 16.11.13 Time: 16:19
 */
public class FindValueResponse extends Response {
	private final List<NodeTriple> responseList;
	private final Serializable value;

	public FindValueResponse(List<NodeTriple> responseList) {
		super();
		this.responseList = responseList;
		value = null;
	}

	public FindValueResponse(Serializable value) {
		super();
		this.value = value;
		responseList = null;
	}

	@Override

	public Response clone() throws CloneNotSupportedException {
		List<NodeTriple> newList;
		Serializable newValue;
		if (responseList != null) {
			newList = new ArrayList<NodeTriple>();
			for (NodeTriple nodeTriple : responseList) {
				newList.add(nodeTriple.clone());
			}
			return new FindValueResponse(newList);
		}
		if (value != null) {
			try {
				//Imitating download
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(value);
				oos.flush();
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(baos.toByteArray()));
				newValue = (Serializable) ois.readObject();
			} catch (Exception e) {
				throw new RuntimeException(
						"The cloning of the stored value wasn't feasible", e);
			}
			return new FindValueResponse(newValue);
		}
		throw new RuntimeException(
				"Couldn't clone, since response list and stored value are " +
				"null. At least " + "one has to be initialized!");
	}

	public List<NodeTriple> getResponseList() {
		return responseList;
	}

	public Serializable getValue() {
		return value;
	}
}
