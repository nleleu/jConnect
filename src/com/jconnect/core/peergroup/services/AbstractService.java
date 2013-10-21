package com.jconnect.core.peergroup.services;

import java.security.InvalidKeyException;
import java.util.List;
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

	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	
	/**
	 * Executed when the service is notified
	 * Calls {@link #action()} and {@link #handleMessagesReceived()}
	 */
	public void update() {
		nextExecutionTime = action()+System.currentTimeMillis();
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
	public abstract void handleMessage(MessageEvent m);
	
	/**
	 * Decides if a {@link MessageEvent} must be handled or not
	 * @param m : MessageEvent to scan
	 * @return true if the message must be handled, false otherwise
	 */
	//TODO : renommer
	public abstract boolean isInteresting(MessageEvent m);
	

	/**
	 * Executed by {@link #update()}
	 * @return seconds until next service's update  
	 */
	protected abstract int action();
	
	

		
	

}
