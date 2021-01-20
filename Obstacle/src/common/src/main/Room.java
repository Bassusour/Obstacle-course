package common.src.main;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

public class Room {
	
	private Player player;
	private Path path;
	private Teleporter[] teleporters;
	private Button[] buttons;
	
	public Room(Player player, Path path, Teleporter[] teleporters, Button[] buttons) {
		this.player = player;
		this.path = path;
		this.teleporters = teleporters;
		this.buttons = buttons;
	}
	
	public Circle getTeleportElement(int i) {
		return teleporters[i].getShape();
	}
	
	public Rectangle getButtonElement(int i) {
		return buttons[i].getShape();
	}

}
