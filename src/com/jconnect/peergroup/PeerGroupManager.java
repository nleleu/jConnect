package com.jconnect.peergroup;

import java.util.List;


public class PeerGroupManager {
	
	private AbstractPeerGroup netPeerGroup;
	private List<AbstractPeerGroup> peerGroups;
	
	public PeerGroupManager() {
		
		netPeerGroup = new NetPeerGroup();
		peerGroups.add(netPeerGroup);
		
	}

	
	public void stop() {
		for (AbstractPeerGroup peerGroup : peerGroups) {
			peerGroup.stop();
		}
	}
	
	public void start() {
		for (AbstractPeerGroup peerGroup : peerGroups) {
			peerGroup.start();
		}
	}

}
