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
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.ShapeRenderer;
import org.newdawn.slick.state.StateBasedGame;

public class Client extends StateBasedGame {
	//Client-server
	private static RemoteSpace inbox;
	private static RemoteSpace server;
	private static int playerCount = 0;
	
	final static int WIDTH = 1920;
	final static int HEIGHT = 1080;
	
	public static final String IP = "127.0.0.1:9001";

	// Game state ids
	public static final int MAIN_MENU = 0;
	public static final int GAME = 1;
	public static final int PAUSE = 2;
	public static final int FIND_MATCH = 3;
	public static final int CREATE_MATCH = 4;
	public static final int LOBBY = 5;
	
	// App properties
	public static final int FRAMES = 60;
	public static final double VERSION = 6.9;

	
	public Client(String title) {
		super(title);
	}
	
	public static void main(String[] args) {

		try {
			server = new RemoteSpace("tcp://" + IP + "/server?keep");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//			System.out.println("Enter ip address or \"localhost\"");
//			String ip = reader.readLine();
			String ip = IP;
			
//			if (ip.equals("localhost")) {
//				ip = "127.0.0.1";
//			}
			
//			System.out.println("Enter port number");
//			String port = reader.readLine();
			String port = "9001";
			
			server.put("getPlayerCount");
			playerCount = (int)(server.get(new FormalField(Integer.class), new FormalField(String.class)))[0]; //gets current player count from server
			
			inbox = new RemoteSpace("tcp://" + IP + "/player" + playerCount + "?keep");
            
            System.out.println("Sucessfully setup");
            
            AppGameContainer app = new AppGameContainer(new Client("Title"+playerCount));

            app.setDisplayMode(WIDTH, HEIGHT, true);
            app.setShowFPS(true); // true for display the numbers of FPS
            app.setVSync(true); // false for disable the FPS synchronize
            app.start();
			
        } catch (SlickException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
		
		try {
			RemoteSpace inbox = new RemoteSpace("tcp://" + IP + "/client1?keep");
			RemoteSpace server = new RemoteSpace("tcp://" + IP + "/server?keep");
			
			System.out.println("Connected from client");
			server.put("test from client");
			
		} catch (IOException | InterruptedException e) { }
	}

	public void initStatesList(GameContainer gc) throws SlickException {
		this.addState(new MainMenu());
		this.addState(new Game(playerCount, inbox));
		this.addState(new Pause());
		this.addState(new FindMatch());
		this.addState(new CreateMatch());
		this.addState(new Lobby(playerCount));

	}

}
