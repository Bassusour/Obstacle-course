package common.src.main;

import org.newdawn.slick.geom.Rectangle;

public class Path {
	
	private Rectangle[] horizontal;
	private Rectangle[] vertical;
	
	public static Rectangle[] PATH_ONE_HORIZONTAL = {
			// South bound (Not enemy)
			new Rectangle(0, 100, 400, 2),
			new Rectangle(100, 300, 400, 2),
			new Rectangle(0, 500, 600, 2),
			new Rectangle(400, 700, 300, 2),
			new Rectangle(0, 700, 300, 2),
			new Rectangle(0, 900, 1200, 2),
			new Rectangle(1300, 900, 420, 2),
			new Rectangle(800, 700, 300, 2),
			new Rectangle(1000, 500, 300, 2),
			new Rectangle(1400, 500, 420, 2),
			new Rectangle(1200, 700, 100, 2),
			new Rectangle(1400, 700, 320, 2),
			new Rectangle(900, 100, 100, 2),
			new Rectangle(1300, 100, 100, 2),
			new Rectangle(1500, 100, 420, 2),
			new Rectangle(1500, 300, 100, 2),
			// North bound (Not enemy)
			new Rectangle(0, 200, 400, 2),
			new Rectangle(100, 400, 500, 2),
			new Rectangle(0, 600, 600, 2),
			new Rectangle(0, 800, 300, 2),
			new Rectangle(400, 800, 700, 2),
			new Rectangle(0, 1000, 1820, 2),
			new Rectangle(900, 600, 820, 2),
			new Rectangle(1400, 800, 320, 2),
			new Rectangle(1100, 400, 100, 2),
			new Rectangle(1500, 200, 220, 2),
			new Rectangle(1820, 200, 100, 2),
			new Rectangle(1720, 400, 100, 2),
			new Rectangle(1500, 400, 100, 2)
	};
	
	public static Rectangle[] PATH_ONE_VERTICAL = {
			// West bound (Not enemy)
			new Rectangle(400, 100, 2, 100),
			new Rectangle(600, 0, 2, 400),
			new Rectangle(600, 500, 2, 100),
			new Rectangle(800, 0, 2, 700),
			new Rectangle(300, 700, 2, 100),
			new Rectangle(1100, 700, 2, 100),
			new Rectangle(1300, 700, 2, 200),
			new Rectangle(1820, 500, 2, 500),
			new Rectangle(1000, 100, 2, 400),
			new Rectangle(1200, 0, 2, 400),
			new Rectangle(1400, 100, 2, 400),
			new Rectangle(1820, 200, 2, 200),
			new Rectangle(1600, 300, 2, 100),
			// East bound (Not enemy)
			new Rectangle(500, 0, 2, 300),
			new Rectangle(100, 300, 2, 100),
			new Rectangle(700, 0, 2, 700),
			new Rectangle(400, 700, 2, 100),
			new Rectangle(1200, 700, 2, 200),
			new Rectangle(900, 100, 2, 500),
			new Rectangle(1720, 600, 2, 100),
			new Rectangle(1720, 800, 2, 100),
			new Rectangle(1400, 700, 2, 100),
			new Rectangle(1100, 0, 2, 400),
			new Rectangle(1300, 100, 2, 400),
			new Rectangle(1500, 100, 2, 100),
			new Rectangle(1720, 200, 2, 200),
			new Rectangle(1500, 300, 2, 100)
	};
	
	public Path(Rectangle[] horizontal, Rectangle[] vertical) {
		this.horizontal = horizontal;
		this.vertical = vertical;
	}
	
	public Rectangle[] getHorizontal() {
		return horizontal;
	}
	
	public Rectangle getHorizontalElement(int i) {
		return horizontal[i];
	}
	
	public Rectangle[] getVertical() {
		return vertical;
	}
	public Rectangle getVerticalElement(int i) {
		return vertical[i];
	}
	
	public void setHorizontal(Rectangle[] horizontal) {
		this.horizontal = horizontal;
	}
	
	public void setVertical(Rectangle[] vertical) {
		this.vertical = vertical;
	}

}
