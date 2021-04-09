


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SendToCloudThread  implements Runnable{

	private FogNode fognode = null;
	private Message message = null;
	
	public SendToCloudThread(FogNode fognode, Message message) {
		this.fognode = fognode;
		this.message = message;
	}
	
	
	@Override
	public void run() {
		fognode.sendToCloud(message);
	}
}