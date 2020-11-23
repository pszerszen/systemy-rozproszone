package kademlia.network;

/**
 * User: Piotrek Date: 15.11.13 Time: 18:51
 */
public class Address {
	private final String value;

	public Address(String value) {
		this.value = value;
	}

	private boolean equals(Address address) {
		return address.getValue().equals(value);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Address && equals((Address) obj);
	}

	@Override
	public String toString() {
		return super.toString() + " " + value;
	}

	public String getValue() {
		return value;
	}
}
