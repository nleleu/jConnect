package com.jconnect.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jconnect.core.network.AbstractSocketThread;
import com.jconnect.core.network.ServerThread;
import com.jconnect.core.network.SocketConnectivityInfo;
import com.jconnect.core.network.UnicastListenerThread;

public class Gate {

	private ExecutorService inputGateThreadPool = Executors.newFixedThreadPool(5); //TODO : Prefs
	private ExecutorService outputGateThreadPool = Executors.newFixedThreadPool(5);//TODO : Prefs
	private HashMap<SocketAddress, SocketConnectivityInfo> currentOpenedSockets = new HashMap<SocketAddress, SocketConnectivityInfo>();
	private HashSet<SocketAddress> pendingListening = new HashSet<SocketAddress>();
	private boolean inputGateOpen = false;
	private boolean outputGateOpen = false;
	private ServerSocket serverSocket;

	public Gate()
	{
		openInputGate();
	}

	//Useless ?
	public SocketConnectivityInfo getSocketToListen()
	{
		SocketConnectivityInfo res = null;
		long currentMin = Long.MAX_VALUE;
		for(Entry<SocketAddress, SocketConnectivityInfo> entry : currentOpenedSockets.entrySet()) {
			if(entry.getValue().getLastListeningDate() < currentMin && !pendingListening.contains(entry.getKey()))
			{
				res = entry.getValue();
				currentMin = entry.getValue().getLastListeningDate();
			}

		}
		return res;
	}

	public void openInputGate()
	{
		if(!inputGateOpen)
		{

			try {
				serverSocket=new ServerSocket(3002);//TODO : Pref
				new ServerThread(this, serverSocket).start();
				inputGateOpen = true;
				listenerScheduler();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}
	}

	public void stopInputGate()
	{
		if(inputGateOpen)
		{

			try {
				serverSocket .close();

				inputGateOpen = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	//Hmmm
	private Gate getGate()
	{
		return this;
	}

	public void addPendingSocket(SocketConnectivityInfo sci)
	{
		pendingListening.add(sci.getSocket().getRemoteSocketAddress());
		//Useless
		currentOpenedSockets.get(sci.getSocket().getRemoteSocketAddress()).setLastListeningDate(System.currentTimeMillis());
		inputGateThreadPool.execute(new UnicastListenerThread(this,sci.getSocket()));
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
					synchronized(getGate()) {

						for(Entry<SocketAddress, SocketConnectivityInfo> entry : currentOpenedSockets.entrySet()) {
							if( !pendingListening.contains(entry.getKey()))
							{
								addPendingSocket( entry.getValue());


							}


						}


					}

				}



			}
		}, 0, 10000); //TODO : Prefs
	}


	synchronized public void handleEndOfListener(SocketAddress sa,int returnCode,ArrayList<String> data)
	{

		pendingListening.remove(sa);
		for(String s : data)
		{
			System.out.println("Message  "+s);
			currentOpenedSockets.get(sa).setLastReceivedDataDate(System.currentTimeMillis());
		}
		if(returnCode==AbstractSocketThread.SOCKET_STATUS_CLOSED || returnCode==AbstractSocketThread.SOCKET_STATUS_UNKNOWN_ERROR)
		{
			
			currentOpenedSockets.remove(sa);
			System.out.println("Erreur on socket  "+sa);
		}

		if(returnCode==AbstractSocketThread.SOCKET_STATUS_TIMEOUT)
		{
			
			if(currentOpenedSockets.get(sa).getLastReceivedDataDate()<System.currentTimeMillis()-1000 && currentOpenedSockets.get(sa).getLastSendDataDate()<System.currentTimeMillis()-1000)
			{
				System.out.println("Marre, on ferme "+sa);
				try {
					currentOpenedSockets.get(sa).getSocket().close();

					currentOpenedSockets.remove(sa);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	synchronized public void addSocket(Socket s)
	{
		SocketConnectivityInfo sci = new SocketConnectivityInfo(s);
		currentOpenedSockets.put(s.getRemoteSocketAddress(), sci);
		addPendingSocket(sci);
		System.out.println("Socket ajouté "+s.getRemoteSocketAddress());

	}

	synchronized public void handleEndOfSender(SocketAddress sa,int returnCode,String data) {
		

		if(returnCode==AbstractSocketThread.SOCKET_STATUS_CLOSED || returnCode==AbstractSocketThread.SOCKET_STATUS_UNKNOWN_ERROR)
		{
			currentOpenedSockets.remove(sa);
			System.out.println("Erreur on socket  "+sa);
		}
		if(returnCode==AbstractSocketThread.SOCKET_STATUS_OK)
		{
			currentOpenedSockets.get(sa).setLastSentDataDate(System.currentTimeMillis());
		}
		
		
	}









}
