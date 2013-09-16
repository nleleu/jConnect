package com.jconnect.core;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jconnect.core.transfer.AbstractSocketThread;
import com.jconnect.core.transfer.MulticastUDPListenerThread;
import com.jconnect.core.transfer.MulticastUDPSenderThread;
import com.jconnect.core.transfer.ServerThread;
import com.jconnect.core.transfer.SocketConnectivityInfo;
import com.jconnect.core.transfer.TransferEvent;
import com.jconnect.core.transfer.UnicastTCPListenerThread;
import com.jconnect.core.transfer.UnicastTCPSenderThread;
import com.jconnect.core.transfer.UnicastUDPListenerThread;
import com.jconnect.core.transfer.UnicastUDPSenderThread;

public class Gate {

	private ExecutorService inputGateThreadPool = Executors.newFixedThreadPool(5); //TODO : Prefs
	private ExecutorService outputGateThreadPool = Executors.newFixedThreadPool(5);//TODO : Prefs
	private HashMap<SocketAddress, SocketConnectivityInfo> currentOpenedSocketsToListen = new HashMap<SocketAddress, SocketConnectivityInfo>();
	private HashSet<SocketAddress> pendingListening = new HashSet<SocketAddress>();
	private HashMap<SocketAddress, SocketConnectivityInfo> currentWriteableSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();
	private boolean inputGateOpen = false;
	private boolean outputGateOpen = false;
	private ServerSocket serverSocket;

	//Que les messages reçu ? Les codes de retour de threads sont gérés direct par la gate
	private ArrayList<TransferEvent> eventsList;
	
	private final Object openedSocketLock = new Object();
	private final Object writeableSocketLock = new Object();
	private final Object eventsListLock = new Object();
	
	
	private MulticastSocket multicastSocket = null;
	private DatagramSocket UDPSocket = null;
	private int portTCP = 3009;
	private InetAddress multicastGroup;
	private int multicastPort = 6789;

	public Gate()
	{
		openInputGate();createUDPSocket();createMulticastSocket();sendMessage("tt");

	}

