package com.jconnect.peergroup.services;

import com.jconnect.peergroup.AbstractPeerGroup;
import com.jconnect.peergroup.peer.PeerListener;


public abstract class AbstractService {

	protected long nextExecutionTime = 0;
	protected AbstractPeerGroup group;

	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	
	public void update() {
		nextExecutionTime = action()+System.currentTimeMillis();
	}

	public boolean needsUpdate(){
		if(System.currentTimeMillis()>=nextExecutionTime){
			return true;
		}
		return false;
	}

	public void setPeerGroup(AbstractPeerGroup abstractPeerGroup) {
		this.group = abstractPeerGroup;
	}

	protected abstract int action();

		
	

}
