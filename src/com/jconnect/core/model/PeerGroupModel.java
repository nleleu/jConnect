package com.jconnect.core.model;

import java.util.UUID;

//Classe plus legere que celle du package peerGroup, voir si on peut pas les lier par un héritage ou les fusionner
public class PeerGroupModel {

	private UUID groupID;
	

	public PeerGroupModel(UUID id) {
		groupID  = id;
	}


	public UUID getGroupID() {
		return groupID;
	}
	

	

}
