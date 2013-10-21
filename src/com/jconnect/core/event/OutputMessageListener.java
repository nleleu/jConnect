package com.jconnect.core.event;

import java.util.List;

import com.jconnect.core.message.Message;
import com.jconnect.util.uuid.PeerID;

public interface OutputMessageListener {
	
	public void onMessageSend(final Message message, List<PeerID> receivers);

}
