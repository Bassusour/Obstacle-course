package common.src.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

class Server {
	private static boolean sync = false; //false if multiplayer
	
	private static final String IP = "127.0.0.1:9001";
	
	//Main thread handles position of players
    public static void main(String[] args) {
    	try {
    		SpaceRepository repo = new SpaceRepository();
    		repo.addGate("tcp://" + IP + "/?keep");
    		
    		SequentialSpace players = new SequentialSpace();
    		repo.add("players", players);  		
    		
    		SequentialSpace server = new SequentialSpace();
    		repo.add("server", server);
    		
    		SequentialSpace ready = new SequentialSpace();
    		repo.add("ready", ready);
    		
    		SequentialSpace player1 = new SequentialSpace();
    		repo.add("player1", player1);
    		SequentialSpace player2 = new SequentialSpace();
    		repo.add("player2", player2);
    		SequentialSpace player3 = new SequentialSpace();
    		repo.add("player3", player3);
    		SequentialSpace player4 = new SequentialSpace();
    		repo.add("player4", player4);
    		SequentialSpace player5 = new SequentialSpace();
    		repo.add("player5", player5);
    		SequentialSpace player6 = new SequentialSpace();
    		repo.add("player6", player6);
;
    		
    		new Thread(new PlayerCountChecker(IP)).start();
    		new Thread(new PlayerRoles(IP)).start();
    		
    		SequentialSpace[] arrPlayersSpaces = {player1, player2, player3, player4, player5, player6};
			
			while(true) {
				
				if(sync) {
					Object[] t = server.get(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class));
					
					if(t[2].equals("player1")) {
						//System.out.println("Sent player1 pos");
						player2.put(t[0], t[1], "player1", "Pos");
						player3.put(t[0], t[1], "player1", "Pos");
						player4.put(t[0], t[1], "player1", "Pos");
					} else if(t[2].equals("player2")) {
						//System.out.println("Sent player2 pos");
						player1.put(t[0], t[1], "player2", "Pos");
						player3.put(t[0], t[1], "player2", "Pos");
						player4.put(t[0], t[1], "player2", "Pos");
					} else if(t[2].equals("player3")) {
						player1.put(t[0], t[1], "player3", "Pos");
						player2.put(t[0], t[1], "player3", "Pos");
						player4.put(t[0], t[1], "player3", "Pos");
					} else if(t[2].equals("player4")) {
						player1.put(t[0], t[1], "player4", "Pos");
						player2.put(t[0], t[1], "player4", "Pos");
						player3.put(t[0], t[1], "player4", "Pos");
					}
				} else {
					
					Object[] notReadyPlayer = ready.queryp(new FormalField(String.class), new ActualField("not ready"));
					//for(int i = 0; i < players.size(); i++)
					if(notReadyPlayer != null) {
						continue;
					} else if(notReadyPlayer == null && ready.size() >= 1) {
						for(int i = 0; i < players.size(); i++) {
							//sends go signal to all connected players
							arrPlayersSpaces[i].put("go");
						}
						sync = true;
					}
				}
				
				}
			
			
		} catch (InterruptedException e) { }
    }
}

//Handles player count
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
    		//player3 = new RemoteSpace("tcp://"+ip+"/player3?keep");
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
    			}
    			
    			//changes ready state of a client
    			if(server.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField("changeReady")) != null) {
    				Object[] input = server.get(new FormalField(String.class), new FormalField(String.class), new ActualField("changeReady"));
    				ready.get(new ActualField(input[0]), new FormalField(String.class));
    				if(input[1].equals("ready")) {
    					ready.put(input[0], "ready");
    				} else if(input[1].equals("not ready")) {
    					ready.put(input[0], "not ready");
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
