package com.jconnect.core.peergroup;

import java.util.List;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.impl.peergroup.NetPeerGroup;


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


	public void addMessageEvent(MessageEvent mEvent) {
		for (int i = 0; i < peerGroups.size(); i++) {
			if(peerGroups.get(i).getuUID().equals(mEvent.getMessage().getGroup())){
				peerGroups.get(i).addMessageEvent(mEvent);
			}
		}
		
	}

}
