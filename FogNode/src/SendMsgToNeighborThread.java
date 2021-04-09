


public class SendMsgToNeighborThread  implements Runnable{

	private FogNode fognode = null;
	private int nodeId = 0;
	private Message message = null;
	
	public SendMsgToNeighborThread(FogNode fognode, int nodeId, Message message) {
		this.fognode = fognode;
		this.nodeId = nodeId;
		this.message = message;
		
	}
	@Override
	public void run() {
		fognode.sendMsgToNeighbor(nodeId, message);
	}
}