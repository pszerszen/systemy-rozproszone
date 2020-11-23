package utils;

public enum PORTS {
	TCPMsg(60666), UDPMsgC(60668), UDPMsgS(60667);
	private final int port;

	private PORTS(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

}
