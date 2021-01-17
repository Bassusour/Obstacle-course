package common.src.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	private RemoteSpace positionButler;
	RemoteSpace playerButler;

	ArrayList<Player> playerList;

	private boolean go = false; //false if multiplayer
	public static final int ID = 1;
	Image background;
	
	final static int WIDTH = 1920;
	final static int HEIGHT = 1080;
	
	private Player player;
	private Path path;
	private Room room;
	
	String username = MainMenu.username;
	
	private boolean createdPlayer = false;

	public Game() {
		
	}

	@Override
	public void init(GameContainer arg0,  StateBasedGame sbg) throws SlickException {
		try {
			positionButler = new RemoteSpace("tcp://" + Client.IP + "/positionButler?keep");
			playerButler = new RemoteSpace("tcp://" + Client.IP + "/playerButler?keep");
			
		} catch (IOException e) { }
		playerList = Client.playerList;
		
		path = new Path(Path.PATH_ONE_HORIZONTAL, Path.PATH_ONE_VERTICAL);
		room = new Room(player, path, Teleporter.PATH_ONE_TELEPORTERS);
		
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
		
	}

	@Override
	public void update(GameContainer con, StateBasedGame sbg,  int arg1) throws SlickException {
		Input input = con.getInput();
		
//		System.out.println("Player list: " + playerList.toString());
		
		if(!createdPlayer) {
			for (Player player : playerList) {
				if (player.getUsername().equals(MainMenu.username)) {
					this.player = player;
				}
			}
			createdPlayer = true;
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
				if (i < 16) {
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
				if (i < 13) {
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
			//System.out.println("Sent coordinates to server from " + mainPlayer);
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
