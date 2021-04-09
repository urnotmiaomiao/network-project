package miao.cloud;

import java.io.*;
import java.net.*;
import java.util.stream.Collectors;

public class CloudAddQueue implements Runnable {

	private Socket socket = null;
	
	public CloudAddQueue(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try{
			BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
			boolean flag =true;  
			while(flag){  
			    String result =  buf.readLine(); 
				Message message = new Message(result);
				Main.cloudQueue.add(message);
			}  
//			
//			InputStream is = socket.getInputStream();
//			
//			String result = new BufferedReader(new InputStreamReader(is))
//			        .lines().collect(Collectors.joining(System.lineSeparator()));
//			
//			Message message = new Message(result);
//			Main.cloudQueue.add(message);
//			socket.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}

