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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jconnect.core.IGate;
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
	//protected PeerGroupManager peerGroupManager;
	Key securityKey = null;

	private List<PeerID> connectedPeers = new ArrayList<PeerID>();
	private List<PeerEventListener> peerEventListeners = new ArrayList<PeerEventListener>();
	private List<OutputMessageListener> outputMessageListeners = new ArrayList<OutputMessageListener>();

	private PeerGroupID uuid;

	private Stack<MessageEvent> messageEvents = new Stack<MessageEvent>();
	private Stack<PeerEvent> peerEvents = new Stack<PeerEvent>();

	private Map<ConversationID, RequestHandler> requestHandles = new HashMap<ConversationID, RequestHandler>();

	private IGate gate;

	public abstract AbstractPeerGroup newIntance(PeerGroupID uuid, IGate gate);
	
	protected AbstractPeerGroup(PeerGroupID uuid, IGate gate) {
		this.uuid = uuid;
		this.gate = gate;
		thread = new GroupThread();
		initServices();
	}
	
	protected abstract void initServices();
	
	public void setSecurityKey(Key securityKey){
		this.securityKey = securityKey;
	}

	/**
	 * Add a new service and start his execution
	 * 
	 * @param service
	 *            to add
	 */
	public void addService(AbstractService service) {
		if (services.contains(service))
			return;
		services.add(service);
		runningServices.add(service);
	}

	/**
	 * remove the service from the peerGroup
	 * 
	 * @param service
	 *            to add
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
			log.log(Level.WARN, "Group " + uuid
					+ " already running. Thread state:" + thread.getState());
			break;

		}
	}

	public void stop() {
		switch (thread.getState()) {
		case TERMINATED:
		case NEW:
			log.log(Level.WARN, "Group " + uuid
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

	public void registerPeerEventListener(PeerEventListener peerEventListener) {
		peerEventListeners.add(peerEventListener);
	}

	public void unRegisterPeerEventListener(PeerEventListener peerEventListener) {
		peerEventListeners.remove(peerEventListener);
	}

	public void registerOutputMessageListener(
			OutputMessageListener outputMessageListener) {
		outputMessageListeners.add(outputMessageListener);
	}

	public void unRegisterOutputMessageListener(
			OutputMessageListener outputMessageListener) {
		outputMessageListeners.remove(outputMessageListener);
	}

	public void addPeerRoutes(List<RouteModel> routes) {
		for (RouteModel routeModel : routes) {
			if(gate.addRoute(routeModel)){
				addPeerEvent(new PeerEvent(routeModel.getContactUUID(), EVENT.CONNECT));
			}
		}
		
	}

	public PeerGroupID getuUID() {
		return uuid;
	}

	public void addMessageEvent(MessageEvent mEvent) {
		
		
		
		if (mEvent.getState() == MessageEvent.State.MESSAGE_RECEIVED) {
			try {
				mEvent.getMessage().decrypt(securityKey);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
				return;
			}
			log.info(getuUID()+" received a message:" + mEvent.getMessage().getContent());
			if (!connectedPeers.contains(mEvent.getMessage().getPeer())&&gate.getPeerRoute(mEvent.getMessage().getPeer(),null).size()>0) {
				addPeerEvent(new PeerEvent(mEvent.getMessage().getPeer(),
						EVENT.CONNECT));
			}
		}
		synchronized (messageEvents) {
			messageEvents.add(mEvent);

		}
		synchronized (thread) {
			thread.notifyAll();
		}

	}

	public void addPeerEvent(PeerEvent pEvent) {
		synchronized (peerEvents) {
			peerEvents.add(pEvent);
		}
		synchronized (thread) {
			thread.notifyAll();
		}

	}

	public void sendMessage(Message message, List<PeerID> receivers,
			TransportType protocol) {
		for (OutputMessageListener oMessageListener : outputMessageListeners) {
			if (oMessageListener.messageMatcher(message))
				oMessageListener.onMessageSend(message, receivers);
		}
		if (securityKey != null) {
			message.encode(securityKey);
		}
		gate.sendMessage(message, receivers, protocol);
	}

	
	/**
	 * 
	 * Call send message and set a call back to handle future answers
	 * 
	 * @param message
	 *            Message to send
	 * @param receivers
	 *            List of UUID receivers
	 * @param maxAnswer
	 *            Max number of answer needed
	 * @param millisTimeOut
	 *            TimeOut in millisecond
	 * @param callBack
	 *            Callback when a answer is receive
	 */
	public void request(Message message, List<PeerID> receivers,
			TransportType protocol, int maxAnswer, double millisTimeOut,
			RequestCallBack callBack) throws InvalidKeyException {
		if (callBack != null)
			requestHandles.put(message.getID(), new RequestHandler(maxAnswer,
					System.currentTimeMillis() + millisTimeOut, callBack));
		sendMessage(message, receivers, protocol);
	}

	
	public List<PeerID> getConnectedPeers() {
		return connectedPeers;
	}
	
	private class GroupThread extends Thread {

		private static final long MAX_THREAD_SLEEP_TIME = 20000;
		private long timeToSleep = 0;
		private Timer timer;

		
		
		@Override
		public void run() {
			log.log(Level.INFO, "Group " + uuid + " started");
			timer = new Timer();
			try {
				while (thread.getState() == Thread.State.RUNNABLE) {
					update();

				}
				
			} catch (InterruptedException e) {

			}
			timer.cancel();
			log.log(Level.INFO, "Group " + uuid + " stopped");

		}

		private void update() throws InterruptedException {

			synchronized (messageEvents) {

				while (!messageEvents.isEmpty()) {
					MessageEvent me = messageEvents.pop();

					RequestHandler mHandler = requestHandles.get(me
							.getMessage().getID());
					if (mHandler != null) {
						boolean remove = mHandler.handleMessage(me);
						if (remove) {
							requestHandles.remove(me.getMessage().getID());
						}
					}
					for (AbstractService service : services) {
						if (service.messageEventMatcher(me))
							service.handleMessage(me);
					}
				}
			}

			for (Entry<ConversationID, RequestHandler> r : requestHandles
					.entrySet()) {
				if (r.getValue().isOver()) {
					requestHandles.remove(r);
				}

			}

			synchronized (peerEvents) {
				while (!peerEvents.isEmpty()) {
					PeerEvent pe = peerEvents.pop();
					switch (pe.getEvent()) {
					case CONNECT:
						if (!connectedPeers.contains(pe.getPeerId())) {
							connectedPeers.add(pe.getPeerId());
							for (PeerEventListener peerListener : peerEventListeners) {
								peerListener.onPeerEvent(pe);
							}
						}

						break;

					case NEW_ROUTE:
						if (!connectedPeers.contains(pe.getPeerId())) {
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
			
			timeToSleep = System.currentTimeMillis()+MAX_THREAD_SLEEP_TIME;
			for (int i = runningServices.size()-1; i >=0 ; i--) {
				AbstractService service = runningServices.get(i);
				if (service.needsUpdate())
					service.update();
				timeToSleep = Math.min(timeToSleep, service.getNextExecutionTime());
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
			

		}

	}

	public void activeService(AbstractService abstractService) {
		if (blockedServices.remove(abstractService))
			runningServices.add(abstractService);

	}

	public void blockService(AbstractService abstractService) {
		if (runningServices.remove(abstractService))
			blockedServices.add(abstractService);
	}

	public PeerID getPeerID(){
		return gate.getPeerID();
	}
	public List<RouteModel> getPeerRoute(PeerID peerID, TransportType protocol) {
		return gate.getPeerRoute(peerID, protocol);
	}

	public void checkPeerUDPConnectivity(PeerID peerID, long millisRefreshInterval, long responseTimeOut) {
		gate.checkPeerUDPConnectivity(peerID, millisRefreshInterval, responseTimeOut);
	}
	
	

}
