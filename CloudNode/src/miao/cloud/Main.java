package miao.cloud;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Main {

	public static ConcurrentLinkedQueue<Message> cloudQueue = new ConcurrentLinkedQueue<Message>();
	
	public static void main(String[] args) throws IOException {
		CloudNode cloudnode = new CloudNode();
		CloudListening cl = new CloudListening();
		CloudProcess cp = new CloudProcess();
		new Thread(cl).start();
		CloudNode.cloudProcessMessage();
		//new Thread(cp).start();
    }
}

