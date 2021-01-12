package common.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.jspace.ActualField;
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
	private RemoteSpace inbox;
	private static RemoteSpace server;
	private Rectangle rec = null;
	private Rectangle rec2 = null;
	private int c;
	private String mainPlayer;
	private boolean go = true; //false if multiplayer
	private static int playerCount = 0;
	
	public Client(String title, String mainPlayer) {
		super(title);
		this.mainPlayer = mainPlayer;
	}
	
	public static void main(String[] args) {
		//System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		//System.setProperty(System.getProperty("java.library.path"));
		Client client;
		try
        {
			server = new RemoteSpace("tcp://127.0.0.1:9001/server?keep");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
			System.out.println("Enter ip address or \"localhost\"");
			String ip = reader.readLine();
			if(ip.equals("localhost")) {
				ip = "127.0.0.1";
			}
			System.out.println("Enter port number");
			String port = reader.readLine();
			
			server.put("getPlayerCount");
			playerCount = (int)(server.get(new FormalField(Integer.class), new FormalField(String.class)))[0]; //gets current player count from server
			
			client = new Client("Game"+playerCount, "player"+playerCount);
			client.inbox = new RemoteSpace("tcp://"+ip+":"+port+"/" + client.mainPlayer + "?keep");
            
            
            System.out.println("Sucessfully setup");
            
            AppGameContainer app = new AppGameContainer(client);
            // app.setDisplayMode(screenSize.width, screenSize.height, true); => Full screen
            
            app.setDisplayMode(640, 480, false);
            app.setShowFPS(false);// true for display the numbers of FPS
            app.setVSync(true); // false for disable the FPS synchronize
            app.start();
			
			
        }
        catch (SlickException | IOException | InterruptedException    e)
        {
            e.printStackTrace();
        }
		
	}

	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		graphics.setColor(Color.cyan);
		graphics.drawString(Integer.toString(c), 10, 20);
//		graphics.setBackground(Color.white);
		graphics.fill(rec);
		graphics.fill(rec2);
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		rec = new Rectangle(100,300,25,25);
		rec2 = new Rectangle(300,300,25,25);
		c = 0;
	}

	@Override
	public void update(GameContainer con, int arg1) throws SlickException {
		
		if(go) { //Waits for all clients to synchronize
		
			try {
				updatePosition();
			} catch (InterruptedException e) { }
			
			Input input = con.getInput();
			if(input.isKeyDown(Input.KEY_W)) {
				c++;
				rec.setY(rec.getY()-3);
			}
			
			if(input.isKeyDown(Input.KEY_S)) {
				c++;
				rec.setY(rec.getY()+3);
			}
			
			if(input.isKeyDown(Input.KEY_A)) {
				c++;
				rec.setX(rec.getX()-3);
			}
			
			if(input.isKeyDown(Input.KEY_D)) {
				c++;
				rec.setX(rec.getX()+3);
			}
		} else {
			try {
				server.put(true, mainPlayer + " ready");
				inbox.get(new ActualField ("go"));
				go = true;
			} catch (InterruptedException e) { }
		}
	}
	
	public void updatePosition() throws InterruptedException  {
		server.put(rec.getX(), rec.getY(), mainPlayer);
		
		if(inbox.queryp(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class), new ActualField ("Pos")) != null) {
			Object[] t = inbox.get(new FormalField(Float.class), new FormalField(Float.class), new FormalField(String.class), new ActualField ("Pos"));
			if(t[2].equals("player1")) {
				rec2.setX((float) t[0]);
				rec2.setY((float) t[1]);
			} else if(t[2].equals("player2")) {
				rec2.setX((float) t[0]);
				rec2.setY((float) t[1]);
			} else if(t[2].equals("player3")) {
				//rec3.setX((float) t[0]);
				//rec3.setY((float) t[1]);
				//osv
			}
		}
	}
}
