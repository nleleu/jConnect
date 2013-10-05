package com.jconnect.impl.peergroup;

import com.jconnect.util.uuid.PeerGroupID;

public class NetPeerGroup extends PeerGroup {

	public static final PeerGroupID NETPEERGROUP_UUID = new PeerGroupID("e5251da8-a745-42d7-ba88-6f6ffd86cce4");

	public NetPeerGroup() {
		super(NETPEERGROUP_UUID, null);
	}
}
