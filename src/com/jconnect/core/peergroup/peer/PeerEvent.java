package com.jconnect.core.peergroup.peer;

import com.jconnect.util.uuid.PeerID;

public class PeerEvent {
	
	private PeerID peerId;
	private EVENT event;
	
	public enum EVENT{
		CONNECT,
		RECONNECT,
		DISCONNECT,
		
	}

	public PeerEvent(PeerID peerId, EVENT event) {
		this.peerId = peerId;
		this.event  = event;
		
	}

	public EVENT getEvent() {
		return event;
	}

	public PeerID getPeerId() {
		return peerId;
	}

}
