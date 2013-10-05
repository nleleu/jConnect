package com.jconnect.impl.peergroup.service;

import java.util.ArrayList;
import java.util.List;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.model.PeerModel;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.core.peergroup.peer.PeerListener;

public class RendezVousService extends Service implements PeerListener{

	
	private boolean isRendezVous = false;
	private List<PeerModel> rendezVousPeer = new ArrayList<PeerModel>();
	private List<PeerModel> connectedPeer = new ArrayList<PeerModel>();;
	
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
			for (PeerModel mPeer : connectedPeer) {
				if(mPeer.equals(event.getPeer())){
					connectedPeer.remove(mPeer);
				}
			}
			for (PeerModel mPeer : rendezVousPeer) {
				if(mPeer.equals(event.getPeer())){
					connectedPeer.remove(mPeer);
				}
			}
			break;
		}
		
	}

	@Override
	protected void handleMessage(MessageEvent m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isInteresting(MessageEvent m) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
	

}
