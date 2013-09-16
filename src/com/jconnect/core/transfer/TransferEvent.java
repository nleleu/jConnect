package com.jconnect.core.transfer;

import java.net.SocketAddress;

public abstract class  TransferEvent {


	
	private SocketAddress socketAddress;
	
	public static final int SOCKET_STATUS_OK = 0;
	public static final int SOCKET_STATUS_TIMEOUT = 1;
	public static final int SOCKET_STATUS_CLOSED = 2;
	public static final int SOCKET_STATUS_UNKNOWN_ERROR = 3;
	
	public TransferEvent(SocketAddress sa)
	{
		this.setSocketAddress(sa);
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}
	
	
	
}
