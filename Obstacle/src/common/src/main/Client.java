package common.src.main;

import java.io.IOException;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class Client extends BasicGame {
	
	public Client(String title) {
		super(title);
	}
	
	public static void main(String[] args) {
		//System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		try
        {
            AppGameContainer app = new AppGameContainer(new Client("Game"));
            // app.setDisplayMode(screenSize.width, screenSize.height, true); => Full screen
            app.setDisplayMode(640, 480, false);
            app.setShowFPS(true); // true for display the numbers of FPS
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
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		graphics.drawString("Hello World", 50, 60);
		
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {
		// TODO Auto-generated method stub
		
	}
	
}
