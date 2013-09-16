package com.jconnect.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jconnect.core.transfer.event.TransferEvent;
import com.jconnect.peergroup.AbstractPeerGroup;
import com.jconnect.util.Constants;

public class ServerTCPThread extends Thread {

	private ServerSocket serverSocket;
	private Gate parent;
	private Logger log = Logger.getLogger(AbstractPeerGroup.class.getName());

	public ServerTCPThread(Gate parent, ServerSocket serverSocket) {
		this.parent = parent;
		this.serverSocket = serverSocket;

	}

	public void run() {
		try {
			while (getState() == Thread.State.RUNNABLE) {

				Socket socketclient = serverSocket.accept();

				socketclient.setSoTimeout(Constants.INPUT_TIMEOUT); 
				parent.addTCPInput(socketclient);
				

			}
		} catch (IOException e) {
			log.log(Level.FINER, "Server TCP thread close :"+e.getMessage()); 
		}
		

	}

}
