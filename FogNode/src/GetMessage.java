import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.stream.Collectors;

public class GetMessage implements Runnable {

	private Socket socket = null;
	private FogNode fognode = null;
	
	public GetMessage(FogNode fognode, Socket socket) {
		this.fognode = fognode;
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
				fognode.handleMessage(fognode, message);
			}  
//			
//			InputStream is = socket.getInputStream();
//			
//			String result = new BufferedReader(new InputStreamReader(is))
//			        .lines().collect(Collectors.joining(System.lineSeparator()));
//			//System.out.println("result = "+result);
//			Message message = new Message(result);
//			fognode.handleMessage(message);
//			
//			socket.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}

