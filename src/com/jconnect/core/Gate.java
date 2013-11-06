package com.jconnect.core;

import java.io.IOException;
import java.lang.Thread.State;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.JConnect;
import com.jconnect.core.event.MessageEvent;
import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.message.Message;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.core.peergroup.peer.PeerEvent;
import com.jconnect.core.transfer.MulticastOutputRunnable;
import com.jconnect.core.transfer.SocketConnectivityInfo;
import com.jconnect.core.transfer.TCPInputRunnable;
import com.jconnect.core.transfer.TCPOutputRunnable;
import com.jconnect.core.transfer.UDPOutputRunnable;
import com.jconnect.util.Constants;
import com.jconnect.util.uuid.PeerGroupID;
import com.jconnect.util.uuid.PeerID;

/**
 * Manages sockets, to operate Networks I/O Owns two threadpools, one for
 * sending, the other for receiving messages Handles servers' threads
 */
public class Gate implements Runnable {

	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private HashMap<SocketAddress, SocketConnectivityInfo> inputSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();
	private HashMap<SocketAddress, SocketConnectivityInfo> outputSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();

	private HashMap<PeerID, List<RouteModel>> peerRoutes = new HashMap<PeerID, List<RouteModel>>();

	private ServerSocket serverTCPSocket;

	private MulticastSocket serverMulticastSocket = null;
	private DatagramSocket serverUDPSocket = null;
	private InetAddress multicastGroup;

	private JConnect jConnect;

	// THREADs

	private ExecutorService inputGateThreadPool;
	private ExecutorService outputGateThreadPool;

	private ServerTCPThread serverTCPThread;
	private ServerMulticastThread serverMulticastThread;
	private ServerUDPThread serverUDPThread;

	private Thread mainThread;

	private Timer timer;

	public Gate(JConnect jConnect) {
		this.jConnect = jConnect;
	}

