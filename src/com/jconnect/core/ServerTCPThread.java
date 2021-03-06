package com.jconnect.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.jconnect.core.peergroup.AbstractPeerGroup;
import com.jconnect.util.Constants;

/**
 * Thread server constantly listening on TCP Socket
 */
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
			log.log(Level.DEBUG, "Server TCP thread close :"+e.getMessage()); 
		}
		

	}

}
