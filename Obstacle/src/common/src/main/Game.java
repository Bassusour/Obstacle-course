package common.src.main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	private RemoteSpace positionButler;
	RemoteSpace playerButler;
	RemoteSpace trapButler;

	ArrayList<Player> playerList;
	public static final int ID = 1;
	Image background;
	
	final static int WIDTH = 1920;
	final static int HEIGHT = 1080;
	
	private boolean triggerTrap = false;
	private int buttonID = -1;
	private String trapType = "";
	
	private Player player;
	private Path path;
	private Room room;
	private long time;
	private Goal goal = new Goal();
	private long teleporterCooldown = 0;
	
	private boolean button1 = false;
	
	String username = MainMenu.username;
	
	public static boolean createdPlayer = false;

	@Override
	public void init(GameContainer gc,  StateBasedGame sbg) throws SlickException {
		gc.setAlwaysRender(true);
		try {
			positionButler = new RemoteSpace("tcp://" + Client.IP + "/positionButler?keep");
			playerButler = new RemoteSpace("tcp://" + Client.IP + "/playerButler?keep");	
			trapButler = new RemoteSpace("tcp://" + Client.IP + "/trapButler?keep");	
		} catch (IOException e) { }
		playerList = Client.playerList;
		
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
		
		for (int i = 0; i < goal.getPattern().length; i++) {
			
			if (i < 8 || i > 15) {
				
				if (i % 2 == 0) {
					graphics.setColor(Color.black);
				} else {
					graphics.setColor(Color.decode("#F7DFD3"));
				}
				
			} else {
				
				if (i % 2 == 0) {
					graphics.setColor(Color.decode("#F7DFD3"));
				} else {
					graphics.setColor(Color.black);
				}
				
			}
			
			graphics.fill(goal.getPattern()[i]);
		}
		
		for (int i = 0; i < Button.PATH_ONE_BUTTONS.length; i++) {
			graphics.setColor(Color.red);
			graphics.fill(room.getButtonElement(i));
		}
		
		graphics.setColor(Color.orange);
		
		for (int i = 0; i < Teleporter.PATH_ONE_TELEPORTERS.length; i++) {
			graphics.fill(room.getTeleportElement(i));
		}
		
		graphics.setColor(Color.white);
		
		for (int i = 0; i < path.getHorizontal().length; i++) {
			graphics.fill(path.getHorizontalElement(i));
		}
		
		for (int i = 0; i < path.getVertical().length; i++) {
			graphics.fill(path.getVerticalElement(i));
		}
		
		drawPlayers(graphics, container);
		
		for (int i = 0; i < Button.PATH_ONE_BUTTONS.length; i++) {
			
			Button button = Button.PATH_ONE_BUTTONS[i];
			
			if (button.isPressed() || button.inUse()) {
				
				if (i < 5) {
					try {
						if (button.isPressed()) { trapButler.put(MainMenu.username, "trap triggered", "bombs", button.getID()); }
					} catch (InterruptedException e) {}
					Trap.setBombs(graphics, time, button);
				} else if (i < 8){
					try {
						if (button.isPressed()) { trapButler.put(MainMenu.username, "trap triggered", "bullets", button.getID()); }
					} catch (InterruptedException e) {}
					Trap.setBullets(graphics, time, button);
				} else {
					try {
						if (button.isPressed()) { trapButler.put(MainMenu.username, "trap triggered", "super bombs", button.getID()); }
					} catch (InterruptedException e) {}
					Trap.setSuperbombs(graphics, time, button);
				}
				
//				button.unpressed();
				
			}
			
		}
		
		
		if (triggerTrap) {
			Button button = null;
			for (Button butt : Button.PATH_ONE_BUTTONS) {
				if (butt.getID() == buttonID) {
					button = butt;
					if (!(button.getCooldown() > System.currentTimeMillis())) {
						button.pressed();
						button.setCooldown(time + 10000);
					}
					if (trapType.equals("bombs")) {
						Trap.setBombs(graphics, time, button);
					} else if (trapType.equals("super bombs")) {
						Trap.setSuperbombs(graphics, time, button);
					} else {
						Trap.setBullets(graphics, time, button);
					}
				}
			}
			if (!button.inUse()) {
				triggerTrap = false;
			}
			
		}	
	}

	@Override
	public void update(GameContainer con, StateBasedGame sbg,  int arg1) throws SlickException {
		
		time = System.currentTimeMillis();
		
		Input input = con.getInput();
		
		int speed;

		if(!createdPlayer) {
			for (Player player : playerList) {
				if (player.getUsername().equals(MainMenu.username)) {
					this.player = player;
				}
			}
			createdPlayer = true;
		}


		if (player.isEnemy()) {
			speed = 7;
		} else {
			speed = 5;
		}
		
		
		try {
			if (playerButler.queryp(new ActualField(MainMenu.username), new ActualField("remove other player"), new FormalField(String.class)) != null) {
				Object[] remove = playerButler.get(new ActualField(MainMenu.username), new ActualField("remove other player"), new FormalField(String.class));
				int index = -1;
				for (int i = 0; i < playerList.size(); i++) {
					if (playerList.get(i).getUsername().equals(remove[2])) {
						index = i;
					}
				}
				if (index != -1) {
					playerList.remove(index);
				}
			}
		} catch (InterruptedException e1) {}
		
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
			
			if (player.getX() < goal.getX() && player.getY() > goal.getY()) {
				System.out.println("Black Wins");
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
			
			try {
				Object[] trap = trapButler.getp(new ActualField(MainMenu.username), new ActualField("trigger trap"), new FormalField(String.class), new FormalField(Integer.class));
				if ( trap != null ) {
					buttonID = (int) trap[3];
					trapType = trap[2].toString();
					triggerTrap = true;
				}
			} catch (InterruptedException e) {}
			
		
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(Client.PAUSE);
		}
		

	}
	
	private void drawPlayers(Graphics g, GameContainer container) {
		

		for (Player player : playerList) {
			g.setColor(player.getColor());
			g.fill(player.getShape());
			g.drawString(player.getUsername(), (player.getX() + player.getSize()/2) - (container.getDefaultFont().getWidth(MainMenu.username)/2), player.getY()-20);
		}
		

	}

	public void updatePosition() throws InterruptedException  {
			positionButler.put(player.getUsername(), player.getX(), player.getY(), "give position");
			if(positionButler.queryp(new ActualField(MainMenu.username), new FormalField(String.class), new FormalField(Float.class), new FormalField (Float.class)) != null) {
				Object[] pos = positionButler.get(new ActualField(MainMenu.username), new FormalField(String.class), new FormalField(Float.class), new FormalField (Float.class));
				for (Player player : playerList) {
					if (player.getUsername().equals(pos[1].toString())) {
						player.setX((float) pos[2]);
						player.setY((float) pos[3]);
					}
				}
			}
		}
	

	@Override
	public int getID() {
		return Game.ID;
	}

}
