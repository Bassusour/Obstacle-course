package common.src.main;

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
	
	public static final int ID = 1;
	Image background;
	
	private Rectangle rec = null;

	@Override
	public void render(GameContainer container, StateBasedGame sbg,  Graphics graphics) throws SlickException {
		graphics.setColor(Color.cyan);
		graphics.fill(rec);

	}

	@Override
	public void init(GameContainer arg0,  StateBasedGame sbg) throws SlickException {
		rec = new Rectangle(100,300,25,25);
	}

	@Override
	public void update(GameContainer con, StateBasedGame sbg,  int arg1) throws SlickException {
		Input input = con.getInput();
		
		if(input.isKeyDown(Input.KEY_W)) {
			rec.setY(rec.getY()-7);
		}
		
		if(input.isKeyDown(Input.KEY_S)) {
			rec.setY(rec.getY()+7);
		}
		
		if(input.isKeyDown(Input.KEY_A)) {
			rec.setX(rec.getX()-7);
		}
		
		if(input.isKeyDown(Input.KEY_D)) {
			rec.setX(rec.getX()+7);
		}
		
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			sbg.enterState(2);
		}
		
	}

	@Override
	public int getID() {
		return Game.ID;
	}

}
