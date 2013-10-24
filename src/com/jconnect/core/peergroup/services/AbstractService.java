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

	private long nextExecutionTime = 0;
	protected AbstractPeerGroup group;

	public long getNextExecutionTime() {
		return nextExecutionTime;
	}
	
	public AbstractService(AbstractPeerGroup group) {
		this.group = group;
	}
	
	
	/**
	 * Executed when the service is notified
	 * Calls {@link #onUpdade()} and {@link #handleMessagesReceived()}
	 */
	public void update() {
		onUpdade();
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
	
	public void restart(){
		nextExecutionTime=0;
		group.activeService(this);
	}
	
	
	/**
	 Blocks this service. It should be noticed that this method is NOT a 
	 blocking call: when it is invoked, the internal nextExecutionTime is
	 is set to a negative value so that, as soon as the <code>action()</code>
	 method returns, the service is put into a blocked service queue so that it will 
	 not be scheduled anymore.<br> 
	 The service is moved back in the pool of active service when either 
	 a message is received or the service is explicitly restarted by means of its 
	 <code>restart()</code> method.<br> 
	 */
	protected void block(){
		block(-1);
	}
	
	/**
	 Blocks this service during the set time. It should be noticed that this method is NOT a 
	 blocking call
	 * @param timeInMillis
	 */
	protected void block(long timeInMillis){
		if(timeInMillis<0){
			nextExecutionTime = -1;
		}
		nextExecutionTime = System.currentTimeMillis()+timeInMillis;
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
	 * handle message
	 * @param m : Message to handle
	 */
	public void handleMessage(final MessageEvent m){
		 if(nextExecutionTime<0){
			 restart();
		 }
		 onHandleMessage(m);
	}
	
	/**
	 * execute on message reception
	 * Must be defined in extended class
	 * @param m : Message to handle
	 */
	protected abstract void onHandleMessage(final MessageEvent m);
	
	/**
	 * Decides if a {@link MessageEvent} must be handled or not
	 * @param message : MessageEvent to scan
	 * @return true if the message must be handled, false otherwise
	 */
	public abstract boolean messageMatcher(final MessageEvent message);
	

	/**
	 * Executed by {@link #update()}
	 * @return seconds until next service's update  
	 */
	protected abstract void onUpdade();
	
	

		
	

}
