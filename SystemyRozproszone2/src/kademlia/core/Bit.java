package kademlia.core;

/**
 * User: Piotrek Date: 15.11.13 Time: 15:23
 */
public class Bit {
	private boolean value;

	public Bit(boolean value) {
		this.value = value;
	}

	public Bit(int value) {
		setValue(value);
	}

	public boolean equals(Bit bit) {
		return isValue() == bit.isValue();
	}

	public int getValue() {
		return value ? 1 : 0;
	}

	@Override
	public int hashCode() {
		return getValue();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Bit && equals((Bit) obj);
	}

	@Override
	public String toString() {
		return String.valueOf(getValue());
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public void setValue(int value) {
		switch (value) {
			case 0:
				setValue(false);
				break;
			case 1:
				setValue(true);
				break;
			default:
				throw new IllegalArgumentException(
						"Bits must be 0 or 1, but was " + value);
		}
	}
}
