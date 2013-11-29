package com.jconnect.impl.peergroup;

import com.jconnect.core.IGate;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.impl.peergroup.service.TestService;
import com.jconnect.util.uuid.PeerGroupID;

public class NetPeerGroup extends PeerGroup {

	
	public NetPeerGroup(PeerGroupID uuid, IGate gate) {
		super(uuid, gate);
	}

	public static final PeerGroupID NETPEERGROUP_UUID = new PeerGroupID("peerGroupID:1475b87f-b0ee-4e54-b268-ad0de2eec1be");

	
	

	
	
}
