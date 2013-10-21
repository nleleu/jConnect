package com.jconnect.impl.peergroup.service;

import java.util.ArrayList;
import java.util.List;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.core.peergroup.peer.PeerListener;
import com.jconnect.util.uuid.PeerID;

public class RendezVousService extends Service implements PeerListener{

	
	private boolean isRendezVous = false;
	private List<PeerID> rendezVousPeer = new ArrayList<PeerID>();
	private List<PeerID> connectedPeer = new ArrayList<PeerID>();;
	
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
//			if(event.getPeer().isRendezVous()){
//				rendezVousPeer.add(event.getPeer());
//			}else{
//				connectedPeer.add(event.getPeer());
//			}
			
			break;
		case DISCONNECT:
			for (PeerID mPeer : connectedPeer) {
				if(mPeer.equals(event.getPeerId())){
					connectedPeer.remove(mPeer);
				}
			}
			for (PeerID mPeer : rendezVousPeer) {
				if(mPeer.equals(event.getPeerId())){
					connectedPeer.remove(mPeer);
				}
			}
			break;
		}
		
	}

	@Override
	public void handleMessage(MessageEvent m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isInteresting(MessageEvent m) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	

}
