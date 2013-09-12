package com.jconnect.core.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Observable;
import java.util.Observer;

import com.jconnect.core.InputGate;


public class MulticastListenerThread extends Observable implements Runnable {

	private boolean running = true;
	
	public MulticastListenerThread (InputGate parent)
	{
		addObserver((Observer) parent);
	}
	
	
	public void stopListerner()
	{
		running = false;
	}
	
	
	
	public void run()
	{

		MulticastSocket s = null;
		try {
			InetAddress group = InetAddress.getByName("228.5.6.7"); //TODO Prefs
		
			s = new MulticastSocket(6789);//TODO Prefs
		 s.joinGroup(group);
		 byte[] buf = new byte[1000]; // TODO : constante ?
		 DatagramPacket recv = new DatagramPacket(buf, buf.length);
		 while(running)
		 {
			  s.receive(recv);
			  notifyObservers(recv.getData());
		 }
		
		 s.close();
		 
		} catch (IOException e) {
			if(s!=null)
				s.close();
		}
			
		
	}
}
