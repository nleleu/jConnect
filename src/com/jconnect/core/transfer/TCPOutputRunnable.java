package com.jconnect.core.transfer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import com.jconnect.core.Gate;
import com.jconnect.core.event.TransferEvent;
import com.jconnect.core.model.RouteModel;
import com.jconnect.core.model.RouteModel.TransportType;

/**
 * Manages the TCP messages' emission 
 */
public class TCPOutputRunnable  extends AbstractSocketRunnable {


	private String message=new String();
	private Socket usingSocket;
	private RouteModel route;
	private int tryCount;

	public TCPOutputRunnable (Gate parent, RouteModel routeModel,String message){
		this(parent, routeModel, message, 0);
	}
	
	public TCPOutputRunnable (Gate parent, RouteModel routeModel,String message, int tryCount)
	{
		super(parent);
		this.route = routeModel;
		this.message = message;
		this.tryCount = tryCount;
	}

	public void run()
	{
		usingSocket = parent.getSocket(route.getSocketAddress());
		if(usingSocket==null || usingSocket.isClosed())
		{
			TransferEvent e = new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SOCKET_CLOSED, TransportType.TCP);
			e.setData(message);
			e.setRoute(route);
			e.setTryCount(tryCount);
			parent.addEvent(e);
			
			
			
		}

		else
		{
			try {


				PrintWriter out = new PrintWriter(usingSocket.getOutputStream());
				out.println(message);
				out.flush();
				TransferEvent e = new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SEND_SUCCESS, TransportType.TCP);
				parent.addEvent(e);
			} catch (IOException ex) {
				TransferEvent e = new TransferEvent(usingSocket.getRemoteSocketAddress(),	TransferEvent.State.SEND_FAIL, TransportType.TCP);
				e.error = ex;
				e.setData(message);
				e.setRoute(route);
				e.setTryCount(tryCount);
				parent.addEvent(e);
			}

		}





	}



}
