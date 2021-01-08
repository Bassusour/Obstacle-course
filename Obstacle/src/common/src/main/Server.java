package common.src.main;

import java.io.IOException;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

class Server implements Runnable {
	
    @Override
    public void run() {
    	try {
			RemoteSpace server = new RemoteSpace("tcp://25.56.25.201:9001/server?keep");
			RemoteSpace client1 = new RemoteSpace("tcp://25.56.25.201:9001/client1?keep");
			
			System.out.println("Connected from server");
			
			client1.put("test from server");
			
			Object[] t = server.get(new FormalField(String.class));
			System.out.println(t[0]);
			
		} catch (IOException | InterruptedException e) { }
    }
}
