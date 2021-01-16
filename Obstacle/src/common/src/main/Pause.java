package common.src.main;

import java.awt.Font;
import java.io.IOException;

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

public class Pause extends BasicGameState {
	
	public static final int ID = 2;
	private int windowWidth;
	private int windowHeight;
	Image resumeGameButton;
	Image optionsButton;
	Image mainMenuButton;
	Image desktopButton;
	
	RemoteSpace server;
	
	Sound buttonClick;
	
	Input input;

	public String mouse;
	

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
//		try {
//			server = new RemoteSpace("tcp//" + Client.IP + "server?keep");
//		} catch (IOException e) {}
		
		resumeGameButton = new Image("res/resumeGameButton.png");
		optionsButton = new Image("res/optionsButton.png");
		mainMenuButton = new Image("res/mainMenuButton.png");
		desktopButton = new Image("res/desktopButton.png");
		buttonClick = new Sound("res/buttonClickSound.wav");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		String pauseTitle = "PAUSED";
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
		g.drawImage(resumeGameButton, (windowWidth/2)-(resumeGameButton.getWidth()/2), 400);
		g.drawImage(optionsButton, (windowWidth/2)-(optionsButton.getWidth()/2), 500);
		g.drawImage(mainMenuButton, (windowWidth/2)-(mainMenuButton.getWidth()/2), 600);
		g.drawImage(desktopButton, (windowWidth/2)-(desktopButton.getWidth()/2), 700);
		
		g.setColor(Color.white);
		
		TrueTypeFont font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 50), true);
		int width = font.getWidth(pauseTitle);
		font.drawString( (windowWidth/2)-(width/2), 200,  pauseTitle);
		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		input = gc.getInput();
		
		int posX = input.getMouseX();
		int posY = input.getMouseY();
	
		resumeClick(posX, posY, sbg);
		optionsClick(posX, posY, sbg);
		mainMenuClick(posX, posY, sbg);
		desktopClick(posX, posY, gc);
		
		if(input.isKeyPressed(Input.KEY_0)) {
			gc.exit();
		}
		
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(1);
		}		
	}
	
	private void resumeClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(resumeGameButton.getWidth()/2) && posX <= (windowWidth/2)-(resumeGameButton.getWidth()/2) + resumeGameButton.getWidth()) && (posY >= 400 && posY <= 400 + resumeGameButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
				sbg.enterState(Client.GAME);
			}
		}
	}
	
	private void optionsClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(optionsButton.getWidth()/2) && posX <= (windowWidth/2)-(optionsButton.getWidth()/2) + optionsButton.getWidth()) && (posY >= 500 && posY <= 500 + optionsButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
				sbg.enterState(Client.MAIN_MENU);
			}
		}
	}
	
	private void mainMenuClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(mainMenuButton.getWidth()/2) && posX <= (windowWidth/2)-(mainMenuButton.getWidth()/2) + mainMenuButton.getWidth()) && (posY >= 600 && posY <= 600 + mainMenuButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
				sbg.enterState(Client.MAIN_MENU);
			}
		}
	}
	
	private void desktopClick(int posX, int posY, GameContainer gc) {
		if((posX >= (windowWidth/2)-(desktopButton.getWidth()/2) && posX <= (windowWidth/2)-(desktopButton.getWidth()/2) + desktopButton.getWidth()) && (posY >= 700 && posY <= 700 + desktopButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
//				server.put("player"+playerCount);
				gc.exit();
			}
		}
	}

	@Override
	public int getID() {
		return Pause.ID;
	}

}