	public void sendMessage(String test)
	{
		if(portTCP==3009)
		{
		// getRoute from DB, test si present dans currentOpenedSockets
		try {
			Socket s = openNewTCPSocket(InetAddress.getLocalHost(),-1);
			outputGateThreadPool.execute(new UnicastTCPSenderThread(this, s,"Envoi TCP"));
			outputGateThreadPool.execute(new UnicastTCPSenderThread(this, s,"Envoi TCP"));
			outputGateThreadPool.execute(new MulticastUDPSenderThread(this, multicastSocket,multicastGroup,multicastPort,"Envoi multi UDP"));
			
	

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

	}

	public void createMulticastSocket()
	{

		try {
			multicastGroup = InetAddress.getByName("228.5.6.7");


			multicastSocket = new MulticastSocket(multicastPort);//TODO Prefs
			multicastSocket.setTimeToLive(1);
			multicastSocket.joinGroup(multicastGroup);
			new MulticastUDPListenerThread(this, multicastSocket).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //TODO Prefs

	}

	public void createUDPSocket()
	{
		try {
			UDPSocket = new DatagramSocket(portTCP);

			new UnicastUDPListenerThread(this, UDPSocket).start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Socket openNewTCPSocket(InetAddress contact, int contactPort)
	{
		Socket res = null;
		try {


			res = new Socket(InetAddress.getLocalHost(),3004);


			addWriteableSocket(res);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public void openInputGate()
	{
		if(!inputGateOpen)
		{

			try {
				serverSocket=new ServerSocket(portTCP);//TODO : Pref
				new ServerThread(this, serverSocket).start();
				inputGateOpen = true;
				listenerScheduler();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}
	}


	public void openOutputGate()
	{
		if(!inputGateOpen)
		{

			try {
				serverSocket=new ServerSocket(3004);//TODO : Pref
				new ServerThread(this, serverSocket).start();
				inputGateOpen = true;
				listenerScheduler();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}
	}

	public void closeMulticastSocket()
	{

		if(multicastSocket!=null)
			multicastSocket.close();

	}




	//Useless ?
	public SocketConnectivityInfo getSocketToListen()
	{
		SocketConnectivityInfo res = null;
		long currentMin = Long.MAX_VALUE;
		for(Entry<SocketAddress, SocketConnectivityInfo> entry : currentOpenedSocketsToListen.entrySet()) {
			if(entry.getValue().getLastListeningDate() < currentMin && !pendingListening.contains(entry.getKey()))
			{
				res = entry.getValue();
				currentMin = entry.getValue().getLastListeningDate();
			}

		}
		return res;
	}


	public void closeOutputGate()
	{
		if(outputGateOpen)
		{

			try {

				for(Entry<SocketAddress, SocketConnectivityInfo> entry : currentWriteableSockets.entrySet()) {

					entry.getValue().getSocket().close();
				}

				currentWriteableSockets.clear();

				outputGateOpen = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


	public void closeInputGate()
	{
		if(inputGateOpen)
		{
			try {
				for(Entry<SocketAddress, SocketConnectivityInfo> entry : currentOpenedSocketsToListen.entrySet()) {

					entry.getValue().getSocket().close();
				}

				currentOpenedSocketsToListen.clear();

				serverSocket .close();

				inputGateOpen = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}






	public void addPendingListeningSocket(SocketConnectivityInfo sci)
	{
		pendingListening.add(sci.getSocket().getRemoteSocketAddress());
		//Useless
		currentOpenedSocketsToListen.get(sci.getSocket().getRemoteSocketAddress()).setLastListeningDate(System.currentTimeMillis());
		inputGateThreadPool.execute(new UnicastTCPListenerThread(this,sci.getSocket()));
		
	}


	private void writeableSocketChecker()
	{

		Timer timer = new Timer();

		timer.schedule (new TimerTask() {
			public void run()
			{
				if(!inputGateOpen) //Point observable
				{
					this.cancel();

				}
				else
				{
					synchronized(getWriteableSocketLock()) {
						Iterator<Map.Entry<SocketAddress, SocketConnectivityInfo>> it = currentWriteableSockets.entrySet().iterator();
						while(it.hasNext()) {
							Map.Entry<SocketAddress, SocketConnectivityInfo> pair = it.next();

							if(pair.getValue().getLastSendDataDate()<System.currentTimeMillis()-10000 )
							{
								try {
									pair.getValue().getSocket().close();

									it.remove();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}


							}


						}


					}

				}



			}
		}, 0, 10000); //TODO : Prefs
	}

	private void listenerScheduler()
	{

		Timer timer = new Timer();

		timer.schedule (new TimerTask() {
			public void run()
			{
				if(!inputGateOpen) //Point observable
				{
					this.cancel();

				}
				else
				{
					synchronized(getOpenedSocketLock()) {

						for(Entry<SocketAddress, SocketConnectivityInfo> entry : currentOpenedSocketsToListen.entrySet()) {
							if( !pendingListening.contains(entry.getKey()))
							{
								addPendingListeningSocket( entry.getValue());


							}


						}


					}

				}



			}
		}, 0, 10000); //TODO : Prefs
	}
	
	
	

	public void addEvent(TransferEvent ev)
	{
		synchronized(eventsListLock) {
		
			eventsList.add(ev);
		}
		
	}
	
	public void handlerMessage(SocketAddress sa,String data)
	{
		synchronized(getOpenedSocketLock()) {
			System.out.println("Message  "+data);
//			AbstractMessage message = new AbstractMessage(data);
//			if(message.getProtocol().equals(AbstractMessage.transportProtocol.unicastTCP))
//				currentOpenedSocketsToListen.get(sa).setLastReceivedDataDate(System.currentTimeMillis());
		}
	}



	public void handleEndOfListener(SocketAddress sa,int returnCode)
	{
		synchronized(getOpenedSocketLock()) {
			pendingListening.remove(sa);

			if(returnCode==AbstractSocketThread.SOCKET_STATUS_CLOSED || returnCode==AbstractSocketThread.SOCKET_STATUS_UNKNOWN_ERROR)
			{

				currentOpenedSocketsToListen.remove(sa);
				System.out.println("Erreur on socket  "+sa);
			}

			if(returnCode==AbstractSocketThread.SOCKET_STATUS_TIMEOUT)
			{

				if(currentOpenedSocketsToListen.get(sa).getLastReceivedDataDate()<System.currentTimeMillis()-1000 && currentOpenedSocketsToListen.get(sa).getLastSendDataDate()<System.currentTimeMillis()-1000)
				{
					System.out.println("Marre, on ferme "+sa);
					try {
						currentOpenedSocketsToListen.get(sa).getSocket().close();

						currentOpenedSocketsToListen.remove(sa);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}


	public void addWriteableSocket(Socket s)
	{
		synchronized(getWriteableSocketLock()) {
			SocketConnectivityInfo sci = new SocketConnectivityInfo(s);
			currentWriteableSockets.put(s.getRemoteSocketAddress(), sci);
			System.out.println("Socket w ajouté "+s.getRemoteSocketAddress());
		}

	}

	public void addSocketToListen(Socket s)
	{
		synchronized(getOpenedSocketLock()) {
			SocketConnectivityInfo sci = new SocketConnectivityInfo(s);
			currentOpenedSocketsToListen.put(s.getRemoteSocketAddress(), sci);
			addPendingListeningSocket(sci);
			System.out.println("Socket r ajouté "+s.getRemoteSocketAddress());
		}

	}

	public void handleEndOfSender(SocketAddress sa,int returnCode,String data) {

		synchronized(getWriteableSocketLock()) {
			if(returnCode==AbstractSocketThread.SOCKET_STATUS_CLOSED || returnCode==AbstractSocketThread.SOCKET_STATUS_UNKNOWN_ERROR)
			{
				currentWriteableSockets.remove(sa);
				System.out.println("Erreur on socket  "+sa);
			}
			if(returnCode==AbstractSocketThread.SOCKET_STATUS_OK)
			{
				//TODO : TCP
				//currentWriteableSockets.get(sa).setLastSentDataDate(System.currentTimeMillis());
			}
		}


	}

	private Object getOpenedSocketLock() {
		return openedSocketLock;
	}

	private Object getWriteableSocketLock() {
		return writeableSocketLock;
	}











}
