package com.jconnect.core.model;

import java.net.InetSocketAddress;

import com.google.gson.JsonObject;
import com.jconnect.util.uuid.PeerID;

/**
 * Route's model
 * Represents a way of contact to reach a peer
 */
public class RouteModel {
	
	private static final String TAG_HOST = "host";
	private static final String TAG_PORT = "port";
	private static final String TAG_PROTOCOLE = "proto";
	private static final String TAG_PEER_ID = "peerID";
	
	
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
	
	public RouteModel(JsonObject json)
	{
		this.socketAddress = new InetSocketAddress(json.get(TAG_HOST).getAsString(), json.get(TAG_PORT).getAsInt());
		this.contactUUID = new PeerID(json.get(TAG_PEER_ID).getAsString());
		String protocole = json.get(TAG_PROTOCOLE).getAsString();
		if(protocole.equals(TransportType.TCP.name())) {
			transportType = TransportType.TCP;
		}else if(protocole.equals(TransportType.MULTICAST.name())) {
			transportType = TransportType.MULTICAST;
		}else if(protocole.equals(TransportType.UDP.name())) {
			transportType = TransportType.UDP;
		}

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

	public JsonObject toJson(){
		JsonObject json = new JsonObject();
		json.addProperty(TAG_PEER_ID, contactUUID.toString());
		json.addProperty(TAG_PROTOCOLE, transportType.name());
		json.addProperty(TAG_HOST, socketAddress.getHostString());
		json.addProperty(TAG_PORT, socketAddress.getPort());
		return json;
	}
	
	


}
