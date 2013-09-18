package com.jconnect.peergroup.peer;

import java.util.UUID;

public class Peer {

	private UUID peerID;
	private long startDate;
	
	@Override
	public boolean equals(Object arg) {
		return ((Peer)arg).getPeerID().equals(peerID);
	}
	public UUID getPeerID() {
		return peerID;
	}
	public long getStartDate() {
		return startDate;
	}
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}
}
