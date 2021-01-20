package common.src.main;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

public class Goal {
	
	private Rectangle[] pattern = new Rectangle[24];
	private int X = 30;
	private int Y = 1000;
	
	public Goal() {
		pattern[0] = new Rectangle(0, 1000, 10, 10);
		pattern[1] = new Rectangle(0, 1010, 10, 10);
		pattern[2] = new Rectangle(0, 1020, 10, 10);
		pattern[3] = new Rectangle(0, 1030, 10, 10);
		pattern[4] = new Rectangle(0, 1040, 10, 10);
		pattern[5] = new Rectangle(0, 1050, 10, 10);
		pattern[6] = new Rectangle(0, 1060, 10, 10);
		pattern[7] = new Rectangle(0, 1070, 10, 10);
		pattern[8] = new Rectangle(10, 1000, 10, 10);
		pattern[9] = new Rectangle(10, 1010, 10, 10);
		pattern[10] = new Rectangle(10, 1020, 10, 10);
		pattern[11] = new Rectangle(10, 1030, 10, 10);
		pattern[12] = new Rectangle(10, 1040, 10, 10);
		pattern[13] = new Rectangle(10, 1050, 10, 10);
		pattern[14] = new Rectangle(10, 1060, 10, 10);
		pattern[15] = new Rectangle(10, 1070, 10, 10);
		pattern[16] = new Rectangle(20, 1000, 10, 10);
		pattern[17] = new Rectangle(20, 1010, 10, 10);
		pattern[18] = new Rectangle(20, 1020, 10, 10);
		pattern[19] = new Rectangle(20, 1030, 10, 10);
		pattern[20] = new Rectangle(20, 1040, 10, 10);
		pattern[21] = new Rectangle(20, 1050, 10, 10);
		pattern[22] = new Rectangle(20, 1060, 10, 10);
		pattern[23] = new Rectangle(20, 1070, 10, 10);
	}
	
	public Rectangle[] getPattern() {
		return pattern;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}

}
