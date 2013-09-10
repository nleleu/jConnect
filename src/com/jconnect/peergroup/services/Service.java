package com.jconnect.peergroup.services;

import com.jconnect.peergroup.PeerGroup;



public abstract class Service extends AbstractService{
	
	
	public PeerGroup getPeerGroup(){
		return (PeerGroup) group;
	}

}
