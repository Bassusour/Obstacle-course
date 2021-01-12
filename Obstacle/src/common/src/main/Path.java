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
			// North bound (Not enemy)
			new Rectangle(0, 200, 400, 2),
			new Rectangle(100, 400, 500, 2),
			new Rectangle(0, 600, 600, 2)
	};
	
	public static Rectangle[] PATH_ONE_VERTICAL = {
			// West bound (Not enemy)
			new Rectangle(400, 100, 2, 100),
			new Rectangle(600, 0, 2, 400),
			new Rectangle(600, 500, 2, 100),
			// East bound (Not enemy)
			new Rectangle(500, 0, 2, 300),
			new Rectangle(100, 300, 2, 100),
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
