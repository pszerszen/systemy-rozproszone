package kademlia.core;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User: Piotrek Date: 15.11.13 Time: 18:45
 */
public class KeyValueStore {
	private HashMap<ID, Serializable> map;

	public KeyValueStore() {
		map = new HashMap<ID, Serializable>();
	}

	public boolean isKeyStored(ID key) {
		return map.containsKey(key);
	}

	public Serializable getValue(ID key) {
		return map.get(key);
	}

	public void putValue(ID key, Serializable value) {
		map.put(key, value);
	}
}
