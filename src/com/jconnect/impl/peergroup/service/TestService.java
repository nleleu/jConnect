package com.jconnect.impl.peergroup.service;

import java.util.List;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.event.OutputMessageListener;
import com.jconnect.core.event.PeerEventListener;
import com.jconnect.core.message.Message;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.impl.message.TestContentMessage;
import com.jconnect.util.uuid.PeerID;

public class TestService extends Service {

	public TestService(AbstractPeerGroup group) {
		super(group);
		group.registerOutputMessageListener(new OutputMessageListener() {

			@Override
			public void onMessageSend(Message message, List<PeerID> receivers) {
				System.out.println("message send " + message.getID());

			}

			@Override
			public boolean messageMatcher(Message message) {

				return true;
			}
		});
		group.registerPeerEventListener(new PeerEventListener() {

			@Override
			public void onPeerEvent(PeerEvent peerEvent) {
				System.out.println("peer event: " + peerEvent.toString());

			}
		});
	}

	@Override
	protected void onHandleMessage(MessageEvent m) {
		System.out.println("message : " + m.getState());
		if (m.getState().equals(MessageEvent.State.MESSAGE_RECEIVED)) {
			System.out.println("message receive: " + m.getMessage().getID());
			System.out.println("message content : "
					+ m.getMessage().getContent().toString());
		}
	}

	@Override
	public boolean messageMatcher(MessageEvent message) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onUpdade() {
		System.out.println("update");
		sendMulticastMessage(new Message(group, new TestContentMessage()));
		System.out.println("block 10s");
		block(10000);
		return;

	}

}
