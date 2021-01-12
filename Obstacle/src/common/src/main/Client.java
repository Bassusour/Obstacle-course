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
import org.newdawn.slick.state.StateBasedGame;

public class Client extends StateBasedGame {
	//Client-server
	private static RemoteSpace inbox;
	private static RemoteSpace server;
	private static int playerCount = 0;
	
	// Game state ids
	public static final int MAINMENU = 0;
	public static final int GAME = 1;
	public static final int PAUSE = 2;
	public static final int FIND_MATCH = 3;
	public static final int CREATE_MATCH = 4;
	
	// App properties
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int FRAMES = 60;
	public static final double VERSION = 6.9;
	
	public Client(String title) {
		super(title);
	}
	
	public static void main(String[] args) {
		//System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		//System.setProperty(System.getProperty("java.library.path"));
		
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
			
			inbox = new RemoteSpace("tcp://"+ip+":"+port+"/player" + playerCount + "?keep");
            
            System.out.println("Sucessfully setup");
            
            AppGameContainer app = new AppGameContainer(new Client("Title"));
            app.setDisplayMode(400, 400, false);
            app.setShowFPS(false);// true for display the numbers of FPS
            app.setVSync(true); // false for disable the FPS synchronize
            app.start();
			
			
        }
        catch (SlickException | IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
		
	}

	public void initStatesList(GameContainer arg0) throws SlickException {
		this.addState(new MainMenu());
		this.addState(new Game(playerCount, inbox));
		this.addState(new Pause());
		this.addState(new FindMatch());
		this.addState(new CreateMatch());
	}
	
	
}
