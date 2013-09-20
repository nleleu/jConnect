package com.jconnect.impl.peergroup;

import java.util.UUID;

public class NetPeerGroup extends PeerGroup {

	public static final UUID NETPEERGROUP_UUID = UUID.fromString("e5251da8-a745-42d7-ba88-6f6ffd86cce4");

	public NetPeerGroup() {
		super(NETPEERGROUP_UUID, null);
	}
}
