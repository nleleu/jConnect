package com.jconnect.peergroup.peer;

public class PeerEvent {
	
	private Peer peer;
	private EVENT event;
	
	public enum EVENT{
		CONNECT,
		RECONNECT,
		DISCONNECT,
		
	}

	public PeerEvent(Peer peer, EVENT event) {
		this.peer = peer;
		this.event  = event;
		
	}

	public EVENT getEvent() {
		return event;
	}

	public Peer getPeer() {
		return peer;
	}

}
