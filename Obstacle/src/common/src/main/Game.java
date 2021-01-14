package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

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
	private Rectangle rec = null;
	private Rectangle rec2 = null;
	private String mainPlayer;
	private Client client;
	private boolean go = false; //false if multiplayer
	private static int playerCount = 0;
	public static final int ID = 1;
	Image background;
	
	final static int WIDTH = 1920;
	final static int HEIGHT = 1080;
	
	private Player player1, player2;
	private Path path;
	private Room room;
	
	String username = MainMenu.username;
	
	private boolean collision = false;

	public Game(int playerCount) {
		//this.inbox = inbox;
		this.playerCount = playerCount;
	}

	@Override
	public void init(GameContainer arg0,  StateBasedGame sbg) throws SlickException {
		try {
			System.out.println("init");
			System.out.println(playerCount);
			mainPlayer = "player"+playerCount;
			inbox = new RemoteSpace("tcp://127.0.0.1:9001/player" + playerCount + "?keep");
			server = new RemoteSpace("tcp://127.0.0.1:9001/server?keep");
			
		} catch (IOException e) { } 
		//rec = new Rectangle(100,300,25,25);
		//rec2 = new Rectangle(300,300,25,25);
		
		player1 = new Player(25, false);
		player2 = new Player(25, true);
		path = new Path(Path.PATH_ONE_HORIZONTAL, Path.PATH_ONE_VERTICAL);
		room = new Room(player1, path, Teleporter.PATH_ONE_TELEPORTERS);
		
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame sbg,  Graphics graphics) throws SlickException {
		
		graphics.setColor(Color.white);
		graphics.drawString("Collision: " + collision, 1700, 50);
		graphics.drawString("Player X: " + player1.getX(), 1700, 70);
		graphics.drawString("Player Y: " + player1.getY(), 1700, 90);
		
		
		graphics.setColor(Color.orange);
		
		for (int i = 0; i < Teleporter.PATH_ONE_TELEPORTERS.length; i++) {
			graphics.fill(room.getTeleportElement(i));
		}
		
		graphics.setColor(player1.getColor());
		graphics.fill(player1.getShape());
		
		graphics.drawString(MainMenu.username, (player1.getX() + player1.getSize()/2) - (container.getDefaultFont().getWidth(MainMenu.username)/2), player1.getY()-20);
		
		graphics.setColor(player2.getColor());
		graphics.fill(player2.getShape());
		

		
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
				player1.setY(player1.getY() - 5);
			}
			
			if (input.isKeyDown(Input.KEY_S)) {
				player1.setY(player1.getY() + 5);
			}
			
			if (input.isKeyDown(Input.KEY_A)) {
				player1.setX(player1.getX() - 5);
			}
			
			if (input.isKeyDown(Input.KEY_D)) {
				player1.setX(player1.getX() + 5);
			}
			
			for (int i = 0; i < Teleporter.PATH_ONE_TELEPORTERS.length; i++) {
				Teleporter teleporter = Teleporter.PATH_ONE_TELEPORTERS[i];
				if (player1.getShape().intersects(teleporter.getShape())) {
					if (input.isKeyPressed(Input.KEY_E)) {
						if (i % 2 == 0) {
							player1.setX(Teleporter.PATH_ONE_TELEPORTERS[i+1].getX() - player1.getSize() / 2);
							player1.setY(Teleporter.PATH_ONE_TELEPORTERS[i+1].getY() - player1.getSize() / 2);
						} else {
							player1.setX(Teleporter.PATH_ONE_TELEPORTERS[i-1].getX() - player1.getSize() / 2);
							player1.setY(Teleporter.PATH_ONE_TELEPORTERS[i-1].getY() - player1.getSize() / 2);
						}
					}
				}
			}
			
			if (player1.getX() < 0) { player1.setX(0); }
			if (player1.getX() >= WIDTH - player1.getSize()) { player1.setX(WIDTH - player1.getSize()); }
			
			if (player1.getY() < 0) { player1.setY(0); }
			if (player1.getY() >= HEIGHT - player1.getSize()) { player1.setY(HEIGHT - player1.getSize()); }
			
			for (int i = 0; i < path.getHorizontal().length; i++) {
				// South bound
				if (i < 3) {
					if (player1.getShape().intersects(path.getHorizontalElement(i))) {
						if(player1.isEnemy()) {
							player1.setY(player1.getY() + 5);
						} else {
							player1.setY(player1.getY() - 5);
						}
					}
				// North bound
				} else {
					if (player1.getShape().intersects(path.getHorizontalElement(i))) {
						if(player1.isEnemy()) {
							player1.setY(player1.getY() - 5);
						} else {
							player1.setY(player1.getY() + 5);
						}
					}
				}	
			}
			
			for (int i = 0; i < path.getVertical().length; i++) {
				// West bound
				if (i < 3) {
					if (player1.getShape().intersects(path.getVerticalElement(i))) {
						if(player1.isEnemy()) {
							player1.setX(player1.getX() - 5);
						} else {
							player1.setX(player1.getX() + 5);
						}
					}
				// East bound
				} else {
					if (player1.getShape().intersects(path.getVerticalElement(i))) {
						if(player1.isEnemy()) {
							player1.setX(player1.getX() + 5);
						} else {
							player1.setX(player1.getX() - 5);
						}
					}
				}
			}
			
		} else {
			try {
				server.put(true, mainPlayer + " ready");
				inbox.get(new ActualField ("go"));
				go = true;
			} catch (InterruptedException e) { }
		
		}
		
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(Client.PAUSE);
		}
	}
		
	public void updatePosition() throws InterruptedException  {
			server.put(player1.getX(), player1.getY(), mainPlayer);
			if(inbox.queryp(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class), new ActualField ("Pos")) != null) {
				Object[] t = inbox.get(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class), new ActualField ("Pos"));
				if(t[2].equals("player1")) {
					player2.setX((float) t[0]);
					player2.setY((float) t[1]);
				} else if(t[2].equals("player2")) {
					player2.setX((float) t[0]);
					player2.setY((float) t[1]);
				} else if(t[2].equals("player3")) {
					//rec3.setX((float) t[0]);
					//rec3.setY((float) t[1]);
					//osv
				}
			}
		}
	

	@Override
	public int getID() {
		return Game.ID;
	}

}
