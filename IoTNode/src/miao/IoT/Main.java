package miao.IoT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

public class Main {

	public static void tryIt() throws UnknownHostException {
		
	}
	
	
	
	public static void main(String[] args) throws IOException {
		IoTNode iotnode = new IoTNode();
		Thread iotthread = new Thread(iotnode);
		iotthread.start();
		iotnode.receiveRes();
	}

}
