package common.src.main;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

class Server {
	private static boolean sync = true; //false if multiplayer
	private int playerCount = 0;
	
    public static void main(String[] args) {
    	try {
    		new Thread(new PlayerCountChecker("127.0.0.1:9001")).start();
    		SpaceRepository repo = new SpaceRepository();
    		SequentialSpace player1 = new SequentialSpace();
    		repo.add("player1", player1);
    		repo.addGate("tcp://127.0.0.1:9001/?keep");
    		
    		SequentialSpace server = new SequentialSpace();
    		repo.add("server", server);
    		
    		SequentialSpace player2 = new SequentialSpace();
    		repo.add("player2", player2);
    		
    		System.out.println("Created spaces");
			
			while(true) {
				if(sync) {
					Object[] t = server.get(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class));
					//Broadcasts the position to all players, except itself
					if(t[2].equals("player1")) {
						player2.put(t[0], t[1], "player1", "Pos");
					} else if(t[2].equals("player2")) {
						player1.put(t[0], t[1], "player2", "Pos");
					}
				} else {//setup (waits for all clients to be ready)
					server.get(new FormalField(Boolean.class), new ActualField("player1 ready"));
					server.get(new FormalField(Boolean.class), new ActualField("player2 ready"));
					Thread.sleep(500);
					player1.put("go");
					player2.put("go");
					sync = true;
				}
				
				}
			
			
		} catch (InterruptedException e) { }
    }
}

class PlayerCountChecker implements Runnable {
	RemoteSpace server;
	RemoteSpace player1;
	RemoteSpace player2;
	RemoteSpace player3;
	private int playerCount = 1;
	private String ip;
	
	public PlayerCountChecker(String ip) {
		this.ip = ip;
	}
	
    public void run() {
    	try {
    		server = new RemoteSpace("tcp://"+ip+"/server?keep");
    		player1 = new RemoteSpace("tcp://"+ip+"/player1?keep");
    		player2 = new RemoteSpace("tcp://"+ip+"/player2?keep");
    		player3 = new RemoteSpace("tcp://"+ip+"/player3?keep");
    	} catch (IOException e) { } 
    	
		
    	while(true) {
    		try {
    			server.get(new ActualField("getPlayerCount"));
    			server.put(playerCount, "playerCount");
    			playerCount++;
    		} catch (InterruptedException e) { } 
    	}
    }
}
