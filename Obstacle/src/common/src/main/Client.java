package common.src.main;

import java.io.IOException;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.ShapeRenderer;

public class Client extends BasicGame {
	
	private boolean collision = false;
	
	final static int WIDTH = 1920;
	final static int HEIGHT = 1080;
	
	private Player player;
	private Path path;
	private Room room;
	
	public Client(String title) {
		super(title);
	}
	
	@Override
	public void init(GameContainer arg0) throws SlickException {		
		player = new Player(25, false);
		path = new Path(Path.PATH_ONE_HORIZONTAL, Path.PATH_ONE_VERTICAL);
		room = new Room(player, path, Teleporter.PATH_ONE_TELEPORTERS);	
	}
	
	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		
		graphics.setColor(Color.white);
		graphics.drawString("Collision: " + collision, 1700, 50);
		graphics.drawString("Player X: " + player.getX(), 1700, 70);
		graphics.drawString("Player Y: " + player.getY(), 1700, 90);
		
		
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
	public void update(GameContainer container, int arg1) throws SlickException {
		
		Input input = container.getInput();
	
        if(input.isKeyPressed(Input.KEY_ESCAPE)) {
            container.exit();
        }
		
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
	}
	
	public static void main(String[] args) {
		try
        {
            AppGameContainer app = new AppGameContainer(new Client("Game"));
            app.setDisplayMode(WIDTH, HEIGHT, true);
            app.setShowFPS(false);// true for display the numbers of FPS
            app.setVSync(true); // false for disable the FPS synchronize
            app.start();
        }
        catch (SlickException e)
        {
            e.printStackTrace();
        }
		
		try {
			RemoteSpace inbox = new RemoteSpace("tcp://25.65.87.75:9001/client1?keep");
			RemoteSpace server = new RemoteSpace("tcp://25.65.87.75:9001/server?keep");
			
			System.out.println("Connected from client");
			server.put("test from client");
			
		} catch (IOException | InterruptedException e) { }
		
	}
}
