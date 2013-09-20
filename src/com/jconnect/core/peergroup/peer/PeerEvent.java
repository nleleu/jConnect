package com.jconnect.core.peergroup.peer;

import com.jconnect.core.model.PeerModel;

public class PeerEvent {
	
	private PeerModel peer;
	private EVENT event;
	
	public enum EVENT{
		CONNECT,
		RECONNECT,
		DISCONNECT,
		
	}

	public PeerEvent(PeerModel peer, EVENT event) {
		this.peer = peer;
		this.event  = event;
		
	}

	public EVENT getEvent() {
		return event;
	}

	public PeerModel getPeer() {
		return peer;
	}

}
