


public class SendStateThread implements Runnable{

	private FogNode fognode = null;
	private int interval;
	public SendStateThread(FogNode fognode, int num) {
		this.fognode = fognode;
		interval = num;
	}
	
	@Override
	public void run() {
		while(true) {
			fognode.sendStateToNeighbor();
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
	}

}
