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
import org.newdawn.slick.state.StateBasedGame;

public class Client extends StateBasedGame {
	
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
		try
        {
            AppGameContainer app = new AppGameContainer(new Client("Obstacle Course " + VERSION));
            // app.setDisplayMode(screenSize.width, screenSize.height, true); => Full screen
            app.setDisplayMode(1920, 1080, true);
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
			
			//Object[] t = inbox.get(new FormalField(String.class));
			//System.out.println(t[0]);
		} catch (IOException | InterruptedException e) { }
		
	}

	

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
		this.addState(new MainMenu());
		this.addState(new Game());
		this.addState(new Pause());
		this.addState(new FindMatch());
		this.addState(new CreateMatch());
		
	}
	
}
