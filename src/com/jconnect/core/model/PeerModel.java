package com.jconnect.core.model;

import com.jconnect.util.uuid.PeerID;

/**
 * Peer's model, used for persistence in SQLite database 
 *
 */
public class PeerModel {

	private PeerID peerID;
	
	public PeerModel(PeerID id) {
		peerID = id;
	}
	@Override
	public boolean equals(Object arg) {
		return ((PeerModel)arg).getPeerID().equals(peerID);
	}
	public PeerID getPeerID() {
		return peerID;
	}

}
