package common.src.main;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenu extends BasicGameState {
	
	public static final int ID = 0;
	
	private int windowWidth;
	private int windowHeight;
	
	Image findMatchButton;
	Image createButton;
	Image desktopButton;
	
	Input input;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
		findMatchButton = new Image("res/findButton.png");
		createButton = new Image("res/createButton.png");
		desktopButton = new Image("res/desktopButton.png");
		g.drawImage(findMatchButton, (windowWidth/2)-(findMatchButton.getWidth()/2), 300);
		g.drawImage(createButton, (windowWidth/2)-(createButton.getWidth()/2), 400);
		g.drawImage(desktopButton, (windowWidth/2)-(desktopButton.getWidth()/2), 500);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		
		input = gc.getInput();
		int posX = input.getMouseX();
		int posY = input.getMouseY();
		
		findMatchClick(posX, posY, sbg);
		createClick(posX, posY, sbg);
		desktopClick(posX, posY, gc);

	}
	
	private void findMatchClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(findMatchButton.getWidth()/2) && posX <= (windowWidth/2)-(findMatchButton.getWidth()/2) + findMatchButton.getWidth()) && (posY >= 300 && posY <= 300 + findMatchButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				sbg.enterState(1);
			}
		}
	}
	
	private void createClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(createButton.getWidth()/2) && posX <= (windowWidth/2)-(createButton.getWidth()/2) + createButton.getWidth()) && (posY >= 400 && posY <= 400 + createButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				sbg.enterState(1);
			}
		}
	}
	
	private void desktopClick(int posX, int posY, GameContainer gc) {
		if((posX >= (windowWidth/2)-(desktopButton.getWidth()/2) && posX <= (windowWidth/2)-(desktopButton.getWidth()/2) + desktopButton.getWidth()) && (posY >= 500 && posY <= 500 + desktopButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				gc.exit();
			}
		}
	}

	@Override
	public int getID() {
		return MainMenu.ID;
	}
	
	

}
