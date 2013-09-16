package tests.network;
import java.io.IOException;

import com.jconnect.JConnect;
import com.jconnect.core.Gate;
import com.jconnect.core.WindowApp;


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
		jconnect.getGate().sendMulticastMessage("coucou");
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook ran!");
                jconnect.getGate().stop();
            }
        });
		WindowApp w = new WindowApp();

	}

}
