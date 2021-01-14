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
	
	//Main thread handles position of players
    public static void main(String[] args) {
    	try {
    		SpaceRepository repo = new SpaceRepository();
    		repo.addGate("tcp://25.56.25.201:9001/?keep");
    		
    		SequentialSpace players = new SequentialSpace();
    		repo.add("players", players);  		
    		
    		SequentialSpace server = new SequentialSpace();
    		repo.add("server", server);
    		
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
    		
    		new Thread(new PlayerCountChecker("25.56.25.201:9001")).start();
    		new Thread(new PlayerRoles("25.56.25.201:9001")).start();
    		
    		SequentialSpace[] arrPlayersSpaces = {player1, player2, player3, player4, player5, player6};
    		
    		System.out.println("Created spaces");
			
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
					
					Object[] notReadyPlayer = players.queryp(new FormalField(String.class),new FormalField(String.class), new ActualField("not ready"));
					//for(int i = 0; i < players.size(); i++)
					if(notReadyPlayer != null) {
						continue;
					} else if(notReadyPlayer == null && players.size() >= 1) {
						for(int i = 0; i <= players.size(); i++) {
							//sends go signal to all connected players
							System.out.println("Size is " + players.size());
							arrPlayersSpaces[i].put("go");
							System.out.println("Sent go to player" + (i+1));
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
    		players = new RemoteSpace("tcp://"+ip+"/players?keep");
    		player1 = new RemoteSpace("tcp://"+ip+"/player1?keep");
    		player2 = new RemoteSpace("tcp://"+ip+"/player2?keep");
    		player3 = new RemoteSpace("tcp://"+ip+"/player3?keep");
    		player4 = new RemoteSpace("tcp://"+ip+"/player4?keep");
    	} catch (IOException e) { } 
		
    	while(true) {
    		try {
    			//gets players and puts them into 'players' space
    			if(server.queryp(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class), new ActualField("createPlayer")) != null) {
    				Object[] t = server.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class), new ActualField("createPlayer"));
        			//If already one bad guy (will do later)
        			/*if(players.query(new FormalField(String.class), new FormalField(String.class), new ActualField("bad guy")) != null) { 
        				
        			} else {
        				
        			}*/
        			//player1, good guy, not ready
        			players.put(t[0], t[1], t[2]);
        			System.out.println("put " + t[0]);
    			}
    			
    			//changes ready state of a client
    			if(server.queryp(new FormalField(String.class), new FormalField(String.class), new ActualField("changeReady")) != null) {
    				System.out.println("Changing ready");
    				Object[] input = server.get(new FormalField(String.class), new FormalField(String.class), new ActualField("changeReady"));
    				System.out.println("Got input");
    				Object[] selectedPlayer = players.get(new ActualField(input[0]), new FormalField(String.class), new FormalField(String.class));
    				System.out.println("Got player");
    				if(input[1].equals("ready")) {
    					players.put(input[0], selectedPlayer[1], "ready");
    					System.out.println("Updated " + input[0] + " to ready");
    				} else if(input[1].equals("not ready")) {
    					players.put(input[0], selectedPlayer[1], "not ready");
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
