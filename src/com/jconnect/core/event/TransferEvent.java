package com.jconnect.core.event;

import java.net.SocketAddress;
/**
 * Contains information about success or failure of a network I/O operation
 */
public class TransferEvent {


	
	private SocketAddress socketAddress;
	
	public enum State{
		MESSAGE_RECEIVED,
		SEND_SUCCESS,
		SEND_FAIL,
		INPUT_TIME_OUT, SOCKET_CLOSED
		
	}
	
	public Exception error;
	private State state;
	private String data;
	
	
	public TransferEvent(SocketAddress sa, State state)
	{
		this.state = state;
		this.setSocketAddress(sa);
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public State getState() {
		return state;
	}

	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	
	
}
