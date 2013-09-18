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
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.JConnect;
import com.jconnect.core.transfer.MulticastOutputRunnable;
import com.jconnect.core.transfer.SocketConnectivityInfo;
import com.jconnect.core.transfer.TCPInputRunnable;
import com.jconnect.core.transfer.event.TransferEvent;
import com.jconnect.message.Message;
import com.jconnect.model.Route.TransportType;
import com.jconnect.peergroup.AbstractPeerGroup;
import com.jconnect.util.Constants;

public class Gate implements Runnable {

	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	private HashMap<SocketAddress, SocketConnectivityInfo> inputSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();
	private HashMap<SocketAddress, SocketConnectivityInfo> outputSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();

	private ServerSocket serverTCPSocket;

	// retour des threads d'envois et receptions
	private Stack<TransferEvent> eventsList = new Stack<TransferEvent>();

	private MulticastSocket serverMulticastSocket = null;
	private DatagramSocket serverUDPSocket = null;
	// private int portTCP = 3009;
	private InetAddress multicastGroup;
	// private int multicastPort = 6789;

	private JConnect jConnect;

	// THREAD

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
				//serverMulticastSocket.setTimeToLive(0);
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

				synchronized (eventsList) {
					while (!eventsList.isEmpty()) {
						TransferEvent event = eventsList.pop();
						SocketConnectivityInfo socketInfo = inputSockets
								.get(event.getSocketAddress());
						switch (event.getState()) {
						case MESSAGE_RECEIVED:
							if(socketInfo!=null){
								socketInfo.setLastReceivedDataDate(System
										.currentTimeMillis());
								
							}
							//Message m = new Message(event.getData());
							System.out.println(event.getData());

							//m.getGroup(); // TODO attente BDD
							break;
						case SEND_FAIL:
							// TODO
							break;
						case SEND_SUCCESS:
							if(socketInfo!=null){
								socketInfo.setLastSentDataDate(System
										.currentTimeMillis());
								
							}
							// TODO
							break;
						default:
							break;
						}
						;
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

	public void addEvent(TransferEvent ev) {
		synchronized (eventsList) {
			eventsList.add(ev);
		}
		synchronized (Gate.this) {
			notifyAll();
		}

	}

	public void sendMulticastMessage(String message) {
		sendMessage(message, null, null);
	}

	public void sendMessage(String message, UUID receiver) {
		List<UUID> receivers = new ArrayList<UUID>();
		receivers.add(receiver);
		sendMessage(message, receivers, TransportType.TCP);
	}

	public void sendMessage(String message, List<UUID> receivers) {
		sendMessage(message, receivers, TransportType.TCP);
	}

	public void sendMessage(String message, List<UUID> receivers,
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

	// private void listenerScheduler() {
	//
	// Timer timer = new Timer();
	//
	// timer.schedule(new TimerTask() {
	// public void run() {
	//
	// synchronized (getOpenedSocketLock()) {
	//
	// for (Entry<SocketAddress, SocketConnectivityInfo> entry :
	// currentOpenedSocketsToListen
	// .entrySet()) {
	// if (!pendingListening.contains(entry.getKey())) {
	// addPendingListeningSocket(entry.getValue());
	//
	// }
	//
	// }
	//
	// }
	//
	// }
	// }, 0, Constants.TIME_GATE_THREAD_REFRESH); // TODO : Prefs
	// }

	// public void sendMessage(String test) {
	// if (portTCP == 3009) {
	// // getRoute from DB, test si present dans currentOpenedSockets
	// try {
	// Socket s = openNewTCPSocket(InetAddress.getLocalHost(), -1);
	// outputGateThreadPool.execute(new UnicastTCPSenderThread(this,
	// s, "Envoi TCP"));
	// outputGateThreadPool.execute(new UnicastTCPSenderThread(this,
	// s, "Envoi TCP"));
	// outputGateThreadPool.execute(new MulticastUDPSenderThread(this,
	// multicastSocket, multicastGroup, multicastPort,
	// "Envoi multi UDP"));
	//
	// } catch (UnknownHostException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }
	//
	// private Socket openNewTCPSocket(InetAddress contact, int contactPort) {
	// Socket res = null;
	// try {
	//
	// res = new Socket(InetAddress.getLocalHost(), 3004);
	//
	// addWriteableSocket(res);
	// } catch (UnknownHostException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return res;
	// }
	//
	// public void openOutputGate() {
	// if (!inputGateOpen) {
	//
	// try {
	// serverSocket = new ServerSocket(3004);// TODO : Pref
	// new ServerThread(this, serverSocket).start();
	// inputGateOpen = true;
	// listenerScheduler();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }

	// // Useless ?
	// public SocketConnectivityInfo getSocketToListen() {
	// SocketConnectivityInfo res = null;
	// long currentMin = Long.MAX_VALUE;
	// for (Entry<SocketAddress, SocketConnectivityInfo> entry :
	// currentOpenedSocketsToListen
	// .entrySet()) {
	// if (entry.getValue().getLastListeningDate() < currentMin
	// && !pendingListening.contains(entry.getKey())) {
	// res = entry.getValue();
	// currentMin = entry.getValue().getLastListeningDate();
	// }
	//
	// }
	// return res;
	// }

	// public void addPendingListeningSocket(SocketConnectivityInfo sci) {
	// pendingListening.add(sci.getSocket().getRemoteSocketAddress());
	// // Useless
	// currentOpenedSocketsToListen.get(
	// sci.getSocket().getRemoteSocketAddress()).setLastListeningDate(
	// System.currentTimeMillis());
	// inputGateThreadPool.execute(new UnicastTCPListenerThread(this, sci
	// .getSocket()));
	//
	// }

	// private void writeableSocketChecker() {
	//
	// Timer timer = new Timer();
	//
	// timer.schedule(new TimerTask() {
	// public void run() {
	// if (!inputGateOpen) // Point observable
	// {
	// this.cancel();
	//
	// } else {
	// synchronized (getWriteableSocketLock()) {
	// Iterator<Map.Entry<SocketAddress, SocketConnectivityInfo>> it =
	// currentWriteableSockets
	// .entrySet().iterator();
	// while (it.hasNext()) {
	// Map.Entry<SocketAddress, SocketConnectivityInfo> pair = it
	// .next();
	//
	// if (pair.getValue().getLastSendDataDate() < System
	// .currentTimeMillis() - 10000) {
	// try {
	// pair.getValue().getSocket().close();
	//
	// it.remove();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// }
	//
	// }
	//
	// }
	//
	// }
	// }, 0, 10000); // TODO : Prefs
	// }

	// public void handlerMessage(SocketAddress sa, String data) {
	// synchronized (getOpenedSocketLock()) {
	// System.out.println("Message  " + data);
	// // AbstractMessage message = new AbstractMessage(data);
	// //
	// if(message.getProtocol().equals(AbstractMessage.transportProtocol.unicastTCP))
	// //
	// currentOpenedSocketsToListen.get(sa).setLastReceivedDataDate(System.currentTimeMillis());
	// }
	// }

	// public void handleEndOfListener(SocketAddress sa, int returnCode) {
	// synchronized (getOpenedSocketLock()) {
	// pendingListening.remove(sa);
	//
	// if (returnCode == AbstractSocketThread.SOCKET_STATUS_CLOSED
	// || returnCode == AbstractSocketThread.SOCKET_STATUS_UNKNOWN_ERROR) {
	//
	// currentOpenedSocketsToListen.remove(sa);
	// System.out.println("Erreur on socket  " + sa);
	// }
	//
	// if (returnCode == AbstractSocketThread.SOCKET_STATUS_TIMEOUT) {
	//
	// if (currentOpenedSocketsToListen.get(sa)
	// .getLastReceivedDataDate() < System.currentTimeMillis() - 1000
	// && currentOpenedSocketsToListen.get(sa)
	// .getLastSendDataDate() < System
	// .currentTimeMillis() - 1000) {
	// System.out.println("Marre, on ferme " + sa);
	// try {
	// currentOpenedSocketsToListen.get(sa).getSocket()
	// .close();
	//
	// currentOpenedSocketsToListen.remove(sa);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// }

	// private void addWriteableSocket(Socket s) {
	// SocketConnectivityInfo sci = new SocketConnectivityInfo(s);
	// currentWriteableSockets.put(s.getRemoteSocketAddress(), sci);
	// log.log(Level.FINER, "Socket w ajouté " + s.getRemoteSocketAddress());
	//
	// }
	//
	// private void addSocketToListen(Socket s) {
	// SocketConnectivityInfo sci = new SocketConnectivityInfo(s);
	// currentOpenedSocketsToListen.put(s.getRemoteSocketAddress(), sci);
	// addPendingListeningSocket(sci);
	// log.log(Level.FINER, "Socket r ajouté " + s.getRemoteSocketAddress());
	//
	// }

	// public void handleEndOfSender(SocketAddress sa, int returnCode, String
	// data) {
	//
	// synchronized (getWriteableSocketLock()) {
	// if (returnCode == AbstractSocketThread.SOCKET_STATUS_CLOSED
	// || returnCode == AbstractSocketThread.SOCKET_STATUS_UNKNOWN_ERROR) {
	// currentWriteableSockets.remove(sa);
	// System.out.println("Erreur on socket  " + sa);
	// }
	// if (returnCode == AbstractSocketThread.SOCKET_STATUS_OK) {
	// // TODO : TCP
	// //
	// currentWriteableSockets.get(sa).setLastSentDataDate(System.currentTimeMillis());
	// }
	// }
	//
	// }

	// private Object getOpenedSocketLock() {
	// return openedSocketLock;
	// }
	//
	// private Object getWriteableSocketLock() {
	// return writeableSocketLock;
	// }

}
