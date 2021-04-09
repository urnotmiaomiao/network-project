import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentLinkedQueue;


public class FogNode {
	private InetAddress my_ip;
	private static int my_udp;
	private int my_tcp;

	private InetAddress cloud_ip;
	private int cloud_tcp;
	
	private InetAddress[] fogAddresses;
	private int[] fogPorts;
	private int[] neighborNodeId;
	private int[] neighborQueueTime;
	
	private int max_response_time = 0;
	private int nodeId;
	List<Node> lis = new ArrayList<>();
	public static ConcurrentLinkedQueue<Message> fogQueue = new ConcurrentLinkedQueue<Message>();
	
	private Socket[] socketTONeighbors;
	private OutputStream[] outToNeighbors;
	private Socket socketTOCloud;
	private OutputStream outToCloud;
	
	
	public FogNode() {}
	
	public FogNode(int nodeID, String path) throws IOException {
		getConfig(nodeID,path);
		//createConnection();
	}	
	
	public void getConfig(int nodeID, String path) throws IOException {
		int network_size = 0;
		try {
			File file_name = new File(path);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file_name));
			BufferedReader buf = new BufferedReader(reader);
			int num_of_valid_lines = 0;
			String line = buf.readLine();
			String[] token;
			//int num = 0;
			while (line != null) {
				line = line.replaceAll("#(.*)","");    // Delete comments
				if(line.matches("^\\s*$")) {
					line = buf.readLine();             // Delete empty lines
					continue;
				}
				line = line.replaceAll("^\\s+","");    // Delete space in the begining

				token = line.split("\\s+");            // Save each entity in token array
				num_of_valid_lines++;
				//System.out.println("line = " + num_of_valid_lines);
				if (num_of_valid_lines == 1){
					network_size = Integer.parseInt(token[0]);   // Get Number of node
				}else if(num_of_valid_lines <= network_size+1 ){
					Node n = new Node();
					n.nodeId = nodeID;
					n.my_ip = InetAddress.getByName(token[1]);
					n.my_tcp = Integer.parseInt(token[2]);		
					if (Integer.parseInt(token[0]) == nodeID) {
						nodeId = nodeID;
						my_ip = InetAddress.getByName(token[1]);
						my_tcp = Integer.parseInt(token[2]);
						my_udp = Integer.parseInt(token[3]);
						max_response_time = Integer.parseInt(token[4]);
						cloud_ip = InetAddress.getByName(token[5]);
						cloud_tcp = Integer.parseInt(token[6]);
					}
					lis.add(n);
				}else {
					// n+2 : 2n+1 each line contain neighbors info
					// First token is node id					
					if (Integer.parseInt(token[0]) == nodeId) {	
						int j = 1;
						neighborNodeId = new int[token.length-1];
						fogAddresses = new InetAddress[token.length-1];
						fogPorts = new int[token.length-1];
						neighborQueueTime = new int[token.length-1];
						while(j<token.length) {
							neighborNodeId[j-1] = Integer.parseInt(token[j]);
							j++;
						}
					}
				}
				line = buf.readLine();
			}
			reader.close();
			for(int j = 0; j<neighborNodeId.length;j++) {
				fogAddresses[j] = lis.get(neighborNodeId[j]-1).my_ip;
				fogPorts[j] = lis.get(neighborNodeId[j]-1).my_tcp;
				neighborQueueTime[j] = 0;
				//System.out.println(neighborNodeId[j]);
			}
			/*
			System.out.println(my_ip);
			System.out.println(my_tcp);
			System.out.println(my_udp);
			System.out.println(cloud_ip);
			System.out.println(cloud_tcp);
			for(int j = 0; j<neighborNodeId.length;j++) {
				System.out.println(neighborNodeId[j]);
				System.out.println(fogAddresses[j]);
				System.out.println(fogPorts[j]);
			}
			*/
			
		}catch (Exception err) {err.printStackTrace();}
	}

	public void updateQueue(Message message) {
		int id = message.node_id;
		int que = message.queue_time;
		for(int i = 0; i<neighborNodeId.length;i++) {
			if(neighborNodeId[i] == id) {
				neighborQueueTime[i] = que;
			}
		}
	}
	
	public int handleRequest(Message message) {
		 int responseTime = message.ptime + getQueueingTime();
		 if(message.forward_limit < 0) return 3;
		 else if(responseTime > max_response_time)
			 return 2;
		 else return 1;
		 
		 
	 }
	// 1 add queue
	// 2 forward
	// 3 cloud
	public int getQueueingTime() {
		int res = 0;
		for(Message it:fogQueue) {
			res += it.ptime;
		}
		return res;
	}

	public String createStateMsg() {
		String s = "0-"+ nodeId+"-"+getQueueingTime()+"\n";
		//System.out.println(s);
		return s;
	}
	
	public int pickNeighbor() {
		 int smallestQueueingTime = Integer.MAX_VALUE;
			int bestNeighborID = -1;

			System.out.print(nodeId  + " choose best neighbor:");
			for(int i=0; i<neighborNodeId.length; i++) {
				System.out.print(" " + neighborNodeId[i] + "(" + neighborQueueTime[i] + ")");
				if(neighborQueueTime[i] < smallestQueueingTime) {
					smallestQueueingTime = neighborQueueTime[i];
					bestNeighborID = neighborNodeId[i];
				}
			}
			System.out.println();
			return bestNeighborID;
	 }
	// - return nodeId
	
	
	// mm
	public String createReqMsg(Message message){
		String msg = null;
		msg = "1-"+message.requestSequenNum+"-"+(message.forward_limit-1)+"-"+message.ipaddr+"-"+message.port+"-"+message.ptime+"-"+message.msg+" "+nodeId+" Forwarded"+"\n";
		return msg;
	}
	
	public void createConnection() {
		this.socketTONeighbors = new Socket[this.fogAddresses.length];
		this.outToNeighbors = new OutputStream[this.fogAddresses.length];
		
        try {
        	for(int i = 0; i<this.fogAddresses.length;i++) {
        		//System.out.println("Node"+this.nodeId+" connets to Node"+this.neighborNodeId[i]);
        		
        		Socket socket = new Socket(this.fogAddresses[i], this.fogPorts[i]);
        		this.socketTONeighbors[i] = socket;
        		OutputStream out = socket.getOutputStream();
        		this.outToNeighbors[i] = out;
        	}
        	this.socketTOCloud = new Socket(this.cloud_ip, this.cloud_tcp);
        	this.outToCloud = socketTOCloud.getOutputStream();
            
        } catch (IOException e) {
            //e.printStackTrace();
        }
	}
	
	//TCP
	public void sendMsgToNeighbor(int nodeId, Message message) {
		String msg = createReqMsg(message); 
        try {
        	//Socket socket = this.socketTONeighbors[nodeId];
        	int i = 0;
        	for(i = 0; i<outToNeighbors.length;i++) {
        		if(neighborNodeId[i] == nodeId)
        			break;
        	}
        	if(i == outToNeighbors.length)
        		i = i-1;
            OutputStream out = this.outToNeighbors[i];
            out.write(msg.getBytes());
            System.out.println("Node"+this.nodeId+" forward request to Node"+nodeId);
        } catch (IOException e) {
           // e.printStackTrace();
        }
	}

	// TCP
	public void sendStateToNeighbor() {
		String msg = createStateMsg();
		
			for(int i = 0; i<neighborNodeId.length; i++) {
				try {
					System.out.println(nodeId+":"+"update queueing time");
		            this.outToNeighbors[i].write(msg.getBytes());
				}catch (IOException e) {
					this.createConnection();
					try {
						this.outToNeighbors[i].write(msg.getBytes());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		            //e.printStackTrace();
		        }
				
			}	
		
	}
	
	//TCP
	public void sendToCloud (Message message) {
		String msg = message.ipaddr +"-"+message.port+"-"+message.msg+"-"+message.ptime+"-"+message.requestSequenNum+"\n"; 
		System.out.println("Node"+this.nodeId+" forward request to Cloud");
		Socket socket = null;
        try {
            socket = this.socketTOCloud;

            OutputStream out = this.outToCloud;
            out.write(msg.getBytes());

            //socket.shutdownOutput(); 
            
            // get message from cloud node
//            String line;
//            System.out.println("Cloud said:");
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
            
        } catch (IOException e) {
            //e.printStackTrace();
        }
	}	
	
	// public void sendToIoT()
	public void sendToIoT(Message message) {
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket(); // make a Datagram socket
		} catch (SocketException ex) {
			System.out.println("Cannot make Datagram Socket!");
			ex.printStackTrace();
		}
		
		InetAddress ipaddr = null;
		try {
			ipaddr = InetAddress.getByName(message.ipaddr.split("/")[0]);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int port = message.port;
	
		String msg = message.msg;
		
		byte[] sendData = new byte[msg.getBytes().length]; // make a Byte array of the data to be sent
		sendData = msg.getBytes(); // get the bytes of the message
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipaddr, port); // craft the message to be sent
		try {
			clientSocket.send(sendPacket); // send the message
		} catch (IOException ex) {
			System.out.println("I/O exception happened!");
			ex.printStackTrace();
		}	
	}

	public void listenToIoT(FogNode fognode) throws SocketException, UnknownHostException {
		DatagramSocket ds = new DatagramSocket(my_udp);
		byte[] buf = new byte[10000];
		DatagramPacket dp_receive = new DatagramPacket(buf, buf.length);

		boolean receivedResponse = false;
		while(true) {
			try {
				ds.receive(dp_receive);
				receivedResponse = true;
			} catch (IOException e) {
				//e.printStackTrace();
			} 

			if(receivedResponse) {
				//String str_receive ="Msg["+ new String(dp_receive.getData(), 0, dp_receive.getLength()) + "] from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
				String str_receive =new String(dp_receive.getData(), 0, dp_receive.getLength());
				//System.out.println("str_receive : "+str_receive);
				Message message = new Message(str_receive);
				//System.out.println("message : "+message);
				handleMessage(fognode, message);
				dp_receive.setLength(buf.length);
			}
		}
	}
	
	// public void listenToNeighbor()
	// TCP -> Message message
	// Message.isRequestMsg
	// - stateMsg: updateQueue(message)
	// - requestMsg: handleRequest(message)
	// - - 1: add queue
	// - - 2: forward: pickNeighbor -> sendMsgToNeighbor(nodeId)
	// - - 3: cloud: sendToCloud(message)
	public void listenToNeighbor() {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(my_tcp);
	        Socket socket = null;
	        boolean f = true;
	        while(f) {
	        	socket = serverSocket.accept();
	        	if(socket!=null) {
	        		new Thread(new GetMessage(this, socket)).start();
	        	}
	        	
	        }
	        serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void handleMessage(FogNode fognode, Message message) {
		if(message.isRequestMsg) {
			// Request Message
			int way = handleRequest(message);
			switch(way) {
			case 1:
				fogQueue.add(message);
				break;
			case 2:
	        	new Thread(new SendMsgToNeighborThread(fognode, pickNeighbor(), message)).start();
				sendMsgToNeighbor(pickNeighbor(), message);
				break;
			case 3:
				new Thread(new SendToCloudThread(fognode, message)).start();
				break;
			}
		}else {
			// State Message
			System.out.println("Node"+nodeId+" received state message: "+message.node_id+" "+message.queue_time);
			updateQueue(message);
			//System.out.println(" AFTER ::: Node"+nodeId+" received state message: "+message.node_id+" "+message.queue_time);
		}
	}
	
	// public void processRequest()
	// - copy from cloud
	public void processRequest() {
		while(true) {
			if(!this.fogQueue.isEmpty()) {
				Message message = null;
	
				// Take the first message in the queue
				message = this.fogQueue.poll(); // Take an element from queue when it's not empty

				int requestSequenNum = message.requestSequenNum;
				int ptime = message.ptime;
				
				int processTime = 0;
				
				processTime = ptime;
	
				// Process the message
				try{
					Thread.sleep(processTime);
				} catch(InterruptedException e){
					//e.printStackTrace();
				}
				//System.out.println("FogNode" + nodeId + "served message #" + requestSequenNum+ ", time = "+message.ptime);
				message.msg = message.msg + " Fog Node" + ":" + "SERVED";
				
				//Thread
				new Thread(new SendToIoTThread(this, message)).start();
			}
		}
	}
	
}
