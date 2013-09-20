package com.jconnect.core.peergroup.services;

import com.jconnect.core.peergroup.AbstractPeerGroup;


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
