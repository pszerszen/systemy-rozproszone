package kademlia.core.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import kademlia.core.ID;
import kademlia.core.NodeTriple;

/**
 * @author Piotrek
 */
public class StoreValueCommand extends Command {
	private final ID key;
	private final Serializable value;

	public StoreValueCommand(NodeTriple sender, ID key, Serializable value) {
		super(sender);
		this.key = key;
		this.value = value;
	}

	@Override
	public Command clone() throws CloneNotSupportedException {
		Serializable newValue;
		if (value != null) {
			try {
				//Imitating upload
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
			return new StoreValueCommand(sender.clone(), key, newValue);
		}
		throw new RuntimeException(
				"Couldn't clone, since stored value is null.");
	}

	public ID getKey() {
		return key;
	}

	public Serializable getValue() {
		return value;
	}
}
