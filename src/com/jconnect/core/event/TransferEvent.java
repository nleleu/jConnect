package com.jconnect.core.event;

import java.net.SocketAddress;

import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;
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
	private Message message;
	private RouteModel route;
	private int tryCount = -1;
	private TransportType transportType;
	
	
	public TransferEvent(SocketAddress sa, State state, TransportType transportType)
	{
		this.state = state;
		this.setSocketAddress(sa);
		this.transportType=transportType;
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

	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}

	public void setRoute(RouteModel route) {
		this.route = route;
	}
	
	public RouteModel getRoute() {
		return route;
	}
	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

	public int getTryCount() {
		return tryCount ;
	}
	public TransportType getTransportType() {
		return transportType;
	}


	
	
}
