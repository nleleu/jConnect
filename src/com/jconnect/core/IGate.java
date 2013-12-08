package com.jconnect.core;

import java.util.List;

import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

public interface IGate {

	boolean addRoute(RouteModel routeModel );

	void sendMessage(Message message, List<PeerID> receivers,
			TransportType protocol);

	PeerID getPeerID();
	
	void checkPeerUDPConnectivity(final PeerID pId,final long millisRefreshInterval, long responseTimeOut);

	List<RouteModel> getPeerRoute(PeerID peerID, TransportType protocol);


}
