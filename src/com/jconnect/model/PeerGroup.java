package com.jconnect.model;

import java.util.ArrayList;
import java.util.UUID;

import com.jconnect.peergroup.peer.Peer;

//Classe plus legere que celle du package peerGroup, voir si on peut pas les lier par un héritage ou les fusionner
public class PeerGroup {

	private UUID groupID;
	private ArrayList<Peer> members;
	

	public UUID getGroupID() {
		return groupID;
	}
	
	public void addMember(Peer p)
	{
		members.add(p);
	}

	public ArrayList<Peer> getMembers() {
		return members;
	}

	

}
