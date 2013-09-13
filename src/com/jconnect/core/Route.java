package com.jconnect.core;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Route {
	
	private InetSocketAddress socketAddress;
	private UUID contactUUID;
	private int ping;
	
	public Route(UUID contact, InetSocketAddress socketAddress)
	{
		this(contact,socketAddress,-1);
	}
	
	public Route(UUID contact, InetSocketAddress socketAddress,int ping)
	{
		this.socketAddress = socketAddress;
		this.contactUUID = contact;
		this.ping = ping;
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

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

}
