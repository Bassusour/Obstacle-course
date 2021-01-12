package common.src.main;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

public class Room {
	
	private Player player;
	private Path path;
	private Teleporter[] teleporters;
	
	public static Circle[] PATH_ONE_PORTS = {
			new Circle(350, 150, 15, 15),
			new Circle(550, 50, 15, 15)
	};
	
	public Room(Player player, Path path, Teleporter[] teleporters) {
		this.player = player;
		this.path = path;
		this.teleporters = teleporters;
	}
	
	public Circle getTeleportElement(int i) {
		return teleporters[i].getShape();
	}

}
