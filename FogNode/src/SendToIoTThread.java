

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SendToIoTThread  implements Runnable{

	private FogNode fognode = null;
	private Message message = null;
	
	public SendToIoTThread(FogNode fognode, Message message) {
		this.fognode = fognode;
		this.message = message;
	}
	
	@Override
	public void run() {
		
		fognode.sendToIoT(message);
	}
}