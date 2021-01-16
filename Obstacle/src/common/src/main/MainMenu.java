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
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenu extends BasicGameState {
	
	public static final int ID = 0;
	
	private int windowWidth;
	private int windowHeight;
	
	Image findMatchButton;
	Image createButton;
	Image desktopButton;
	TrueTypeFont font;
	
	RemoteSpace playerButler;
	
	Input input;
	
	TextField userField;
	public static String username = "";
	Sound buttonClick;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		try {
			playerButler = new RemoteSpace("tcp://" + Client.IP + "/playerButler?keep");
		} catch (IOException e) {}
		
		font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 25), true);
		userField = new TextField(gc, font, 0, 0, 200, 35);
		
		buttonClick = new Sound("res/buttonClickSound.wav");
		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		font.drawString( (userField.getX()-font.getWidth("Username: ")), userField.getY(), "Username: ");
		findMatchButton = new Image("res/findButton.png");
		createButton = new Image("res/createButton.png");
		desktopButton = new Image("res/desktopButton.png");
		g.drawImage(findMatchButton, (windowWidth/2)-(findMatchButton.getWidth()/2), 300);
//		g.drawImage(createButton, (windowWidth/2)-(createButton.getWidth()/2), 400);
		g.drawImage(desktopButton, (windowWidth/2)-(desktopButton.getWidth()/2), 500);
		
		userField.setLocation((windowWidth/2)-(userField.getWidth()/2), 100);
		userField.setBorderColor(Color.transparent);
		userField.setMaxLength(15);
		userField.setBackgroundColor(Color.darkGray);
		userField.render(gc,  g);
		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		
		input = gc.getInput();
		int posX = input.getMouseX();
		int posY = input.getMouseY();
		
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
		
		findMatchClick(posX, posY, sbg);
//		createClick(posX, posY, sbg);
		desktopClick(posX, posY, gc);
		
		username = userField.getText();

	}
	
	private void findMatchClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(findMatchButton.getWidth()/2) && posX <= (windowWidth/2)-(findMatchButton.getWidth()/2) + findMatchButton.getWidth()) && (posY >= 300 && posY <= 300 + findMatchButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				createPlayer();
				buttonClick.play();
				sbg.enterState(Client.LOBBY);
			}
		}
	}
	
	private void createPlayer() {
		try {
			playerButler.put(username, "create player");
			System.out.println("send create to server");
		} catch (InterruptedException e) {}
		
	}

	private void createClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(createButton.getWidth()/2) && posX <= (windowWidth/2)-(createButton.getWidth()/2) + createButton.getWidth()) && (posY >= 400 && posY <= 400 + createButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
				sbg.enterState(Client.CREATE_MATCH);
			}
		}
	}
	
	private void desktopClick(int posX, int posY, GameContainer gc) {
		if((posX >= (windowWidth/2)-(desktopButton.getWidth()/2) && posX <= (windowWidth/2)-(desktopButton.getWidth()/2) + desktopButton.getWidth()) && (posY >= 500 && posY <= 500 + desktopButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
				gc.exit();
			}
		}
	}

	@Override
	public int getID() {
		return MainMenu.ID;
	}
	
	

}
