package com.jconnect.core.transfer;

import java.net.Socket;
/**
 * Contains some information about opened sockets
 *
 */
public class SocketConnectivityInfo {
	
	private Socket socket;
	private long lastSentDataDate;
	private long lastReceivedDataDate;
	private long lastListeningDate;

	
	public SocketConnectivityInfo(Socket socket)
	{
		this.socket = socket;
		this.lastSentDataDate = 0;
		this.lastReceivedDataDate = 0;
		this.lastListeningDate = 0;
	}
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public long getLastSendDataDate() {
		return lastSentDataDate;
	}
	public void setLastSentDataDate(long lastSentDataDate) {
		this.lastSentDataDate = lastSentDataDate;
	}
	public long getLastReceivedDataDate() {
		return lastReceivedDataDate;
	}
	public void setLastReceivedDataDate(long lastReceivedDataDate) {
		this.lastReceivedDataDate = lastReceivedDataDate;
	}


	

}
