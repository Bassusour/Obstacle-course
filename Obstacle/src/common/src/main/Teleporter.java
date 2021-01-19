package common.src.main;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Circle;

public class Teleporter {
	
	private Circle shape;
	private int size;
	private int X;
	private int Y;
	
	public static Teleporter[] PATH_ONE_TELEPORTERS = {
			new Teleporter(15, 350, 150),
			new Teleporter(15, 550, 30),
			new Teleporter(15, 150, 350),
			new Teleporter(15, 30, 550),
			new Teleporter(15, 550, 550),
			new Teleporter(15, 750, 30),
			new Teleporter(15, 750, 750),
			new Teleporter(15, 950, 550),
			new Teleporter(15, 1770, 550),
			new Teleporter(15, 1770, 350)
	};
	
	public Teleporter(int size, int X, int Y) {
		this.shape = new Circle(X, Y, size, size);
		this.size = size;
		this.X = X;
		this.Y = Y;
	}
	
	public Circle getShape() {
		return shape;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
}
