package com.jconnect.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.util.Constants;


public class ServerMulticastThread extends Thread {



	private MulticastSocket serverSocket;
	private Gate parent;
	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());




	public ServerMulticastThread (Gate parent,MulticastSocket serverSocket)
	{
		this.parent=parent;
		this.serverSocket = serverSocket;

	}


	public void run()
	{


		try {
			while(getState()==Thread.State.RUNNABLE)
			{
				byte[] buf = new byte[Constants.SERVER_BUFFER_SIZE]; 
				DatagramPacket recv = new DatagramPacket(buf, buf.length);
				serverSocket.receive(recv);
				String data = new String(recv.getData(), recv.getOffset(), recv.getLength());
				TransferEvent ev = new TransferEvent(null, TransferEvent.State.MESSAGE_RECEIVED);
				ev.setData(data);
				parent.addEvent(ev);

			}



		} catch (IOException e) {
			log.log(Level.FINER, "Server TCP thread close :"+e.getMessage()); 
		}


	}
}
