package kademlia.utils;

public class LogicalClock {
	private long time;

	public LogicalClock() {
		this.time = 0;
	}

	public long getTime() {
		return time++;
	}
}
