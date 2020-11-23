package kademlia.core;

import javax.naming.OperationNotSupportedException;
import java.security.SecureRandom;
import java.util.Iterator;

/**
 * User: Piotrek Date: 15.11.13 Time: 15:45
 */
public class ID implements Iterable<Bit> {
	private class IDIterator implements Iterator<Bit> {
		private int index;
		private ID myID;

		public IDIterator(ID id) {
			this(id, 0);
		}

		public IDIterator(ID id, int startIndex) {
			if (startIndex < 0 || startIndex >= maxLength) {
				throw new IndexOutOfBoundsException(
						"Allowed maximum is less than " + maxLength);
			}
			index = startIndex;
			myID = id;
		}

		@Override
		public boolean hasNext() {
			return (index <= maxLength - 1);
		}

		@Override
		public Bit next() {
			return myID.getBitOnPosition(index++);
		}

		@Override
		public void remove() {
			throw new RuntimeException(new OperationNotSupportedException(
					"Remove operation is not intended for this iterator"));
		}
	}

	public static final int maxLength = 160;
	private final Bit[] id;

	/**
	 * Creates a random id using SecureRandom generator. Since the key is 160 bit
	 * length there is extremely small possibility to duplicate one.
	 */
	public ID() {
		id = new Bit[maxLength];
		SecureRandom r = new SecureRandom();
		for (int i = 0; i < id.length; i++) {
			id[i] = new Bit(r.nextBoolean());
		}
	}

	public ID(Bit[] id) {
		this.id = id;
	}

	/**
	 * The parameter for the ID is evaluated as a decimal number
	 *
	 * @param value
	 * 		to be evaluated
	 */
	@SuppressWarnings("unused")
	public ID(int value) {
		this(Integer.toBinaryString(value));
	}

	public ID(String value) {
		id = new Bit[maxLength];
		final int diff = maxLength - value.length();
		if (diff < 0) {
			throw new IllegalArgumentException(
					"The length of the value was larger than the allowed " +
					"maximum of " + maxLength);
		}

		for (int i = 0; i < maxLength; i++) {
			if (i < diff) {
				id[i] = new Bit(0);
			} else {
				id[i] = new Bit(new Integer(Character.toString(value.charAt(
						i - diff))));
			}
		}
	}

	private String binaryValue() {
		String result = "";
		Iterator<Bit> it = iteratorOnMostSignificantBit();
		while (it.hasNext()) {
			result += it.next();
		}
		return result;
	}

	private boolean equals(ID id) {
		for (int i = 0; i < maxLength; i++) {
			if (!id.getBitOnPosition(i).equals(getBitOnPosition(i))) {
				return false;
			}
		}
		return true;
	}

	private Bit getBitOnPosition(int index) {
		try {
			return id[index];
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new IndexOutOfBoundsException("Bad input for position");
		}
	}

	public ID applyXOR(ID id) {
		return ID.applyXOR(this, id);
	}

	public static ID applyXOR(ID id1, ID id2) {
		Bit[] result = new Bit[maxLength];
		for (int i = 0; i < maxLength; i++) {
			if (id1.getBitOnPosition(i).equals(id2.getBitOnPosition(i))) {
				result[i] = new Bit(0);
			} else {
				result[i] = new Bit(1);
			}
		}
		return new ID(result);
	}

	/**
	 * Return the offset of the most significant bit, seen from the right.
	 * Big-endian! This is not equal to the position of a bit, which is calculated
	 * from the right, as it is an array!
	 *
	 * @return as in description
	 */
	public int getRightOffsetOfMostSignificantBit() {
		int x = maxLength;
		Iterator<Bit> it = iterator();
		while (it.hasNext()) {
			Bit bit = it.next();
			x--;
			if (bit.isValue()) {
				break;
			}
		}
		return x;
	}

	@SuppressWarnings("unused")
	public int getValueOnPosition(int index) {
		return getBitOnPosition(index).getValue();
	}

	@Override
	public int hashCode() {
		return integerValue();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ID && equals((ID) obj);
	}

	@Override
	public String toString() {
		return binaryValue();
	}

	public int integerValue() {
		int result = 0;
		for (Iterator<Bit> it = iteratorOnMostSignificantBit(); it
				.hasNext(); ) {
			result *= 2;
			result += it.next().getValue();
		}
		return result;
	}

	@SuppressWarnings("unused")
	public Iterator<Bit> iterator(int startIndex) {
		return new IDIterator(this, startIndex);
	}

	@Override
	public Iterator<Bit> iterator() {
		return new IDIterator(this);
	}

	public Iterator<Bit> iteratorOnMostSignificantBit() {
		return new IDIterator(this, maxLength - 1 -
		                            getRightOffsetOfMostSignificantBit());
	}
}
