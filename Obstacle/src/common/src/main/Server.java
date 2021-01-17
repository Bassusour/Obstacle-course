package common.src.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

class Server {
	public static final String IP = "127.0.0.1:9001";
	public static HashMap<String, SequentialSpace> players = new HashMap<String, SequentialSpace>();
	
	//Main thread creates spaces and starts other threads
    public static void main(String[] args) {
    	
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

		new Thread(new PlayerController()).start();
		new Thread(new ReadyController()).start();
		new Thread(new PositionController()).start();
		
		System.out.println("Created spaces");

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
			
//			System.out.println("Server player list :" + players.toString());
			
			try {
				//("username", "create player")
				Object[] create = playerButler.getp(new FormalField(String.class), new ActualField("create player"));
				
				if(create != null) {
					SequentialSpace playerSpace = new SequentialSpace();
					String username = create[0].toString();
					if (!players.containsKey(username)) {
						players.put(username, playerSpace);
						//playerSpace.put("role", "good guy");
						
						ready.put(username, "not ready");
						playerButler.put("successfully created", "response to client");
					} else {

						System.out.println("player already exists");
						playerButler.put("player already exists", "response to client");
					}
				}
				
			} catch (InterruptedException e) {}
			
			
			try {
				Object[] remove = playerButler.getp(new FormalField(String.class), new ActualField("remove player"));
				
				if (remove != null) {
					players.remove(remove[0]);
					ready.get(new ActualField(remove[0]), new FormalField(String.class));
					ready.getp(new ActualField("all ready"));
					for (Map.Entry<String, SequentialSpace> entry : players.entrySet()) {
						if(!entry.getKey().equals(remove[0])) {
							playerButler.put(entry.getKey(), "remove other player", remove[0]);
						}
					}
				}

				
			} catch (InterruptedException e) {}
		}		
	}
}
	
class ReadyController implements Runnable {

	RemoteSpace ready;
	boolean allReady = false;
	
	
	@Override
	public void run() {
		
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
			} catch (InterruptedException e1) {}
			allReady = checkReady(readyList);
			
			 //assigns a random player to enemy
			 try {
				if (allReady && ready.queryp(new ActualField("all ready")) == null) {
					Random r = new Random();
					int rn = r.nextInt(readyList.size());
					ready.put("all ready", rn);
				 }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
