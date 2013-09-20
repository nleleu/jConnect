package com.jconnect.core.model;

import java.util.UUID;

public class PeerModel {

	private UUID peerID;
	
	public PeerModel(UUID id) {
		peerID = id;
	}
	@Override
	public boolean equals(Object arg) {
		return ((PeerModel)arg).getPeerID().equals(peerID);
	}
	public UUID getPeerID() {
		return peerID;
	}

}
