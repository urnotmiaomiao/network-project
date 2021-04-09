package miao.cloud;

import java.io.IOException;

public class CloudProcess implements Runnable{

	@Override
	public void run() {
		try {
			CloudNode.cloudProcessMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
