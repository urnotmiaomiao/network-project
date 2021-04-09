public class ListenNeighborThread  implements Runnable{

	private FogNode fognode = null;
	
	public ListenNeighborThread(FogNode fognode) {
		this.fognode = fognode;
	}
	
	@Override
	public void run() {
		fognode.listenToNeighbor();
	}
}