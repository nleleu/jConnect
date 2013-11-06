package com.jconnect.core.model;

import java.net.InetSocketAddress;

import com.jconnect.util.uuid.PeerID;

/**
 * Route's model
 * Represents a way of contact to reach a peer
 */
public class RouteModel {
	
	public enum TransportType
	{
		TCP,
		UDP,
		MULTICAST
	}
	
	private PeerID contactUUID;
	private TransportType transportType;
	private InetSocketAddress socketAddress;
	
	public long lastReception = 0;
	public long lastSend = 0;
	public int lastPing=-1;
	
	
	
	public RouteModel(PeerID contact, InetSocketAddress socketAddress,TransportType transportType)
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

	public PeerID getContactUUID() {
		return contactUUID;
	}

	public void setContactUUID(PeerID contactUUID) {
		this.contactUUID = contactUUID;
	}



	public TransportType getTransportType() {
		return transportType;
	}

	public void setTransportType(TransportType protocol) {
		this.transportType = protocol;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RouteModel){
			RouteModel r = (RouteModel)obj;
			return r.getSocketAddress().equals(socketAddress)&&r.contactUUID.equals(contactUUID)&&r.transportType.equals(transportType);
			
		}
		return false;
	}



}
