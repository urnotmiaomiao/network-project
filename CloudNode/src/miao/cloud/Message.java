package miao.cloud;

import java.net.UnknownHostException;

public class Message{

	String ipaddr;
	int port;
	String msg;
	int ptime;
	int sqno;
	
	public Message(String message) throws UnknownHostException {
		String[] message_list = message.split("-");
		ipaddr = message_list[0];
		port = Integer.parseInt(message_list[1]);
		msg = message_list[2];
		ptime = Integer.parseInt(message_list[3]);
		sqno = Integer.parseInt(message_list[4].trim());
	}
}
