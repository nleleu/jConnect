package com.jconnect.peergroup.services;

import java.util.ArrayList;
import java.util.List;

import com.jconnect.peergroup.peer.Peer;
import com.jconnect.peergroup.peer.PeerEvent;
import com.jconnect.peergroup.peer.PeerListener;

public class RendezVousService extends Service implements PeerListener{

	
	private boolean isRendezVous = false;
	private List<Peer> rendezVousPeer = new ArrayList<Peer>();
	private List<Peer> connectedPeer = new ArrayList<Peer>();;
	
	public boolean isRendezVous(){
		return isRendezVous;		
	}
	
	private boolean isConnectedToRendezVous() {
		return rendezVousPeer.size()>0;
	}
	
	@Override
	protected int action() {
		if(!isRendezVous()){
			if(isConnectedToRendezVous()){
				//RAS
			}else{
				
			}
			
		}
		return 0;
	}

	@Override
	public void onPeerEvent(PeerEvent event) {
		switch (event.getEvent()) {
		case CONNECT:
			if(event.getPeer().isRendezVous()){
				rendezVousPeer.add(event.getPeer());
			}else{
				connectedPeer.add(event.getPeer());
			}
			
			break;
		case DISCONNECT:
			for (Peer mPeer : connectedPeer) {
				if(mPeer.equals(event.getPeer())){
					connectedPeer.remove(mPeer);
				}
			}
			for (Peer mPeer : rendezVousPeer) {
				if(mPeer.equals(event.getPeer())){
					connectedPeer.remove(mPeer);
				}
			}
			break;
		}
		
	}

	
	
	

}
