package common.src.main;

import java.io.IOException;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;

public class Client implements Runnable {
	String username;
	
	public Client (String username) {
        this.username = username;
    }
	
	@Override
	public void run() {
		try {
			RemoteSpace inbox = new RemoteSpace("tcp://25.56.25.201:9001/client1?keep");
			RemoteSpace server = new RemoteSpace("tcp://25.56.25.201:9001/server?keep");
			
			System.out.println("Connected from client");
			
			server.put("test from client");
			Object[] t = inbox.get(new FormalField(String.class));
			System.out.println(t[0]);
		} catch (IOException | InterruptedException e) { }
		
	}
	
}
