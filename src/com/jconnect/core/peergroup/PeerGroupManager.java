package com.jconnect.core.peergroup;

import java.util.List;

import com.jconnect.JConnect;
import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.message.Message;
import com.jconnect.impl.peergroup.NetPeerGroup;
import com.jconnect.util.uuid.PeerID;

/**
 * PeerGroupManager
 * 
 */
public class PeerGroupManager {
	
	private AbstractPeerGroup netPeerGroup;
	private List<AbstractPeerGroup> peerGroups;
	private JConnect jConnect;
	
	public PeerGroupManager(JConnect jConnect) {
		this.jConnect = jConnect;
		netPeerGroup = new NetPeerGroup();
		addPeerGroup(netPeerGroup);
		
	}
	
	public void addPeerGroup(AbstractPeerGroup pg)
	{
		pg.setPeerGroupManager(this);
		peerGroups.add(pg);
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

	public void sendMessage(String m,List<PeerID> receivers) {
		jConnect.getGate().sendMessage(m, receivers);
		
	}

}
