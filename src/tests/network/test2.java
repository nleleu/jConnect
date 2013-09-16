package tests.network;
import com.jconnect.core.Gate;
import com.jconnect.core.WindowApp;


public class test2 {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		final Gate g = new Gate();
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook ran!");
                g.closeInputGate();
            }
        });
		WindowApp w = new WindowApp();

	}

}
