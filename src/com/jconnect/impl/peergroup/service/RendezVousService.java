package com.jconnect.impl.peergroup.service;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.core.peergroup.peer.PeerListener;

public class RendezVousService extends Service implements PeerListener{

	public RendezVousService(AbstractPeerGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onPeerEvent(PeerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onHandleMessageEvent(MessageEvent m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean messageEventMatcher(MessageEvent message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onUpdade() {
		// TODO Auto-generated method stub
		
	}

	
	

	
	
	

}
