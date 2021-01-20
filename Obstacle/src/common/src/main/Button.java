package common.src.main;

import org.newdawn.slick.geom.Rectangle;

public class Button {
	
	private int size, X, Y, id;
	Rectangle shape;
	Rectangle backdrop;
	private boolean pressed = false;
	private long cooldown = 0;
	int trapX, trapY;
	
	public static Button[] PATH_ONE_BUTTONS = {
			new Button(25, 200, 105, 100, 0, true, 0),
			new Button(25, 340, 390, 100, 400, true, 1),
			new Button(25, 340, 590, 200, 600, true, 2),
			new Button(25, 1140, 590, 900, 600, true, 3),
			new Button(25, 1760, 105, 1300, 0, true, 4),
			new Button(25, 710, 150, 600, 0, false, 5),
			new Button(25, 910, 350, 800, 0, false, 6),
			new Button(25, 1810, 740, 1820, 200, false, 7),
			new Button(25, 1240, 990, 100, 1000, true, 8)
	};

	public Button(int size, int X, int Y, int trapX, int trapY, boolean horizontal, int id) {
		//new Rectangle(size, size * 2, X, Y);
		if (horizontal) {
			this.shape = new Rectangle(X, Y, size, size / 4);
		} else {
			this.shape = new Rectangle(X, Y, size / 4, size);
		}
		this.size = size;
		this.X = X;
		this.Y = Y;
		this.trapX = trapX;
		this.trapY = trapY;
		this.id = id;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
	public Rectangle getShape() {
		return shape;
	}
	
	public long getCooldown() {
		return cooldown;
	}
	
	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public void pressed() {
		this.pressed = true;
	}
	
	public void unpressed() {
		this.pressed = false;
	}
	
	public boolean inUse() {
		return (cooldown - 5000) > System.currentTimeMillis();
	}
	
	public int getID() {
		return X;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
}
