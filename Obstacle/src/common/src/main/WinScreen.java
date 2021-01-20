package common.src.main;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;

import org.jspace.RemoteSpace;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class WinScreen extends BasicGameState {
	
	public static final int ID = 4;
	
	Image mainMenuButton;
	
	private int windowWidth;
	private int windowHeight;
	
	RemoteSpace playerButler;
	
	ArrayList<Player> playerList;
	
	String winTitle;
	
	Sound buttonClick;
	
	Input input;
	

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		
		try {
			playerButler = new RemoteSpace("tcp://" + Client.IP + "/playerButler?keep");
		} catch (IOException e) {}
		
		playerList = Client.playerList;
		
		mainMenuButton = new Image("res/mainMenuButton.png");
		buttonClick = new Sound("res/buttonClickSound.wav");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setBackground(Color.black);
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
		g.drawImage(mainMenuButton, (windowWidth/2)-(mainMenuButton.getWidth()/2), 600);
		
		g.setColor(Color.white);
		
		TrueTypeFont font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 50), true);
		int width = font.getWidth(winTitle);
		font.drawString( (windowWidth/2)-(width/2), 200,  winTitle);

	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		
		input = gc.getInput();
		
		int posX = input.getMouseX();
		int posY = input.getMouseY();
		
		winTitle = Game.winTitle;
		
		mainMenuClick(posX, posY, sbg);

	}
	
	private void mainMenuClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(mainMenuButton.getWidth()/2) && posX <= (windowWidth/2)-(mainMenuButton.getWidth()/2) + mainMenuButton.getWidth()) && (posY >= 600 && posY <= 600 + mainMenuButton.getHeight())) {
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
				MainMenu.createdPlayer= false;
				sbg.enterState(Client.MAIN_MENU);
			}
		}
	}

	@Override
	public int getID() {
		return WinScreen.ID;
	}

}
