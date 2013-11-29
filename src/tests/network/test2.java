package tests.network;
import java.io.IOException;

import com.jconnect.JConnect;
import com.jconnect.impl.peergroup.NetPeerGroup;
import com.jconnect.util.WindowApp;


public class test2 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		final JConnect jconnect = new JConnect();
		try {
			jconnect.getGate().start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		NetPeerGroup netPeerGroup = (NetPeerGroup) jconnect.getPeerGroupManager().newGroupInstance(NetPeerGroup.class, NetPeerGroup.NETPEERGROUP_UUID);
		jconnect.getPeerGroupManager().startAllGroup();
		
		//jconnect.getGate().sendMulticastMessage(new Message("test").toString());
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook ran!");
                jconnect.getGate().stop();
                jconnect.getPeerGroupManager().stopAllGroup();
            }
        });
		WindowApp w = new WindowApp();
		
//		Timer t =new Timer();
//		
//		t.scheduleAtFixedRate(new TimerTask() {
//			
//			@Override
//			public void run() {
//				//jconnect.getGate().sendMulticastMessage("coucou"+System.currentTimeMillis());
//				
//			}
//		}, 1000, 2000);

	}

}
