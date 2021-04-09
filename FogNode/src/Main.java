import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
	

	public static void main(String[] args) throws UnknownHostException, IOException, Exception {
		FogNode fognode = new FogNode(Integer.parseInt(args[0]),args[1]);
		//FogNode fognode = new FogNode(2, "/Users/liu/Desktop/config.txt");
		
		new Thread(new ListenIotThread(fognode)).start();
		new Thread(new ListenNeighborThread(fognode)).start();
		Thread.sleep(10000);
		fognode.createConnection();
		//Thread.sleep(10000);
		
		new Thread(new SendStateThread(fognode,3)).start();
		fognode.processRequest();
        

	}

}
