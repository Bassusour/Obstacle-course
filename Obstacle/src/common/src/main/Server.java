package common.src.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

class Server {
	private static boolean sync = false; //false if multiplayer
	
	public static final String IP = "127.0.0.1:9001";
	public static HashMap<String, SequentialSpace> players;
	
	//Main thread handles position of players
    public static void main(String[] args) {
    	players = new HashMap<String, SequentialSpace>();
    	
//    	players.put("init", new SequentialSpace());
    	
    	SpaceRepository repo = new SpaceRepository();
		repo.addGate("tcp://" + IP + "/?keep");  		
		
		SequentialSpace server = new SequentialSpace();
		repo.add("server", server);
		
		SequentialSpace ready = new SequentialSpace();
		repo.add("ready", ready);
		
		SequentialSpace playerButler = new SequentialSpace();
		repo.add("playerButler", playerButler);
		
		SequentialSpace positionButler = new SequentialSpace();
		repo.add("positionButler", positionButler);
		
		
		
//    		new Thread(new PlayerCountChecker(IP)).start();
//		new Thread(new PlayerRoles(IP)).start();
		new Thread(new PlayerController()).start();
		new Thread(new ReadyController()).start();
		new Thread(new PositionController()).start();
		
		System.out.println("Created spaces");
		
		while(true) {
			
			if(sync) {
				
				
			} else {
				
				
			}
			
			}
    }
}

class PlayerController implements Runnable {
	
	RemoteSpace playerButler;
	RemoteSpace ready;
	Map<String, SequentialSpace> players;
	
	public PlayerController() {
		
	}

	@Override
	public void run() {
		
		try {
			playerButler = new RemoteSpace("tcp://" + Server.IP + "/playerButler?keep");
			ready = new RemoteSpace("tcp://" + Server.IP + "/ready?keep");
			players = Server.players;
		} catch (IOException e) {}
		
		while(true) {
			
			try {
				Object[] create = playerButler.getp(new FormalField(String.class), new ActualField("create player"));
				
				if(create != null) {
					if (!players.containsKey(create[0])) {
						players.put(create[0].toString(), new SequentialSpace());
						players.get(create[0]).put("role", "good guy");
						ready.put(create[0], "not ready");
					} else {
						playerButler.put(create[0], "user exists");
					}
				}
				
			} catch (InterruptedException e) {}
			
			
			try {
				Object[] remove = playerButler.getp(new FormalField(String.class), new ActualField("remove player"));
				
				if (remove != null) {
					players.remove(remove[0]);
				}

				
			} catch (InterruptedException e) {}
		}		
	}
}
	
class ReadyController implements Runnable {

	RemoteSpace ready;
	Map<String, SequentialSpace> players;
	boolean allReady = false;
	
	
	@Override
	public void run() {
		
		players = Server.players;
		try {
			ready = new RemoteSpace("tcp://" + Server.IP + "/ready?keep");
		} catch (IOException e) {}
		
		while(!false) {
			try {
				Object[] getChange = ready.getp(new FormalField(String.class), new ActualField("change ready"), new FormalField(String.class));
				if(getChange != null) {
					Object[] getReady = ready.get(new ActualField(getChange[0]), new FormalField(String.class));
					if (getReady[1].equals("not ready")) {
						ready.put(getReady[0], "ready");
					} else if (getReady[1].equals("ready")) {
						ready.put(getReady[0], "not ready");
					}
						
				}
			} catch (InterruptedException e) {}
			
			List<Object[]> readyList = null;
			
			try {
				readyList = ready.queryAll(new FormalField(String.class), new FormalField(String.class));
//				System.out.println("Ready List : " + readyList.toString());
			} catch (InterruptedException e1) {}
			allReady = checkReady(readyList);
			
			 
			 if (allReady) {
				 try {
					ready.put("all ready");
				} catch (InterruptedException e) {}
			 }
		}
		
	}
	
	public boolean checkReady(List<Object[]> readyList) {
		
		int count = 0;
		
		if(readyList.size() == 0) {
			return false;
		}
		
		for(int i = 0; i < readyList.size(); i++) {
			if (readyList.get(i)[1].equals("ready")) {
				count++;
			}
			
		}
		
		if(count == readyList.size()) {
			return true;
		} else {
			return false;
		}
		
	}
}

class PositionController implements Runnable {
	
	RemoteSpace positionButler;
	Map<String, SequentialSpace> players;