	/**
	 * Starts all threads, creates threadpools
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		try {
			timer = new Timer();

			inputGateThreadPool = Executors
					.newFixedThreadPool(Constants.NB_INPUT_THREAD);
			outputGateThreadPool = Executors
					.newFixedThreadPool(Constants.NB_OUTPUT_THREAD);
			// open Input Gate
			if (jConnect.getPrefs().isTCP()) {
				serverTCPSocket = new ServerSocket(jConnect.getPrefs()
						.getTCPPort());
				serverTCPThread = new ServerTCPThread(this, serverTCPSocket);
				serverTCPThread.start();
			}
			if (jConnect.getPrefs().isUDP()) {
				serverUDPSocket = new DatagramSocket(jConnect.getPrefs()
						.getUDPPort());
				serverUDPThread = new ServerUDPThread(this, serverUDPSocket);
				serverUDPThread.start();
			}
			if (jConnect.getPrefs().isMulticast()) {
				multicastGroup = InetAddress.getByName(Constants.MULTICAST_IP);
				serverMulticastSocket = new MulticastSocket(jConnect.getPrefs()
						.getMulticastPort());
				// serverMulticastSocket.setTimeToLive(0);
				serverMulticastSocket.joinGroup(multicastGroup);
				serverMulticastThread = new ServerMulticastThread(this,
						serverMulticastSocket);
				serverMulticastThread.start();
				mainThread = new Thread(this);
				mainThread.start();

			}
		} catch (IOException e) {
			stop();
			throw e;
		}

	}

	/**
	 * Closes opened sockets and interrupt all threads
	 */
	public void stop() {
		if (inputGateThreadPool != null) {
			inputGateThreadPool.shutdownNow();
			inputGateThreadPool = null;
		}
		if (outputGateThreadPool != null) {
			outputGateThreadPool.shutdownNow();
			outputGateThreadPool = null;
		}
		if (mainThread != null) {
			mainThread.interrupt();
			mainThread = null;
		}
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		if (serverTCPThread != null) {
			serverTCPThread.interrupt();
			serverTCPThread = null;
		}
		if (serverTCPSocket != null) {
			try {
				serverTCPSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serverTCPSocket = null;

		}

		if (serverUDPSocket != null) {
			serverUDPSocket.close();
			serverUDPSocket = null;
		}

		if (serverUDPThread != null) {
			serverUDPThread.interrupt();
			serverUDPThread = null;
		}

		if (serverMulticastSocket != null) {
			serverMulticastSocket.close();
			serverMulticastSocket = null;
		}
		if (serverMulticastThread != null) {
			serverMulticastThread.interrupt();
			serverMulticastThread = null;
		}

		for (Entry<SocketAddress, SocketConnectivityInfo> entry : outputSockets
				.entrySet()) {
			try {
				entry.getValue().getSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		outputSockets.clear();

		for (Entry<SocketAddress, SocketConnectivityInfo> entry : inputSockets
				.entrySet()) {
			try {
				entry.getValue().getSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		inputSockets.clear();

	}

	@Override
	public void run() {
		log.log(Level.FINER, "Gate Thread Started");
		try {
			while (mainThread != null
					&& mainThread.getState() == State.RUNNABLE) {
				{
					// CHECK OUTPUT
					Iterator<Map.Entry<SocketAddress, SocketConnectivityInfo>> it = outputSockets
							.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<SocketAddress, SocketConnectivityInfo> pair = it
								.next();

						if (pair.getValue().getLastSendDataDate() < System
								.currentTimeMillis()
								- Constants.OUTPUT_THREAD_TIMEOUT) {
							try {
								pair.getValue().getSocket().close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							it.remove();
						}
					}
				}
				{
					// CHECK INPUT
					Iterator<Map.Entry<SocketAddress, SocketConnectivityInfo>> it = inputSockets
							.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<SocketAddress, SocketConnectivityInfo> pair = it
								.next();

						if (pair.getValue().getLastReceivedDataDate() < System
								.currentTimeMillis()
								- Constants.INPUT_THREAD_TIMEOUT) {
							try {
								pair.getValue().getSocket().close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						synchronized (Gate.this) {
							Gate.this.notifyAll();
						}

					}
				}, Constants.TIME_GATE_THREAD_REFRESH);
				synchronized (Gate.this) {
					wait();
				}

				timer.cancel();
				timer = new Timer();

			}
		} catch (InterruptedException e) {
			log.log(Level.FINER, "Gate Thread Interupted");
		}
		log.log(Level.FINER, "Gate Thread Terminated");

	}

	public synchronized Socket getSocket(InetSocketAddress addr) {

		SocketConnectivityInfo sInfo = outputSockets.get(addr);
		if (sInfo == null || sInfo.getSocket().isClosed()) {
			try {
				Socket s = new Socket(addr.getAddress(), addr.getPort());
				outputSockets.put(addr, new SocketConnectivityInfo(s));
				return s;
			} catch (IOException e) {
				log.log(Level.SEVERE,
						"Can't open new socket: " + e.getMessage());
				return null;
			}
		}

		return sInfo.getSocket();
	}

	public void addEvent(TransferEvent event) {

		switch (event.getState()) {
		case MESSAGE_RECEIVED: {

			SocketConnectivityInfo socketInfo = inputSockets.get(event
					.getRoute().getSocketAddress());
			if (socketInfo != null) {
				socketInfo.setLastReceivedDataDate(System.currentTimeMillis());
			}
			{
				event.getRoute().lastReception = System.currentTimeMillis();
				if (event.getTransportType() == TransportType.UDP) {
					if (event.getMessage().getGroup().equals(PeerGroupID.NULL)) {// CHECK
																					// CONNECTIVITY
																					// MESSAGE
						if (event.getMessage().getPeer()
								.equals(jConnect.getPeerID())) {// HAVE TO
																// RESPOND
							outputGateThreadPool.execute(new UDPOutputRunnable(
									this, serverUDPSocket, event.getRoute(),
									event.getMessage()));
						} else {
							event.getRoute().lastSend = event.getMessage()
									.getDate();

							event.getRoute().lastPing = (int) (event.getRoute().lastReception - event
									.getRoute().lastSend);
						}
					}

				} else if (event.getTransportType() == TransportType.MULTICAST) { // Catch
																					// our
																					// sended
																					// message
					if (event.getMessage().getPeer()
							.equals(jConnect.getPeerID())) {
						// ignore message
						return;

					}

				}

				Message m = event.getMessage();

				addRoute(event.getRoute());
				MessageEvent mEvent = new MessageEvent(
						MessageEvent.State.MESSAGE_RECEIVED, m);
				jConnect.getPeerGroupManager().addMessageEvent(mEvent);
			}
		}
			break;
		case SEND_FAIL:
		case SOCKET_CLOSED:
			log.log(Level.FINER, "SOCKET CLOSED");
			if (event.getRoute() != null) { // only TCP¨have retry
				if ((event.getTryCount() + 1) < jConnect.getPrefs()
						.getTCPSendAttempt()) {

					outputGateThreadPool.execute(new TCPOutputRunnable(this,
							event.getRoute(), event.getMessage(), event
									.getTryCount() + 1));
					log.log(Level.FINER, "attempt " + (event.getTryCount() + 1)
							+ " on "
							+ event.getRoute().getSocketAddress().toString());
					break;
				}

				removeRoute(event.getRoute());

			}
			MessageEvent e = new MessageEvent(MessageEvent.State.SEND_FAIL,
					event.getMessage());
			jConnect.getPeerGroupManager().addMessageEvent(e);
			break;
		case INPUT_TIME_OUT:
			log.log(Level.WARNING, "Input Time Out");
			break;
		case SEND_SUCCESS:
			if (event.getTransportType().equals(TransportType.TCP)) {
				SocketConnectivityInfo socketInfo = inputSockets.get(event
						.getRoute().getSocketAddress());
				if (socketInfo != null) {
					socketInfo.setLastSentDataDate(System.currentTimeMillis());

				}
			}
			{
				MessageEvent mEvent = new MessageEvent(
						MessageEvent.State.SEND_SUCCESS, event.getMessage());
				jConnect.getPeerGroupManager().addMessageEvent(mEvent);
			}
			break;
		default:
			break;
		}
		;
		synchronized (Gate.this) {
			notifyAll();
		}

	}

	public void addRoute(RouteModel route) {
		if (route.getTransportType().equals(TransportType.MULTICAST))
			return;
		synchronized (peerRoutes) {

			List<RouteModel> routes = peerRoutes.get(route.getContactUUID());
			if (routes != null) {

				if (!routes.contains(route)) {
					routes.add(route);
					PeerEvent pEvent = new PeerEvent(route.getContactUUID(),
							PeerEvent.EVENT.NEW_ROUTE);
					jConnect.getPeerGroupManager().addPeerEvent(pEvent);
					log.log(Level.FINER, "Peer " + route.getContactUUID()
							+ " new route");
				} else {
					if (route.lastPing > 0) {
						RouteModel r = routes.get(routes.indexOf(route));
						r.lastReception = route.lastReception;
						r.lastPing = route.lastPing;
						r.lastSend = route.lastSend;
					}

				}
			} else {
				routes = new ArrayList<RouteModel>();
				routes.add(route);
				peerRoutes.put(route.getContactUUID(), routes);
				// PeerEvent pEvent = new PeerEvent(route.getContactUUID(),
				// PeerEvent.EVENT.CONNECT);
				// jConnect.getPeerGroupManager().addPeerEvent(pEvent);
				log.log(Level.FINER, "Peer " + route.getContactUUID()
						+ " connected");
			}

		}
	}

	private void removeRoute(RouteModel route) {
		synchronized (peerRoutes) {
			List<RouteModel> routes = peerRoutes.get(route.getContactUUID());
			routes.remove(route);
			if (routes.size() == 0) { // Peer Disconnected
				PeerEvent pEvent = new PeerEvent(route.getContactUUID(),
						PeerEvent.EVENT.DISCONNECT);
				jConnect.getPeerGroupManager().addPeerEvent(pEvent);
				log.log(Level.FINER, "Peer " + route.getContactUUID()
						+ " disconnected");
			}
		}
	}

	public void checkPeerUDPConnectivity(final PeerID pId,
			final long millisRefreshInterval, long responseTimeOut) {
		List<RouteModel> routes = getPeerRoute(pId, TransportType.UDP);
		final List<RouteModel> refreshList = new ArrayList<RouteModel>();
		Message m = Message.getEmptyMessage(pId);
		for (RouteModel routeModel : routes) {
			if (routeModel.lastReception + millisRefreshInterval < System
					.currentTimeMillis()) {
				outputGateThreadPool.execute(new UDPOutputRunnable(this,
						serverUDPSocket, routeModel, m));
				refreshList.add(routeModel);

			}
		}

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				for (RouteModel routeModel : refreshList) {
					if (routeModel.lastReception + millisRefreshInterval < System
							.currentTimeMillis()) {
						removeRoute(routeModel);
					}
				}

			}
		}, responseTimeOut);

	}

	/**
	 * send Message
	 * 
	 * @param message
	 *            - the string to send
	 * @param receivers
	 *            - if null use of multicast
	 * @param protocol
	 *            - protocol used to send the message (useless if multicast)
	 */
	public void sendMessage(Message message, List<PeerID> receivers,
			TransportType protocol) {

		if (receivers == null) { // MULTICAST
			outputGateThreadPool.execute(new MulticastOutputRunnable(this,
					serverMulticastSocket, multicastGroup, jConnect.getPrefs()
							.getMulticastPort(), message));
		} else {
			// retrieving peer's routes
			List<RouteModel> routes = new ArrayList<RouteModel>();
			for (PeerID peerID : receivers) {
				List<RouteModel> peerRoutesList = getPeerRoute(peerID, protocol);
				if (peerRoutesList.size() > 0) {
					routes.add(getBestRoute(peerRoutesList));
				}
			}

			for (RouteModel routeModel : routes) {
				if (protocol == TransportType.TCP) { // TCP

					outputGateThreadPool.execute(new TCPOutputRunnable(this,
							routeModel, message));
				} else { // UDP
					outputGateThreadPool.execute(new UDPOutputRunnable(this,
							serverUDPSocket, routeModel, message));
				}
			}

		}

	}

	private List<RouteModel> getPeerRoute(PeerID peerID, TransportType protocol) {
		List<RouteModel> result = new ArrayList<RouteModel>();
		List<RouteModel> routes = peerRoutes.get(peerID);
		if (routes != null) {
			for (RouteModel routeModel : routes) {
				if (protocol == null
						|| routeModel.getTransportType() == protocol) {
					result.add(routeModel);
				}
			}
		}
		return result;
	}

	/*
	 * We need to select local address first
	 */
	private RouteModel getBestRoute(List<RouteModel> peerRoutesList) {
		for (RouteModel routeModel : peerRoutesList) {
			if (routeModel.getSocketAddress().getAddress().isSiteLocalAddress())
				return routeModel;
		}
		return peerRoutesList.get(0);

	}

	void addTCPInput(Socket socketclient) {
		synchronized (inputSockets) {

			SocketConnectivityInfo sci = new SocketConnectivityInfo(
					socketclient);
			inputSockets.put(socketclient.getRemoteSocketAddress(), sci);
			inputGateThreadPool
					.execute(new TCPInputRunnable(this, socketclient));

		}

	}

}
