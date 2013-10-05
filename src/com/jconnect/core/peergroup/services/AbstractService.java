package com.jconnect.core.peergroup.services;

import java.security.InvalidKeyException;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.util.uuid.PeerID;

/**
 * Abstract class for services 
 *
 */
public abstract class AbstractService {
	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	protected long nextExecutionTime = 0;
	protected AbstractPeerGroup group;
	private Stack<MessageEvent> messageEvents;

	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	
	/**
	 * Executed when the service is notified
	 * Calls {@link #action()} and {@link #handleMessagesReceived()}
	 */
	public void update() {
		nextExecutionTime = action()+System.currentTimeMillis();
		handleMessagesReceived();
	}
	/**
	 * 
	 * @return true if the service need to be updated, false otherwise
	 */
	public boolean needsUpdate(){
		if(System.currentTimeMillis()>=nextExecutionTime){
			return true;
		}
		return false;
	}

	public void setPeerGroup(AbstractPeerGroup abstractPeerGroup) {
		this.group = abstractPeerGroup;
	}
	
	/**
	 * For all messages received, looks if the message must be handled
	 * In that case, calls {@link #handleMessage(MessageEvent)}
	 */
	private void handleMessagesReceived()
	{
		synchronized (messageEvents) {
			for(MessageEvent m : messageEvents)
			{
				if(isInteresting(m))
					handleMessage(m);
			}
				
		}
		
	}
	
	/**
	 * SendMessage
	 * @param m : message to send
	 * @param receivers : list of receiver's {@link PeerID}
	 */
	protected void sendMessage(Message m, List<PeerID> receivers)
	{
		try {
			group.sendMessage(m, receivers);
		} catch (InvalidKeyException e) {
			log.log(Level.SEVERE, "InvalidKeyException");
		}
	}
	
	/**
	 * Message handler
	 * Must be defined in extended class
	 * @param m : Message to handle
	 */
	protected abstract void handleMessage(MessageEvent m);
	
	/**
	 * Decides if a {@link MessageEvent} must be handled or not
	 * @param m : MessageEvent to scan
	 * @return true if the message must be handled, false otherwise
	 */
	protected abstract boolean isInteresting(MessageEvent m);
	
	/**
	 * Add a message in {@link #messageEvents}
	 * @param m : MessageEvent to add
	 */
	public void addMessageEvent(MessageEvent m)
	{
		synchronized (messageEvents) {
			messageEvents.add(m);
		}
		
	}

	/**
	 * Executed by {@link #update()}
	 * @return seconds until next service's update  
	 */
	protected abstract int action();
	
	

		
	

}
