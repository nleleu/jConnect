package com.jconnect.core;

import java.io.IOException;
import java.lang.Thread.State;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
import com.jconnect.core.model.RouteModel.TransportType;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.core.transfer.MulticastOutputRunnable;
import com.jconnect.core.transfer.SocketConnectivityInfo;
import com.jconnect.core.transfer.TCPInputRunnable;
import com.jconnect.util.Constants;
import com.jconnect.util.uuid.PeerID;

/**
 * Manages sockets, to operate Networks I/O 
 * Owns two threadpools, one for sending, the other for receiving messages
 * Handles servers' threads
 */
public class Gate implements Runnable {

	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private HashMap<SocketAddress, SocketConnectivityInfo> inputSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();
	private HashMap<SocketAddress, SocketConnectivityInfo> outputSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();

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

				// synchronized (eventsList) {
				// while (!eventsList.isEmpty()) {
				// TransferEvent event = eventsList.pop();
				// SocketConnectivityInfo socketInfo = inputSockets
				// .get(event.getSocketAddress());
				// switch (event.getState()) {
				// case MESSAGE_RECEIVED:
				// if(socketInfo!=null){
				// socketInfo.setLastReceivedDataDate(System
				// .currentTimeMillis());
				// }
				// jConnect.getPeerGroupManager().message;
				// //Message m = new Message(event.getData());
				// System.out.println(event.getData());
				//
				// //m.getGroup(); // TODO attente BDD
				// break;
				// case SEND_FAIL:
				// // TODO
				// break;
				// case SEND_SUCCESS:
				// if(socketInfo!=null){
				// socketInfo.setLastSentDataDate(System
				// .currentTimeMillis());
				//
				// }
				// // TODO
				// break;
				// default:
				// break;
				// }
				// ;
				// }
				// }

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

	public void addEvent(TransferEvent event) {
		SocketConnectivityInfo socketInfo = inputSockets.get(event
				.getSocketAddress());
		switch (event.getState()) {
		case MESSAGE_RECEIVED:
			if (socketInfo != null) {
				socketInfo.setLastReceivedDataDate(System.currentTimeMillis());
			}
			{
				MessageEvent mEvent = new MessageEvent(
						MessageEvent.State.MESSAGE_RECEIVED);
				mEvent.setMessage(new Message(event.getData()));
				jConnect.getPeerGroupManager().addMessageEvent(mEvent);
			}
			break;
		case SEND_FAIL:
			// TODO
			break;
		case SEND_SUCCESS:
			if (socketInfo != null) {
				socketInfo.setLastSentDataDate(System.currentTimeMillis());

			}
			{
				MessageEvent mEvent = new MessageEvent(
						MessageEvent.State.SEND_SUCCESS);
				mEvent.setMessage(new Message(event.getData()));
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

	public void sendMulticastMessage(String message) {
		sendMessage(message, null, null);
	}

	public void sendMessage(String message, PeerID receiver) {
		List<PeerID> receivers = new ArrayList<PeerID>();
		receivers.add(receiver);
		sendMessage(message, receivers, TransportType.TCP);
	}

	public void sendMessage(String message, List<PeerID> receivers) {
		sendMessage(message, receivers, TransportType.TCP);
	}

	public void sendMessage(String message, List<PeerID> receivers,
			TransportType protocol) {

		if (receivers == null) { // MULTICAST
			outputGateThreadPool.execute(new MulticastOutputRunnable(this,
					serverMulticastSocket, multicastGroup, jConnect.getPrefs()
							.getMulticastPort(), message));
		} else {
			// TODO

		}

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
