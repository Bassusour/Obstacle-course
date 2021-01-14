package common.src.main;

import java.awt.Font;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class CreateMatch extends BasicGameState {
	
	public static final int ID = 4;
	
	String username;
	Image createButton;
	
	Sound buttonClick;
	
	Input input;
	
	private int windowWidth;
	private int windowHeight;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		createButton = new Image("res/createButton.png");
		buttonClick = new Sound("res/buttonClickSound.wav");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		String createMatchTitle = "CREATE MATCH";
		TrueTypeFont font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 50), true);
		int width = font.getWidth(createMatchTitle);
		font.drawString( (windowWidth/2)-(width/2), 200,  createMatchTitle);
		
		g.drawImage(createButton, (windowWidth/2)-(createButton.getWidth()/2), 900);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		
		input = gc.getInput();
		
		int posX = input.getMouseX();
		int posY = input.getMouseY();
		
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
		
		username = MainMenu.username;
		
		createClick(posX, posY, sbg);
		
		Input input = gc.getInput();
        if(input.isKeyPressed(Input.KEY_ESCAPE)) {
            gc.exit();
        }

	}
	
	private void createClick(int posX, int posY, StateBasedGame sbg) {
		if((posX >= (windowWidth/2)-(createButton.getWidth()/2) && posX <= (windowWidth/2)-(createButton.getWidth()/2) + createButton.getWidth()) && (posY >= 900 && posY <= 900 + createButton.getHeight())) {
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				buttonClick.play();
				sbg.enterState(Client.LOBBY);
			}
		}
	}

	@Override
	public int getID() {
		return CreateMatch.ID;
	}

}
