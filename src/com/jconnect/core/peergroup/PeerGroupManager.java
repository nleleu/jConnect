package com.jconnect.core.peergroup;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.jconnect.JConnect;
import com.jconnect.core.IGate;
import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.util.uuid.PeerGroupID;

/**
 * PeerGroupManager
 * 
 */
public class PeerGroupManager {
	
	private List<AbstractPeerGroup> peerGroups = new ArrayList<AbstractPeerGroup>();
	private JConnect jConnect;
	
	public PeerGroupManager(JConnect jConnect) {
		this.jConnect = jConnect;
		
		
	}
	
	public AbstractPeerGroup newGroupInstance(Class<?> abstractPeerGroupClass, PeerGroupID peerGroupID) {
		try {
			AbstractPeerGroup pg = (AbstractPeerGroup) abstractPeerGroupClass.getConstructor(PeerGroupID.class, IGate.class).newInstance(peerGroupID, jConnect.getGate());
			peerGroups.add(pg);
			return pg;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
		
	}


	
	public void stopAllGroup() {
		for (AbstractPeerGroup peerGroup : peerGroups) {
			peerGroup.stop();
		}
	}
	
	public void startAllGroup() {
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

	
	public void addPeerEvent(PeerEvent pEvent) {
		for (int i = 0; i < peerGroups.size(); i++) {
			peerGroups.get(i).addPeerEvent(pEvent);
		}
		
	}
	
	
	

	

	

}
