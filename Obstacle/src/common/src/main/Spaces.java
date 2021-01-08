package common.src.main;

import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

public class Spaces {

	public static void main(String[] args) {
		SpaceRepository repo = new SpaceRepository();
		SequentialSpace client1 = new SequentialSpace();
		repo.add("client1", client1);
		repo.addGate("tcp://25.65.87.75:9001/?keep");
		
		SequentialSpace server = new SequentialSpace();
		repo.add("server", server);
		
		System.out.println("Created spaces");
		
		//new Thread(new Server()).start();
		
	}

}
