package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Triangulator;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Game extends BasicGameState {
	
	private RemoteSpace inbox;
	private RemoteSpace server;
	private RemoteSpace ready;

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
	private long time;
	private long teleporterCooldown = 0;
	
	private boolean button1 = false;
	
	String username = MainMenu.username;
	
	private boolean collision = false;

	public Game(int playerCount, RemoteSpace inbox) {
		this.inbox = inbox;
		this.playerCount = playerCount;
	}

	@Override
	public void init(GameContainer arg0,  StateBasedGame sbg) throws SlickException {
		try {
			mainPlayer = "player"+playerCount;
			//inbox = new RemoteSpace("tcp://127.0.0.1:9001/player" + playerCount + "?keep");
			server = new RemoteSpace("tcp://" + Client.IP + "/server?keep");
			players = new RemoteSpace("tcp://" + Client.IP + "/players?keep");
			ready = new RemoteSpace("tcp://" + Client.IP + "/ready?keep");
			
			createPlayer();
			
		} catch (IOException | InterruptedException e) { }
		path = new Path(Path.PATH_ONE_HORIZONTAL, Path.PATH_ONE_VERTICAL);
		room = new Room(player, path, Teleporter.PATH_ONE_TELEPORTERS, Button.PATH_ONE_BUTTONS);
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame sbg,  Graphics graphics) throws SlickException {
		
		graphics.setBackground(Color.decode("#F7DFD3"));
		
		// PATH_ONE HORIZONTAL
		graphics.setColor(Color.decode("#355C7D"));
		graphics.fill(new Rectangle(0, 100, 400, 100));
		graphics.fill(new Rectangle(100, 300, 400, 100));
		graphics.fill(new Rectangle(0, 500, 600, 100));	
		graphics.fill(new Rectangle(0, 700, 300, 100));	
		graphics.fill(new Rectangle(400, 700, 700, 100));
		graphics.fill(new Rectangle(0, 900, 1820, 100));
		graphics.fill(new Rectangle(1400, 700, 420, 100));
		graphics.fill(new Rectangle(900, 500, 920, 100));
		graphics.fill(new Rectangle(1500, 300, 100, 100));
		graphics.fill(new Rectangle(1500, 100, 420, 100));
		
		// PATH_ONE VERTICAL
		graphics.fill(new Rectangle(500, 0, 100, 400));
		graphics.fill(new Rectangle(700, 0, 100, 700));
		graphics.fill(new Rectangle(900, 100, 100, 400));
		graphics.fill(new Rectangle(1100, 0, 100, 400));
		graphics.fill(new Rectangle(1300, 100, 100, 400));
		graphics.fill(new Rectangle(1720, 200, 100, 200));
		graphics.fill(new Rectangle(1720, 600, 100, 100));
		graphics.fill(new Rectangle(1720, 800, 100, 100));
		graphics.fill(new Rectangle(1200, 700, 100, 200));
		
		for (int i = 0; i < Button.PATH_ONE_BUTTONS.length; i++) {
			graphics.setColor(Color.red);
			graphics.fill(room.getButtonElement(i));
		}
		
		graphics.setColor(Color.orange);
		
		for (int i = 0; i < Teleporter.PATH_ONE_TELEPORTERS.length; i++) {
			graphics.fill(room.getTeleportElement(i));
		}
		

		graphics.drawString(MainMenu.username, (player.getX() + player.getSize()/2) - (container.getDefaultFont().getWidth(MainMenu.username)/2), player.getY()-20);
		//graphics.setColor(player.getColor());
		//graphics.fill(player.getShape());

		graphics.setColor(Color.white);
		
		for (int i = 0; i < path.getHorizontal().length; i++) {
			graphics.fill(path.getHorizontalElement(i));
		}
		
		for (int i = 0; i < path.getVertical().length; i++) {
			graphics.fill(path.getVerticalElement(i));
		}
		
		if(createPlayers) {
			for (int i = 0; i < allPlayers.size(); i++) {
				//System.out.println("null at player " + (i+1));
				graphics.setColor(playersArr[i+1].getColor());
				graphics.fill(playersArr[i+1].getShape());
			}
		}
		
		for (int i = 0; i < Button.PATH_ONE_BUTTONS.length; i++) {
			
			Button button = Button.PATH_ONE_BUTTONS[i];
			
			if (button.isPressed() || button.inUse()) {
				
				if (i < 5) {
					Trap.setBombs(graphics, time, button);
				} else if (i < 8){
					Trap.setBullets(graphics, time, button);
				} else {
					Trap.setSuperbombs(graphics, time, button);
				}
				
				button.unpressed();
				
			}
		}
	
		
		
	}

	@Override
	public void update(GameContainer con, StateBasedGame sbg,  int arg1) throws SlickException {
		
		time = System.currentTimeMillis();
		
		Input input = con.getInput();
		
		int speed;
		
		if (player.isEnemy()) {
			speed = 10;
		} else {
			speed = 5;
		}
		
		if(go) { //Waits for all clients to synchronize
			
			try {
				updatePosition();
			} catch (InterruptedException e) { }
			
			if (input.isKeyDown(Input.KEY_W)) {
				player.setY(player.getY() - speed);
			}
			
			if (input.isKeyDown(Input.KEY_S)) {
				player.setY(player.getY() + speed);
			}
			
			if (input.isKeyDown(Input.KEY_A)) {
				player.setX(player.getX() - speed);
			}
			
			if (input.isKeyDown(Input.KEY_D)) {
				player.setX(player.getX() + speed);
			}
			
			for (int i = 0; i < Button.PATH_ONE_BUTTONS.length; i++) {
				Button button = Button.PATH_ONE_BUTTONS[i];
				if (player.getShape().intersects(button.getShape())) {
					if (time > button.getCooldown()) {
						if (input.isKeyDown(Input.KEY_E)) {
							button.pressed();
							button.setCooldown(time + 10000);
						}
					}
				}
			}
			
			if (time > teleporterCooldown) {
				for (int i = 0; i < Teleporter.PATH_ONE_TELEPORTERS.length; i++) {
					Teleporter teleporter = Teleporter.PATH_ONE_TELEPORTERS[i];
					if (player.getShape().intersects(teleporter.getShape())) {
						if (input.isKeyDown(Input.KEY_E)) {
							if (i % 2 == 0) {
								player.setX(Teleporter.PATH_ONE_TELEPORTERS[i+1].getX() - player.getSize() / 2);
								player.setY(Teleporter.PATH_ONE_TELEPORTERS[i+1].getY() - player.getSize() / 2);
								teleporterCooldown = time + (1 * 1000);
								break;
							} else {
								player.setX(Teleporter.PATH_ONE_TELEPORTERS[i-1].getX() - player.getSize() / 2);
								player.setY(Teleporter.PATH_ONE_TELEPORTERS[i-1].getY() - player.getSize() / 2);
								teleporterCooldown = time + (1 * 1000);
								break;
							}
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
				if (i < 16) {
					if (player.getShape().intersects(path.getHorizontalElement(i))) {
						if(player.isEnemy()) {
							player.setY(player.getY() + speed);
						} else {
							player.setY(player.getY() - speed);
						}
					}
				// North bound
				} else {
					if (player.getShape().intersects(path.getHorizontalElement(i))) {
						if(player.isEnemy()) {
							player.setY(player.getY() - speed);
						} else {
							player.setY(player.getY() + speed);
						}
					}
				}	
			}
			
			for (int i = 0; i < path.getVertical().length; i++) {
				// West bound
				if (i < 13) {
					if (player.getShape().intersects(path.getVerticalElement(i))) {
						if(player.isEnemy()) {
							player.setX(player.getX() - speed);
						} else {
							player.setX(player.getX() + speed);
						}
					}
				// East bound
				} else {
					if (player.getShape().intersects(path.getVerticalElement(i))) {
						if(player.isEnemy()) {
							player.setX(player.getX() + speed);
						} else {
							player.setX(player.getX() - speed);
						}
					}
				}
			}
			
		} else {
			try {
				server.put(mainPlayer, "ready", "changeReady");
				inbox.get(new ActualField ("go"));
				getPlayers();
				go = true;
				createPlayers = true;
			} catch (InterruptedException e) { }
		}
		
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(Client.PAUSE);
		}
	}
	
	//"player1", "good guy", "not ready"
	private void getPlayers() throws InterruptedException {
		allPlayers = players.queryAll(new FormalField(String.class), new FormalField(String.class));
		
		for (int i = 0; i < allPlayers.size(); i++) {
			boolean role = true;
			if(allPlayers.get(i)[1].equals("good guy")) {
				role = false;
			} else {
				role = true;
			}
			
			/*
			 * "player1", "bad guy"
			 * "player2", "bad guy"
			 */
			
			if(allPlayers.get(i)[0].equals(mainPlayer)) {
				continue; //Should not create itself
			} else {
				playersArr[i+1] = new Player(25, role);
			}
		}
	}
	private void createPlayer() throws InterruptedException {
		/*
		if(Math.random() >= 0.5) {
			server.put(mainPlayer, "bad guy", "createPlayer");
			ready.put(mainPlayer, "not ready");
			player = new Player(25,true);
			playersArr[playerCount] = player;
			System.out.println("Put player"+playerCount+" in array index " + (playerCount));
		} else {
			server.put(mainPlayer, "good guy", "createPlayer");
			ready.put(mainPlayer, "not ready");
			player = new Player(25,false);
			playersArr[playerCount] = player;
			System.out.println("Put player"+playerCount+" in array index " + (playerCount));
		}
		*/
		server.put(mainPlayer, "bad guy", "createPlayer");
		ready.put(mainPlayer, "not ready");
		player = new Player(25,true);
		playersArr[playerCount] = player;
		
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
