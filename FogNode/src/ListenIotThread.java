import java.io.IOException;

public class ListenIotThread  implements Runnable{

	private FogNode fognode = null;
	public ListenIotThread(FogNode fognode) {
		this.fognode = fognode;
	}
	@Override
	public void run() {
		try {
			fognode.listenToIoT(fognode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
