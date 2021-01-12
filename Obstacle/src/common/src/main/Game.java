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
	private int c;
	private String mainPlayer;
	private Client client;
	private boolean go = false; //false if multiplayer
	private static int playerCount = 0;
	public static final int ID = 1;
	Image background;

	public Game(int playerCount, RemoteSpace inbox) {
		this.inbox = inbox;
		this.playerCount = playerCount;
	}

	@Override
	public void render(GameContainer container, StateBasedGame sbg,  Graphics graphics) throws SlickException {
		graphics.setColor(Color.cyan);
//		graphics.setBackground(Color.white);
		graphics.fill(rec);
		graphics.fill(rec2);
	}

	@Override
	public void init(GameContainer arg0,  StateBasedGame sbg) throws SlickException {
		try {
			System.out.println(playerCount);
			mainPlayer = "player"+playerCount;
			//inbox = new RemoteSpace("tcp://127.0.0.1:9001/player" + playerCount + "?keep");
			server = new RemoteSpace("tcp://127.0.0.1:9001/server?keep");
			
		} catch (IOException e) { } 
		rec = new Rectangle(100,300,25,25);
		rec2 = new Rectangle(300,300,25,25);
	}

	@Override
	public void update(GameContainer con, StateBasedGame sbg,  int arg1) throws SlickException {
		Input input = con.getInput();
		
		if(go) { //Waits for all clients to synchronize
			
			try {
				updatePosition();
			} catch (InterruptedException e) { }
			
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
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(2);
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

	@Override
	public int getID() {
		return Game.ID;
	}

}
