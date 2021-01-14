package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.List;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends BasicGameState {
	
	private RemoteSpace inbox;
	private RemoteSpace server;
	private RemoteSpace players;
	private List<Object[]> allPlayers;
	private boolean createPlayers = false;
	private String mainPlayer;
	private Player[] playersArr = new Player[10];
	private Client client;
	private boolean go = false; //false if multiplayer
	private static int playerCount = 0;
	public static final int ID = 1;
	Image background;
	
	final static int WIDTH = 1920;
	final static int HEIGHT = 1080;
	
	private Player player;
	private Path path;
	private Room room;
	
	private boolean collision = false;

	public Game(int playerCount, RemoteSpace inbox) {
		this.inbox = inbox;
		this.playerCount = playerCount;
	}

	@Override
	public void init(GameContainer arg0,  StateBasedGame sbg) throws SlickException {
		try {
			System.out.println(playerCount);
			mainPlayer = "player"+playerCount;
			//inbox = new RemoteSpace("tcp://127.0.0.1:9001/player" + playerCount + "?keep");
			server = new RemoteSpace("tcp://127.0.0.1:9001/server?keep");
			players = new RemoteSpace("tcp://127.0.0.1:9001/players?keep");
			
			createPlayer();
			
		} catch (IOException | InterruptedException e) { }
		
//		player1 = new Player(25, false);
//		player2 = new Player(25, true);
		path = new Path(Path.PATH_ONE_HORIZONTAL, Path.PATH_ONE_VERTICAL);
		room = new Room(player, path, Teleporter.PATH_ONE_TELEPORTERS);
		
	}

	private void createPlayer() throws InterruptedException {
		if(Math.random() >= 0.5) {
			server.put(mainPlayer, "bad guy", "not ready", "createPlayer");
			player = new Player(25,true);
			playersArr[playerCount] = player;
		} else {
			server.put(mainPlayer, "good guy", "not ready", "createPlayer");
			player = new Player(25,false);
			playersArr[playerCount] = player;
		}
		
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame sbg,  Graphics graphics) throws SlickException {
		
		graphics.setColor(Color.white);
		graphics.drawString("Collision: " + collision, 1700, 50);
		graphics.drawString("Player X: " + player.getX(), 1700, 70);
		graphics.drawString("Player Y: " + player.getY(), 1700, 90);
		
		
		if(createPlayers) {
			for (int i = 0; i < allPlayers.size(); i++) {
				graphics.setColor(playersArr[i+1].getColor());
				graphics.fill(playersArr[i+1].getShape());
			}
		}

		
		
		graphics.setColor(Color.orange);
		
		for (int i = 0; i < Teleporter.PATH_ONE_TELEPORTERS.length; i++) {
			graphics.fill(room.getTeleportElement(i));
		}
		
		graphics.setColor(player.getColor());
		graphics.fill(player.getShape());
		
		graphics.setColor(Color.white);
		
		for (int i = 0; i < path.getHorizontal().length; i++) {
			graphics.fill(path.getHorizontalElement(i));
		}
		
		for (int i = 0; i < path.getVertical().length; i++) {
			graphics.fill(path.getVerticalElement(i));
		}
		
	}

	@Override
	public void update(GameContainer con, StateBasedGame sbg,  int arg1) throws SlickException {
		Input input = con.getInput();
		
		
		
		if(go) { //Waits for all clients to synchronize
			
			try {
				updatePosition();
			} catch (InterruptedException e) { }
			
			if (input.isKeyDown(Input.KEY_W)) {
				player.setY(player.getY() - 5);
			}
			
			if (input.isKeyDown(Input.KEY_S)) {
				player.setY(player.getY() + 5);
			}
			
			if (input.isKeyDown(Input.KEY_A)) {
				player.setX(player.getX() - 5);
			}
			
			if (input.isKeyDown(Input.KEY_D)) {
				player.setX(player.getX() + 5);
			}
			
			for (int i = 0; i < Teleporter.PATH_ONE_TELEPORTERS.length; i++) {
				Teleporter teleporter = Teleporter.PATH_ONE_TELEPORTERS[i];
				if (player.getShape().intersects(teleporter.getShape())) {
					if (input.isKeyPressed(Input.KEY_E)) {
						if (i % 2 == 0) {
							player.setX(Teleporter.PATH_ONE_TELEPORTERS[i+1].getX() - player.getSize() / 2);
							player.setY(Teleporter.PATH_ONE_TELEPORTERS[i+1].getY() - player.getSize() / 2);
						} else {
							player.setX(Teleporter.PATH_ONE_TELEPORTERS[i-1].getX() - player.getSize() / 2);
							player.setY(Teleporter.PATH_ONE_TELEPORTERS[i-1].getY() - player.getSize() / 2);
						}
					}
				}
			}
			
			if (player.getX() < 0) { player.setX(0); }
			if (player.getX() >= WIDTH - player.getSize()) { player.setX(WIDTH - player.getSize()); }
			
			if (player.getY() < 0) { player.setY(0); }
			if (player.getY() >= HEIGHT - player.getSize()) { player.setY(HEIGHT - player.getSize()); }
			
			for (int i = 0; i < path.getHorizontal().length; i++) {
				// South bound
				if (i < 3) {
					if (player.getShape().intersects(path.getHorizontalElement(i))) {
						if(player.isEnemy()) {
							player.setY(player.getY() + 5);
						} else {
							player.setY(player.getY() - 5);
						}
					}
				// North bound
				} else {
					if (player.getShape().intersects(path.getHorizontalElement(i))) {
						if(player.isEnemy()) {
							player.setY(player.getY() - 5);
						} else {
							player.setY(player.getY() + 5);
						}
					}
				}	
			}
			
			for (int i = 0; i < path.getVertical().length; i++) {
				// West bound
				if (i < 3) {
					if (player.getShape().intersects(path.getVerticalElement(i))) {
						if(player.isEnemy()) {
							player.setX(player.getX() - 5);
						} else {
							player.setX(player.getX() + 5);
						}
					}
				// East bound
				} else {
					if (player.getShape().intersects(path.getVerticalElement(i))) {
						if(player.isEnemy()) {
							player.setX(player.getX() + 5);
						} else {
							player.setX(player.getX() - 5);
						}
					}
				}
			}
			
		} else {
			try {
				server.put(mainPlayer, "ready", "changeReady");
				inbox.get(new ActualField ("go"));
				getPlayers();
				createPlayers = true;
				go = true;
			} catch (InterruptedException e) { }
		
		}
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(2);
		}
	}
	
	//"player1", "good guy", "not ready"
	private void getPlayers() throws InterruptedException {
		allPlayers = players.queryAll(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
		
		for (int i = 0; i < allPlayers.size(); i++) {
			boolean role;
			if(allPlayers.get(i)[1].equals("good guy")) {
				role = false;
			} else {
				role = true;
			}
			
			if(allPlayers.get(i)[0].equals(mainPlayer)) {
				System.out.println("Skipped itself");
				continue; //Should not create itself
			} else {
				playersArr[i+1] = new Player(25, role);
				System.out.println("Created player"+(i+1));
			}
		}
	}

	public void updatePosition() throws InterruptedException  {
			server.put(player.getX(), player.getY(), mainPlayer);
			//System.out.println("Sent coordinates to server from " + mainPlayer);
			if(inbox.queryp(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class), new ActualField ("Pos")) != null) {
				Object[] t = inbox.get(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class), new ActualField ("Pos"));
				//Can never get position of itself from server
				if(t[2].equals("player1")) {
					playersArr[1].setX((float) t[0]);
					playersArr[1].setY((float) t[1]);
				} else if(t[2].equals("player2")) {
					playersArr[2].setX((float) t[0]);
					playersArr[2].setY((float) t[1]);
				} else if(t[2].equals("player3")) {
					playersArr[3].setX((float) t[0]);
					playersArr[3].setY((float) t[1]);
				} else if(t[2].equals("player4")) {
					playersArr[4].setX((float) t[0]);
					playersArr[4].setY((float) t[1]);
				}
			}
		}

	@Override
	public int getID() {
		return Game.ID;
	}

}
