package com.jconnect.impl.peergroup.service;

import java.util.ArrayList;
import java.util.List;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.impl.message.RouteContentMessage;
import com.jconnect.util.uuid.PeerID;

public class LocalDiscoveryService extends Service {

	private int state = 0;

	public LocalDiscoveryService(AbstractPeerGroup group) {
		super(group);
	}

	@Override
	protected void onHandleMessageEvent(MessageEvent m) {
		ArrayList<RouteModel> routes = ((RouteContentMessage)m.getMessage().getContent()).getRoutes();
		getPeerGroup().addPeerRoutes(routes);
	}

	@Override
	public boolean messageEventMatcher(MessageEvent messageEvent) {
		return messageEvent.getState().equals(
				MessageEvent.State.MESSAGE_RECEIVED)
				&& (messageEvent.getMessage().getContent() instanceof RouteContentMessage);
	}

	@Override
	protected void onUpdade() {
		if (state < 10) {
			block(2000);
			state++;
			List<PeerID> connectedPeers = getPeerGroup().getConnectedPeers();
			RouteContentMessage content = new RouteContentMessage();
			for (PeerID peerID : connectedPeers) {
				content.addRoutes(group.getPeerRoute(peerID,
						null));
			}
			
			content.addRoutes(group.getPeerRoute(
					getPeerGroup().getPeerID(), null));
			Message m = new Message(group, content);
			sendMulticastMessage(m);
		} else {
			state = 0;
			List<PeerID> connectedPeers = getPeerGroup().getConnectedPeers();
			for (PeerID peerID : connectedPeers) {
				group.checkPeerUDPConnectivity(peerID, 5000,
						2000);

			}
		}

	}

}
