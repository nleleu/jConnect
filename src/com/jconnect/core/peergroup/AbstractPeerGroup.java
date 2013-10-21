package com.jconnect.core.peergroup;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.event.OutputMessageListener;
import com.jconnect.core.event.PeerEventListener;
import com.jconnect.core.message.Message;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.core.peergroup.services.AbstractService;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

/**
 * Abstract class for PeerGroup Owns a list of services
 */
public abstract class AbstractPeerGroup {

	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private AbstractPeerGroup parentGroup;
	private List<AbstractService> services;
	private Thread thread;
	protected PeerGroupManager peerGroupManager;
	Key securityKey = null;

	private List<PeerID> connectedPeers = new ArrayList<PeerID>();
	private List<PeerEventListener> peerEventListeners = new ArrayList<PeerEventListener>();
	private  List<OutputMessageListener> outputMessageListeners  = new ArrayList<OutputMessageListener>();
	
	private PeerGroupID uuid;

	private Stack<MessageEvent> messageEvents = new Stack<MessageEvent>();
	private Stack<PeerEvent> peerEvents = new Stack<PeerEvent>();

	

	public AbstractPeerGroup(PeerGroupID uuid, AbstractPeerGroup pGroup) {
		parentGroup = pGroup;
		this.uuid = uuid;
		thread = new GroupThread();
	}

	public AbstractPeerGroup(Key securityKey, PeerGroupID uuid,
			AbstractPeerGroup pGroup) {
		parentGroup = pGroup;
		this.uuid = uuid;
		thread = new GroupThread();
		this.securityKey = securityKey;
	}

	public void addService(AbstractService service) {
		service.setPeerGroup(this);
		services.add(service);
	}

	public AbstractService getService(String serviceName) {
		for (AbstractService service : services) {
			if (service.getClass().getName().equals(serviceName)) {
				return service;
			}
		}
		return null;
	}

	public void start() {
		switch (thread.getState()) {
		case TERMINATED:
			thread = new GroupThread();
		case NEW:
			thread.start();
			break;
		case BLOCKED:
		case WAITING:
		case TIMED_WAITING:
		case RUNNABLE:
			log.log(Level.WARNING, "Group " + uuid
					+ " already running. Thread state:" + thread.getState());
			break;

		}
	}

	public void stop() {
		switch (thread.getState()) {
		case TERMINATED:
		case NEW:
			log.log(Level.WARNING, "Group " + uuid
					+ " not running. Thread state:" + thread.getState());

			break;
		case BLOCKED:
		case WAITING:
		case TIMED_WAITING:
		case RUNNABLE:
			thread.interrupt();
			break;

		}
	}
	
	public void registerPeerEventListener(PeerEventListener peerEventListener){
		peerEventListeners.add(peerEventListener);
	}
	public void unRegisterPeerEventListener(PeerEventListener peerEventListener){
		peerEventListeners.remove(peerEventListener);
	}

	public void registerOutputMessageListener(OutputMessageListener outputMessageListener){
		outputMessageListeners.add(outputMessageListener);
	}
	public void unRegisterOutputMessageListener(OutputMessageListener outputMessageListener){
		outputMessageListeners.remove(outputMessageListener);
	}
	

	public PeerGroupID getuUID() {
		return uuid;
	}

	public void addMessageEvent(MessageEvent mEvent) {
		synchronized (messageEvents) {
			messageEvents.add(mEvent);

		}
	}

	public void addPeerEvent(PeerEvent pEvent) {
		synchronized (peerEvents) {
			peerEvents.add(pEvent);
		}

	}

	public void sendMessage(Message m, List<PeerID> receivers)
			throws InvalidKeyException {
		for (OutputMessageListener oMessageListener : outputMessageListeners) {
			oMessageListener.onMessageSend(m,  receivers);
		}
		peerGroupManager.sendMessage(m.generate(securityKey), receivers);
	}

	public void setPeerGroupManager(PeerGroupManager peerGroupManager) {
		this.peerGroupManager = peerGroupManager;

	}
	
	private class GroupThread extends Thread {

		private long timeToSleep = 0;
		private Timer timer = new Timer();

		@Override
		public void run() {

			log.log(Level.FINER, "Group " + uuid + " started");

			try {
				while (thread.getState() == Thread.State.RUNNABLE) {
					update();

				}
			} catch (InterruptedException e) {

			}

			log.log(Level.FINER, "Group " + uuid + " stopped");

		}

		private void update() throws InterruptedException {

			for (AbstractService service : services) {
				synchronized (messageEvents) {
					for (MessageEvent me : messageEvents) {
						if (service.isInteresting(me))
							service.handleMessage(me);
					}
					messageEvents.clear();
				}
				synchronized (peerEvents) {
					for (PeerEvent pe : peerEvents) {
						switch (pe.getEvent()) {
						case CONNECT:
						case RECONNECT:
							if(!connectedPeers.contains(pe.getPeerId())){
								connectedPeers.add(pe.getPeerId());
							}
							for (PeerEventListener peerListener : peerEventListeners) {
								peerListener.onPeerEvent(pe);
							}

							break;
						case DISCONNECT:
							if (connectedPeers.remove(pe.getPeerId())) {
								for (PeerEventListener peerListener : peerEventListeners) {
									peerListener.onPeerEvent(pe);
								}
							}
							break;
						}

					}

				}

				if (service.needsUpdate())
					service.update();
				timeToSleep = timeToSleep == 0 ? service.getNextExecutionTime()
						: Math.min(timeToSleep, service.getNextExecutionTime());
			}

			timeToSleep = timeToSleep - System.currentTimeMillis();

			if (timeToSleep > 0) {

				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						if (thread != null) {
							synchronized (thread) {
								thread.notifyAll();
							}
						}
					}
				}, timeToSleep);
				synchronized (this) {
					wait();
				}

			}
			timeToSleep = 0;

		}

	}
	

}
