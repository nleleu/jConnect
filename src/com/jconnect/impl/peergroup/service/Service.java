package com.jconnect.impl.peergroup.service;

import com.jconnect.core.peergroup.services.AbstractService;
import com.jconnect.impl.peergroup.PeerGroup;



public abstract class Service extends AbstractService{
	
	
	public PeerGroup getPeerGroup(){
		return (PeerGroup) group;
	}

}
