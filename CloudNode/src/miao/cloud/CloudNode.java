package miao.cloud;

import java.io.*;
import java.net.*;
import java.util.*;

public class CloudNode{
	private static final String PROPERTIES_FILE_NAME = "config.properties";
	
	private InetAddress my_ip;
	private static int my_port;
	
	public CloudNode() throws IOException {
		//String proFilePath = System.getProperty("user.dir") + "\\config\\"+ PROPERTIES_FILE_NAME;
		String proFilePath = System.getProperty("user.dir") + "/config/"+ PROPERTIES_FILE_NAME;
		System.out.println(proFilePath);
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
		ResourceBundle rb = new PropertyResourceBundle(inputStream);

		my_port = Integer.parseInt(rb.getString("my_port"));
		
		try {
			my_ip = InetAddress.getByName(rb.getString("my_ip"));
		}catch(MissingResourceException e) {
			my_ip = InetAddress.getLocalHost();
		}catch(UnknownHostException e) {
			my_ip = InetAddress.getLocalHost();
		}
	}
	
	public static void listenSocket() throws IOException {
		ServerSocket serverSocket = new ServerSocket(my_port);
        Socket socket = null;
        boolean f = true;
        while(f) {
        	socket = serverSocket.accept();
        	System.out.println("Connected!");
        	new Thread(new CloudAddQueue(socket)).start();
        }
        serverSocket.close();
	}
	
	// Cloud process message in the cloud queue
	public static void cloudProcessMessage() throws IOException {
		while(true) {
			if(!Main.cloudQueue.isEmpty()) {
				Message message = null;
	
				// Take the first message in the queue
				message = Main.cloudQueue.poll(); // Take an element from queue when it's not empty
	
				// Get the massage information
				int processTime = 0;
				int sqno = message.sqno;
				int ptime = message.ptime;
				InetAddress ipaddr = InetAddress.getByName(message.ipaddr.split("/")[0]);
				int port = message.port;
				processTime = 10*ptime; // 100 times faster than fog node
	
				// Process the message
				try{Thread.sleep(processTime);} catch(InterruptedException e){e.printStackTrace();};	
				System.out.println("cloud served message #" + sqno);
				message.msg = message.msg + " CLOUD" + ":" + "SERVED";
				sendMsgToIOTNode(ipaddr,port,message.msg);		
			}
		}
	}
	
	public static void sendMsgToIOTNode (InetAddress IOTip, int port, String message) {
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket(); // make a Datagram socket
		} catch (SocketException ex) {
			System.out.println("Cannot make Datagram Socket!");
			ex.printStackTrace();
		}
	
		byte[] sendData = new byte[message.getBytes().length]; // make a Byte array of the data to be sent
		sendData = message.getBytes(); // get the bytes of the message
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IOTip, port); // craft the message to be sent
		try {
			clientSocket.send(sendPacket); // send the message
		} catch (IOException ex) {
			System.out.println("I/O exception happened!");
			ex.printStackTrace();
		}	
	}
}
