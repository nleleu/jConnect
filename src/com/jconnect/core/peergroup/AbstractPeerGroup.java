package com.jconnect.core.peergroup;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.event.OutputMessageListener;
import com.jconnect.core.event.PeerEventListener;
import com.jconnect.core.event.RequestCallBack;
import com.jconnect.core.event.RequestHandler;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.core.peergroup.peer.PeerEvent.EVENT;
import com.jconnect.core.peergroup.services.AbstractService;
import com.jconnect.util.uuid.ConversationID;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

/**
 * Abstract class for PeerGroup Owns a list of services
 */
public abstract class AbstractPeerGroup {

	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private List<AbstractService> services = new ArrayList<AbstractService>();
	private List<AbstractService> blockedServices = new ArrayList<AbstractService>();
	private List<AbstractService> runningServices = new ArrayList<AbstractService>();

	
	
	private Thread thread;
	protected PeerGroupManager peerGroupManager;
	Key securityKey = null;

	private List<PeerID> connectedPeers = new ArrayList<PeerID>();
	private List<PeerEventListener> peerEventListeners = new ArrayList<PeerEventListener>();
	private  List<OutputMessageListener> outputMessageListeners  = new ArrayList<OutputMessageListener>();
	
	private PeerGroupID uuid;

	private Stack<MessageEvent> messageEvents = new Stack<MessageEvent>();
	private Stack<PeerEvent> peerEvents = new Stack<PeerEvent>();

	private Map<ConversationID , RequestHandler> requestHandles = new HashMap<ConversationID , RequestHandler>();

	

	public AbstractPeerGroup(PeerGroupID uuid) {
		this.uuid = uuid;
		thread = new GroupThread();
	}

	public AbstractPeerGroup(Key securityKey, PeerGroupID uuid) {
		this.uuid = uuid;
		thread = new GroupThread();
		this.securityKey = securityKey;
	}

	/**
	 * Add a new service and start his execution
	 * @param service to add
	 */
	public void addService(AbstractService service) {
		if(services.contains(service))
			return;
		services.add(service);
		runningServices.add(service);
	}
	
	/**
	 * remove the service from the peerGroup
	 * @param service to add
	 */
	public void removeService(AbstractService service) {
		services.remove(service);
		runningServices.remove(service);
		blockedServices.remove(service);
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
	
	public void addPeerRoutes(List<RouteModel> routes){
		peerGroupManager.addPeerRoutes(routes);
	}

	public PeerGroupID getuUID() {
		return uuid;
	}

	public void addMessageEvent(MessageEvent mEvent) {
		if(!connectedPeers.contains(mEvent.getMessage().getPeer())){
			addPeerEvent(new PeerEvent(mEvent.getMessage().getPeer(), EVENT.CONNECT));
		}
		synchronized (messageEvents) {
			messageEvents.add(mEvent);

		}
		thread.notifyAll();
	}

	public void addPeerEvent(PeerEvent pEvent) {
		synchronized (peerEvents) {
			peerEvents.add(pEvent);
		}
		thread.notifyAll();
	}

	public void sendMessage(Message message, List<PeerID> receivers, TransportType protocol) {
		for (OutputMessageListener oMessageListener : outputMessageListeners) {
			oMessageListener.onMessageSend(message,  receivers);
		}
		if(securityKey!=null){
			message.encode(securityKey);
		}
		peerGroupManager.sendMessage(message, receivers, protocol);
	}
	
	/**
	 * 
	 * Call send message and set a call back to handle future answers
	 * 
	 * @param message Message to send
	 * @param receivers List of UUID receivers
	 * @param maxAnswer Max number of answer needed
	 * @param millisTimeOut TimeOut in millisecond
	 * @param callBack Callback when a answer is receive
	 */
	public void request(Message message, List<PeerID> receivers,TransportType protocol, int maxAnswer, double millisTimeOut, RequestCallBack callBack ) throws InvalidKeyException{
		if(callBack!=null)
			requestHandles.put(message.getID(), new RequestHandler(maxAnswer, System.currentTimeMillis()+millisTimeOut, callBack));
		sendMessage(message, receivers, protocol);
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
					
					while(!messageEvents.isEmpty()){
						MessageEvent me = messageEvents.pop();
												
						RequestHandler mHandler = requestHandles.get(me.getMessage().getID());
						if(mHandler!=null){
							boolean remove = mHandler.handleMessage(me);
							if(remove){
								requestHandles.remove(me.getMessage().getID());
							}
						}
						if (service.messageMatcher(me))
							service.handleMessage(me);
					}
				}
				
				
				for (Entry<ConversationID, RequestHandler> r : requestHandles.entrySet()) {
					if(r.getValue().isOver()){
						requestHandles.remove(r);
					}
					
				}
				
				
				synchronized (peerEvents) {
					while(!peerEvents.isEmpty()){
						PeerEvent pe = peerEvents.pop();
						switch (pe.getEvent()) {
						case CONNECT:
							if(!connectedPeers.contains(pe.getPeerId())){
								connectedPeers.add(pe.getPeerId());
							}
							for (PeerEventListener peerListener : peerEventListeners) {
								peerListener.onPeerEvent(pe);
							}

							break;

						case NEW_ROUTE:
							if(!connectedPeers.contains(pe.getPeerId())){
								for (PeerEventListener peerListener : peerEventListeners) {
									peerListener.onPeerEvent(pe);
								}
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

	public void activeService(AbstractService abstractService) {
		if(blockedServices.remove(abstractService))
			runningServices.add(abstractService);
		
	}

	public void blockService(AbstractService abstractService) {
		if(runningServices.remove(abstractService))
			blockedServices.add(abstractService);
	}
	

}
