package miao.IoT;

import java.io.*;
import java.net.*;
import java.util.*;

public class IoTNode implements Runnable {
	
	//private static final int TIMEOUT = 5000;
	private static final String PROPERTIES_FILE_NAME = "config.properties";
		
	private int sequence_num = 0;
	private InetAddress my_ip;
	private int my_port;
	private int interval;

	private InetAddress[] fogAddresses;
	private int[] fogPorts;
	
	private int forward_limit = 0;
	private int process_time = 0;
	
	
	public IoTNode() throws IOException {
		sequence_num = 0;
		
		getConfig();
		createRandomCons();
	}
	
	//Create forward limit and process time randomly for each request.
	public void createRandomCons() {
		forward_limit = 2 + (int)(Math.random()*4); // Random in [2, 5]
		process_time = 3 + (int)(Math.random()*5); // Random in [3, 7]
	}
	
	public void getConfig() throws IOException {
		String proFilePath = System.getProperty("user.dir") + "/config/"+ PROPERTIES_FILE_NAME;
		System.out.println(proFilePath);
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
		ResourceBundle rb = new PropertyResourceBundle(inputStream);

		my_port = Integer.parseInt(rb.getString("my_port"));
		interval = Integer.parseInt(rb.getString("interval"));
		
		try {
			my_ip = InetAddress.getByName(rb.getString("my_ip"));
		}catch(MissingResourceException e) {
			my_ip = InetAddress.getLocalHost();
		}
		String[] fog_nodes = rb.getString("fog_nodes").split("-");
		int fog_nodes_length = fog_nodes.length;
		
		InetAddress[] fogAddresses = new InetAddress[fog_nodes_length/2];
		int [] fogPorts = new int[fog_nodes_length/2];

		if(fog_nodes_length > 0 && fog_nodes_length%2 == 0) {
			for(int i = 0; i < fog_nodes_length; i+=2) {
				fogAddresses[i/2] = InetAddress.getByName(fog_nodes[i]);
				fogPorts[i/2] = Integer.parseInt(fog_nodes[i+1]);
			}
			this.fogAddresses = fogAddresses;
			this.fogPorts = fogPorts;
		}else {
			System.out.println("Items in \"fog nodes\" should be even number.");
		}
	}
	
	//Create the package message to send to a fog node.
	public String createSendStr() {
		createRandomCons();
		String str_send = "1-";
		str_send += sequence_num; //1
		str_send += "-" + forward_limit; //2
		str_send += "-" + process_time; //3
		str_send += "-" + my_ip; //4
		str_send += "-" + my_port; //5
		sequence_num++;
		return str_send;
	}
	
	// Randomly pick a fog node to send request.
	public int pickFogNode() {
		int fogId = (int)(Math.random()*fogAddresses.length);
		return fogId;
	}
	
	// construct a request packet and send it
	public void generateRequest() throws IOException{
		
		int fogId = pickFogNode(); // pick a fog node randomly
		InetAddress loc = fogAddresses[fogId];
		int port = fogPorts[fogId];		
		
		DatagramSocket ds = new DatagramSocket();
		
		String str_send = createSendStr();
		DatagramPacket dp_send = new DatagramPacket(str_send.getBytes(), str_send.length(), loc, port);
		
		ds.send(dp_send);
		ds.close();
	}
	
	public void receiveRes() throws SocketException {
		DatagramSocket ds = new DatagramSocket(my_port);
		byte[] buf = new byte[10000];
		DatagramPacket dp_receive = new DatagramPacket(buf, buf.length);

		boolean receivedResponse = false;
		while(true) {
			try {
				ds.receive(dp_receive);
				receivedResponse = true;
			} catch (IOException e) {
				e.printStackTrace();
			} 

			if(receivedResponse) {
				String str_receive ="Msg["+ new String(dp_receive.getData(), 0, dp_receive.getLength()) + "] from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
				System.out.println(str_receive);
				dp_receive.setLength(buf.length);
			}
		}
	}

	@Override
	public void run() {
		while(true) {
			try {
				generateRequest();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
