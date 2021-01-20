package common.src.main;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.newdawn.slick.Color;
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
	
	public static final int ID = 3;
	
	String host;
	
	RemoteSpace ready;
	RemoteSpace playerButler;
	
	private int windowWidth;
	
	List<Object []> readyList;
	
	String mouse;
	String lobbyTitle = "LOBBY";
	ArrayList<Player> playerList;
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
			playerButler = new RemoteSpace("tcp://" + Client.IP + "/playerButler?keep");
			ready = new RemoteSpace("tcp://" + Client.IP + "/ready?keep");
		} catch (IOException e) {}
		playerList = Client.playerList;
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setBackground(Color.black);
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
			//System.out.println(username);
			if(!hasPlayer(username)) {
				playerList.add(new Player(25, false, username));
			}
			
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
		
		for (Object[] o : readyList) {
			if (o[0].equals(MainMenu.username) && o[1].equals("ready")) {
				readyButton = new Image("res/unreadyButton.png");
			} else {
				readyButton = new Image("res/readyButton.png");
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
			playerList.get((int) allReady[1]).setEnemy(true);
			Game.createdPlayer = false;
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
				try {
					playerButler.put(MainMenu.username, "remove player");
				} catch (InterruptedException e) {}
				int index = -1;
				for (int i = 0; i < playerList.size(); i++) {
					if (playerList.get(i).getUsername().equals(MainMenu.username)) {
						index = i;
					}
				}
				if (index != -1) {
					playerList.remove(index);
				}
				buttonClick.play();
				MainMenu.createdPlayer = false;
				sbg.enterState(Client.MAIN_MENU);
			}
		}
	}

	@Override
	public int getID() {
		return Lobby.ID;
	}

}
