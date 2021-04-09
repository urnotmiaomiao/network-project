package miao.cloud;

import java.io.IOException;

public class CloudListening implements Runnable{

	@Override
	public void run() {
		try {
			CloudNode.listenSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
