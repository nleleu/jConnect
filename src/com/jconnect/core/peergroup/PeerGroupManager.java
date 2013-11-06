package com.jconnect.core.peergroup;

import java.util.ArrayList;
import java.util.List;

import com.jconnect.JConnect;
import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.impl.peergroup.NetPeerGroup;
import com.jconnect.util.uuid.PeerID;

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

	
	public void addPeerEvent(PeerEvent pEvent) {
		for (int i = 0; i < peerGroups.size(); i++) {
			peerGroups.get(i).addPeerEvent(pEvent);
		}
		
	}
	
	
	public void sendMessage(Message message,List<PeerID> receivers, TransportType protocol) {
		jConnect.getGate().sendMessage(message, receivers, protocol);
	}

	public void addPeerRoutes(List<RouteModel> routes) {
		for (RouteModel routeModel : routes) {
			jConnect.getGate().addRoute(routeModel);
		}
	}

	public PeerID getPeerID() {
		return jConnect.getPeerID();
		
	}

	

}
