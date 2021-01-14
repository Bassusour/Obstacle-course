package common.src.main;

import java.awt.Font;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class FindMatch extends BasicGameState {
	
	public static final int ID = 3;
	
	private int windowWidth;
	private int windowHeight;

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		String findMatchTitle = "FIND MATCH";
		TrueTypeFont font = new TrueTypeFont(new Font("Trebuchet", Font.BOLD, 50), true);
		int width = font.getWidth(findMatchTitle);
		font.drawString( (windowWidth/2)-(width/2), 200,  findMatchTitle);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int arg2) throws SlickException {
		
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
	}

	@Override
	public int getID() {
		return FindMatch.ID;
	}

}
