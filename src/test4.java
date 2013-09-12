import com.jconnect.core.Gate;
import com.jconnect.core.WindowApp;


public class test4 {
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
                g.stopInputGate();
            }
        });
		WindowApp w = new WindowApp();

	}

}
