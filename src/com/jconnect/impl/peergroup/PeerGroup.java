package com.jconnect.impl.peergroup;

import java.util.UUID;

import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.impl.peergroup.service.ConnectivityService;
import com.jconnect.impl.peergroup.service.DiscoveryService;
import com.jconnect.impl.peergroup.service.InboxService;
import com.jconnect.impl.peergroup.service.OutboxService;
import com.jconnect.impl.peergroup.service.RendezVousService;

public abstract class PeerGroup extends AbstractPeerGroup {

	private InboxService inboxService;
	private OutboxService outboxService;
	private DiscoveryService discoveryService;
	private ConnectivityService connectivityService;
	private RendezVousService rendezVousService;


	
	
	public PeerGroup(UUID uuid, AbstractPeerGroup pGroup) {
		super(uuid, pGroup);
		inboxService = new InboxService();
		addService(inboxService);
		outboxService = new OutboxService();
		addService(outboxService);
		addService(new OutboxService());
		discoveryService = new DiscoveryService();
		addService(discoveryService);
		connectivityService = new ConnectivityService();
		addService(new ConnectivityService());
		rendezVousService = new RendezVousService();
		addService(new RendezVousService());
	}
	
	
	
	
	
	// ##################### GETTER - SETTER #######################
	public InboxService getInboxService() {
		return inboxService;
	}
	public OutboxService getOutboxService() {
		return outboxService;
	}
	public DiscoveryService getDiscoveryService() {
		return discoveryService;
	}
	public ConnectivityService getConnectivityService() {
		return connectivityService;
	}
	public RendezVousService getRendezVousService() {
		return rendezVousService;
	}
	

}
