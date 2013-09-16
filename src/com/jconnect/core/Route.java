package com.jconnect.core;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Route {
	
	public enum Protocol
	{
		TCP,
		UDP
	}
	private InetSocketAddress socketAddress;
	private UUID contactUUID;
	private Protocol protocol;
	private boolean isLocal;
	
	
	
	public Route(UUID contact, InetSocketAddress socketAddress,Protocol protocol, boolean isLocal)
	{
		this.socketAddress = socketAddress;
		this.contactUUID = contact;
		this.protocol = protocol;
		this.isLocal = isLocal;
	}

	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(InetSocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public UUID getContactUUID() {
		return contactUUID;
	}

	public void setContactUUID(UUID contactUUID) {
		this.contactUUID = contactUUID;
	}



	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

}
