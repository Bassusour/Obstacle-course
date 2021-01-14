package common.src.main;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

public class Player {
	
	private Color color;
	private Rectangle shape;
	private int size;
	private boolean isEnemy;
	
	public Player(int size, boolean isEnemy) {
		if (isEnemy) {
			this.color = Color.decode("#F67280");
			this.shape = new Rectangle(30, 137, size, size);
		} else {
			this.color = Color.darkGray;
			this.shape = new Rectangle(30, 37, size, size);
		}
		this.size = size;
		this.isEnemy = isEnemy;
	}
	
	public Color getColor() {
		return color;
	}
	
	public Rectangle getShape() {
		return shape;
	}
	
	public boolean isEnemy() {
		return isEnemy;
	}
	
	public float getX() {
		return shape.getX();
	}
	
	public float getY() {
		return shape.getY();
	}
	
	public int getSize() {
		return size;
	}
	
	public void setX(float x) {
		shape.setX(x);
	}
	
	public void setY(float y) {
		shape.setY(y);
	}

}
