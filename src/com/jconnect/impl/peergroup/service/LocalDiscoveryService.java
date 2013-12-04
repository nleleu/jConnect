package com.jconnect.impl.peergroup.service;

import javax.swing.plaf.SliderUI;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.peergroup.AbstractPeerGroup;


public class LocalDiscoveryService extends Service{

	public LocalDiscoveryService(AbstractPeerGroup group) {
		super(group);
		// TODO Auto-generated constructor stub
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
		block();
		
	}


}
