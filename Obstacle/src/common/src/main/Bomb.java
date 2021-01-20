package common.src.main;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Circle;

public class Bomb {
	
	int X, Y, radius;
	Circle shape;
	private Color color;

	public Bomb(int X, int Y, int radius, Color color) {
		this.X = X;
		this.Y = Y;
		this.radius = radius;
		this.shape = new Circle(X, Y, radius);
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Circle getShape() {
		return shape;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
	public void setX(int X) {
		this.X = X;
	}
	
	public void setY(int Y) {
		this.Y = Y;
	}

}
