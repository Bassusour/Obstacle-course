package common.src.main;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspace.ActualField;
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
	
	RemoteSpace ready;
	
	private int windowWidth;
	
	List<Object []> readyList;
	public static ArrayList<Player> playerList;
	
	String mouse;
	String lobbyTitle = "LOBBY";
	
	Sound buttonClick;
	
	TrueTypeFont font2;
	TrueTypeFont font;
	
	int width;
	
	Input input;
	
	Rectangle playerBox;

	
	Image readyButton;
	Image mainMenuButton;

	public Lobby() {
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		playerBox = new Rectangle(0,380,700, 430);
		font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 50), true);
		font2 = new TrueTypeFont(new Font("Trebuchet", Font.ITALIC, 30), true);
		playerBox.setX((Client.WIDTH/2)-(playerBox.getWidth()/2));
		width = font.getWidth(lobbyTitle);
		readyButton = new Image("res/readyButton.png");
		buttonClick = new Sound("res/buttonClickSound.wav");
		mainMenuButton = new Image("res/mainMenuButton.png");
		gc.setAlwaysRender(true);
		try {
			ready = new RemoteSpace("tcp://" + Client.IP + "/ready?keep");
		} catch (IOException e) {}
		playerList = new ArrayList<Player>();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		font.drawString( (windowWidth/2)-(width/2), 200,  lobbyTitle);
		g.drawString(mouse, 10, 50);
		font2.drawString(windowWidth/2-playerBox.getWidth()/2, 340, "Players joined");
		
		g.drawImage(readyButton, (windowWidth/2)-(readyButton.getWidth()), 900);
		g.drawImage(mainMenuButton, (windowWidth/2)+10, 900);
		g.draw(playerBox);
		
		printPlayers(readyList, g);

	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		input = gc.getInput();
		
		try {
			readyList = ready.queryAll(new FormalField(String.class), new FormalField(String.class));
		} catch (InterruptedException e) {}
		
		for (int i = 0; i < readyList.size(); i++) {
			String username = readyList.get(i)[0].toString();
			if(!hasPlayer(username)) {
				playerList.add(new Player(25, false, username));
			}
			
		}
		
		int posX = input.getMouseX();
		int posY = input.getMouseY();
		
		windowWidth = gc.getWidth();
		
		mouse = "x: " + posX + " y: " + posY;
		
		readyClick(posX, posY, sbg);
		mainMenuClick(posX, posY, sbg);
		
		Object[] allReady = null;
		try {
			allReady = ready.queryp(new ActualField("all ready"), new FormalField(Integer.class));
		} catch (InterruptedException e) {}
		
		if(allReady != null) {
			System.out.println("Should start");
			playerList.get((int) allReady[1]).setEnemy(true);
			sbg.enterState(Client.GAME);
		}

	}
	
	private boolean hasPlayer (String username) {
		for (Player player : playerList) {
			if(player.getUsername().equals(username)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void printPlayers (List<Object []> playerList, Graphics g) {
		for(int i = 0, pos = 400; i < playerList.size(); i++, pos += 40) {
			Object [] player = playerList.get(i);
			String playerName = player[0].toString() + " - " + player[1];
			TrueTypeFont font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 25), true);
			int width = font.getWidth(playerName);
			font.drawString( playerBox.getX()+20, pos,  playerName);
		}
	}

	private void readyClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(readyButton.getWidth()) && posX <= (windowWidth/2) && (posY >= 900 && posY <= 900 + readyButton.getHeight()))) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				try {
					ready.put(MainMenu.username, "change ready", "poop");
				} catch (InterruptedException e) {}
				buttonClick.play();
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
