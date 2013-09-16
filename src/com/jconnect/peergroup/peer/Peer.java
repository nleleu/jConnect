package com.jconnect.peergroup.peer;

import java.util.UUID;

public class Peer {

	private UUID peerID;
	
	@Override
	public boolean equals(Object arg) {
		return ((Peer)arg).getPeerID().equals(peerID);
	}
	public UUID getPeerID() {
		return peerID;
	}
}
