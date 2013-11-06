package com.jconnect.impl.peergroup;

import com.jconnect.impl.peergroup.service.TestService;
import com.jconnect.util.uuid.PeerGroupID;

public class NetPeerGroup extends PeerGroup {

	public static final PeerGroupID NETPEERGROUP_UUID = new PeerGroupID("peerGroupID:1475b87f-b0ee-4e54-b268-ad0de2eec1be");

	public NetPeerGroup() {
		super(NETPEERGROUP_UUID);
		addService(new TestService(this));
	}
}
