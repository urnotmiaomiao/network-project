


import java.net.InetAddress;
import java.net.UnknownHostException;

public class Message{

	
	boolean isRequestMsg = false;	// 0

	int requestSequenNum = 0;
	String ipaddr = "";	
	int port = 0;	
	int ptime = 0;
	String msg = "";
	int forward_limit = 0;
	int node_id = 0;
	int queue_time = 0;
	
	public Message(String message) throws UnknownHostException {
		
		String[] message_list = message.split("-");
		if(Integer.parseInt(message_list[0])==0) {
			isRequestMsg = false;
			node_id = Integer.parseInt(message_list[1]);
			queue_time =  Integer.parseInt(message_list[2]);
		}else {
			isRequestMsg = true;
			requestSequenNum = Integer.parseInt(message_list[1]);
			forward_limit = Integer.parseInt(message_list[2]);
			ipaddr = message_list[3];
			port = Integer.parseInt(message_list[4]);
			ptime = Integer.parseInt(message_list[5]);

			msg = message_list[6];
		}
	}
}
