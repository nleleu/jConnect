package com.jconnect.impl.peergroup;

import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.impl.peergroup.service.ConnectivityService;
import com.jconnect.impl.peergroup.service.LocalDiscoveryService;
import com.jconnect.impl.peergroup.service.RendezVousService;
import com.jconnect.util.uuid.PeerGroupID;

public abstract class PeerGroup extends AbstractPeerGroup {

	private LocalDiscoveryService discoveryService;
	private ConnectivityService connectivityService;
	private RendezVousService rendezVousService;


	
	
	public PeerGroup(PeerGroupID uuid) {
		super(uuid);
		discoveryService = new LocalDiscoveryService(this);
		addService(discoveryService);
		connectivityService = new ConnectivityService(this);
		addService(connectivityService);
		rendezVousService = new RendezVousService(this);
		addService(rendezVousService);
	}
	
	
	
	
	
	// ##################### GETTER - SETTER #######################
	public LocalDiscoveryService getDiscoveryService() {
		return discoveryService;
	}
	public ConnectivityService getConnectivityService() {
		return connectivityService;
	}
	public RendezVousService getRendezVousService() {
		return rendezVousService;
	}
	

}
