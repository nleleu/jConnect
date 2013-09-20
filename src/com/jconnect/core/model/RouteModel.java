package com.jconnect.core.model;

import java.net.InetSocketAddress;
import java.util.UUID;

public class RouteModel {
	
	public enum TransportType
	{
		TCP,
		UDP,
		MULTICAST
	}
	
	private UUID contactUUID;
	private TransportType transportType;
	private InetSocketAddress socketAddress;

	
	
	
	public RouteModel(UUID contact, InetSocketAddress socketAddress,TransportType transportType)
	{
		this.socketAddress = socketAddress;
		this.contactUUID = contact;
		this.transportType = transportType;

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



	public TransportType getTransportType() {
		return transportType;
	}

	public void setTransportType(TransportType protocol) {
		this.transportType = protocol;
	}



}
