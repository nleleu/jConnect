package com.jconnect.core.model;

import com.jconnect.util.uuid.PeerGroupID;

/**
 * PeerGroup's model, used for persistence in SQLite database 
 *
 */
public class PeerGroupModel {

	private PeerGroupID groupID;
	

	public PeerGroupModel(PeerGroupID id) {
		groupID  = id;
	}


	public PeerGroupID getGroupID() {
		return groupID;
	}
	

	

}
