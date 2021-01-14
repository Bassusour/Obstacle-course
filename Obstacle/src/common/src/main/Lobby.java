package common.src.main;

import java.awt.Font;
import java.io.IOException;
import java.util.List;

import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Lobby extends BasicGameState {
	
	public static final int ID = 5;
	
	String host;
	
	RemoteSpace server;
	
	private int windowWidth;
	private int windowHeight;
	
	List<Object []> playerList;
	
	String mouse;
	String lobbyTitle = "LOBBY";
	
	Sound buttonClick;
	
	TrueTypeFont font2;
	TrueTypeFont font;
	
	int width;
	
	Input input;
	RemoteSpace players;
	
	Rectangle playerBox;
	private int playerCount;
	
	Image readyButton;
	Image mainMenuButton;

	public Lobby(int playerCount) {
		this.playerCount = playerCount;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		gc.setMinimumLogicUpdateInterval(20);
		playerBox = new Rectangle(0,380,700, 430);
		font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 50), true);
		font2 = new TrueTypeFont(new Font("Trebuchet", Font.ITALIC, 30), true);
		playerBox.setX((Client.WIDTH/2)-(playerBox.getWidth()/2));
		width = font.getWidth(lobbyTitle);
		readyButton = new Image("res/readyButton.png");
		buttonClick = new Sound("res/buttonClickSound.wav");
		mainMenuButton = new Image("res/mainMenuButton.png");
		try {
			players = new RemoteSpace("tcp://127.0.0.1:9001/players?keep");
			server = new RemoteSpace("tcp://127.0.0.1:9001/server?keep");
		} catch (IOException e) {}

	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		font.drawString( (windowWidth/2)-(width/2), 200,  lobbyTitle);
		g.drawString(mouse, 10, 50);
		font2.drawString(windowWidth/2-playerBox.getWidth()/2, 340, "Players joined");
		
		g.drawImage(readyButton, (windowWidth/2)-(readyButton.getWidth()), 900);
		g.drawImage(mainMenuButton, (windowWidth/2)+10, 900);
		g.draw(playerBox);
		
		printPlayers(playerList, g);

	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		input = gc.getInput();
		
		try {
			playerList = players.queryAll(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
		} catch (InterruptedException e) {}
		
		int posX = input.getMouseX();
		int posY = input.getMouseY();
		
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
		
		mouse = "x: " + posX + " y: " + posY;
		
		readyClick(posX, posY, sbg);
		mainMenuClick(posX, posY, sbg);

	}
	
	private void printPlayers (List<Object []> playerList, Graphics g) {
		for(int i = 0, pos = 400; i < playerList.size(); i++, pos += 40) {
			Object [] player = playerList.get(i);
			String playerName = player[0].toString() + " - " + player[2];
			TrueTypeFont font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 25), true);
			int width = font.getWidth(playerName);
			font.drawString( playerBox.getX()+20, pos,  playerName);
		}
	}

	private void readyClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(readyButton.getWidth()) && posX <= (windowWidth/2) && (posY >= 900 && posY <= 900 + readyButton.getHeight()))) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				try {
					server.put("player"+playerCount, "ready", "changeReady");
				} catch (InterruptedException e) {}
				buttonClick.play();
				sbg.enterState(Client.GAME);
			}
		}
	}
	
	private void mainMenuClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)+10 && posX <= (windowWidth/2)+10+ mainMenuButton.getWidth()) && (posY >= 900 && posY <= 900 + mainMenuButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
				sbg.enterState(Client.MAIN_MENU);
			}
		}
	}

	@Override
	public int getID() {
		return Lobby.ID;
	}

}
