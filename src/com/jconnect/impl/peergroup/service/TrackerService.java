package com.jconnect.impl.peergroup.service;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.core.peergroup.peer.PeerEvent;


public class TrackerService extends Service{

	public TrackerService(AbstractPeerGroup group) {
		super(group);
	}

	@Override
	protected void onHandleMessage(MessageEvent m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean messageMatcher(MessageEvent message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onUpdade() {
		// TODO Auto-generated method stub
		
	}
	
    

}