	@Override
	public void run() {
		
		players = Server.players;
		
		try {
			positionButler = new RemoteSpace("tcp://" + Server.IP + "/positionButler?keep");
		} catch (IOException e) {}
		
		while(true) {
			try {
				if (positionButler.queryp(new FormalField(String.class), new FormalField(Float.class), new FormalField(Float.class), new ActualField("give position")) != null) {
					Object[] pos = positionButler.get(new FormalField(String.class), new FormalField(Float.class), new FormalField(Float.class), new ActualField("give position"));

					for (Map.Entry<String, SequentialSpace> entry : players.entrySet()) {
						if (!entry.getKey().equals(pos[0])) {
							entry.getValue().put("positionQueue", pos[0], pos[1], pos[2]);
						}
					}
				}
				
				for (Map.Entry<String, SequentialSpace> entry : players.entrySet()) {
					if (entry.getValue().queryp(new ActualField("positionQueue"), new FormalField(String.class), new FormalField(Float.class), new FormalField(Float.class)) != null) {
						Object[] pos = entry.getValue().get(new ActualField("positionQueue"), new FormalField(String.class), new FormalField(Float.class), new FormalField(Float.class));
						positionButler.put(entry.getKey(), pos[1], pos[2], pos[3]);
					}
				}
			} catch (InterruptedException e) {}
		}
		
	}
	
}
	


//Handles player count
//class PlayerCountChecker implements Runnable {
//	RemoteSpace server;
//	RemoteSpace player1;
//	RemoteSpace player2;
//	RemoteSpace player3;
//	private int playerCount = 1;
//	private String ip;
//	
//	public PlayerCountChecker(String ip) {
//		this.ip = ip;
//	}
//	
//    public void run() {
//    	try {
//    		server = new RemoteSpace("tcp://"+ip+"/server?keep");
//    		player1 = new RemoteSpace("tcp://"+ip+"/player1?keep");
//    		player2 = new RemoteSpace("tcp://"+ip+"/player2?keep");
//    		//player3 = new RemoteSpace("tcp://"+ip+"/player3?keep");
//    	} catch (IOException e) { } 
//    	
//		
//    	while(true) {
//    		try {
//    			server.get(new ActualField("getPlayerCount"));
//    			server.put(playerCount, "playerCount");
//    			playerCount++;
//    		} catch (InterruptedException e) { } 
//    	}
//    }
//}

//Handles roles of each player
class PlayerRoles implements Runnable {
	RemoteSpace server;
	RemoteSpace ready;
	RemoteSpace players;
	RemoteSpace player1;
	RemoteSpace player2;
	RemoteSpace player3;
	RemoteSpace player4;
	private String ip;
	
	public PlayerRoles(String ip) {
		this.ip = ip;
	}
	
    public void run() {
    	try {
    		server = new RemoteSpace("tcp://"+ip+"/server?keep");
    		ready = new RemoteSpace("tcp://"+ip+"/ready?keep");
    		players = new RemoteSpace("tcp://"+ip+"/players?keep");
    		player1 = new RemoteSpace("tcp://"+ip+"/player1?keep");
    		player2 = new RemoteSpace("tcp://"+ip+"/player2?keep");
    		player3 = new RemoteSpace("tcp://"+ip+"/player3?keep");
    		player4 = new RemoteSpace("tcp://"+ip+"/player4?keep");
    	} catch (IOException e) { } 
		
    	while(true) {
    		try {
    			//gets players and puts them into 'players' space
    			if(server.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField("createPlayer")) != null) {
    				Object[] t = server.get(new FormalField(String.class), new FormalField(String.class), new ActualField("createPlayer"));
        			//If already one bad guy (will do later)
        			/*if(players.query(new FormalField(String.class), new FormalField(String.class), new ActualField("bad guy")) != null) { 
        				
        			} else {
        				
        			}*/
        			//player1, good guy, not ready
        			players.put(t[0], t[1]);
        			System.out.println("put " + t[0]);
    			}
    			
    			//changes ready state of a client
    			if(server.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField("changeReady")) != null) {
    				System.out.println("Changing ready");
    				Object[] input = server.get(new FormalField(String.class), new FormalField(String.class), new ActualField("changeReady"));
    				System.out.println("Got input");
    				ready.get(new ActualField(input[0]), new FormalField(String.class));
    				System.out.println("Removed old ready");
    				if(input[1].equals("ready")) {
    					ready.put(input[0], "ready");
    					System.out.println("Updated " + input[0] + " to ready");
    				} else if(input[1].equals("not ready")) {
    					ready.put(input[0], "not ready");
    					System.out.println("Updated " + input[0] + " to not ready");
    				}
    			}
    			
    			/*if(server.queryp(new FormalField(String.class), new ActualField("getPlayers")) != null) {
    				Object[] t = server.get(new FormalField(String.class), new ActualField("getPlayers"));
    				Object[] playerNames = new Object[10];
    				List<Object[]> allPlayers = players.queryAll(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
    				
    				for(int i = 0; i < players.size(); i++) {
    					playerNames[i] = allPlayers.get(i)[0];
    				}
    				
        			if(t[0].equals("player1")) {
        				player1.put(playerNames);
        			} else if(t[0].equals("player2")) {
        				player2.put(playerNames);
        			} else if(t[0].equals("player3")) {
        				player3.put(playerNames);
        			} else if(t[0].equals("player4")) {
        				player4.put(playerNames);
        			}
    			}*/
    			
    			
    		} catch (InterruptedException e) { } 
    	}
    }
}
