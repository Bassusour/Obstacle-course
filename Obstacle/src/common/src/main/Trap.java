package common.src.main;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.jspace.RemoteSpace;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

public class Trap {
	
	private int size, X, Y;
	private Button button;
	private static Circle[] bombs = new Circle[10];
	private static Circle[] superbombs = new Circle[10];
	private static Circle[] bullets = new Circle[20];
	private static long spawntime;
	
	static RemoteSpace trapButler;
	
	public Trap(int size, int X, int Y, Button button) {
		this.size = size;
		this.X = X;
		this.Y = Y;
		this.button = button;
		
	}

	public static void setBombs(Graphics graphics, long time, Button button) {
		
		if (button.isPressed()) {
			
			System.out.println("in button pressed");
			
			spawntime = time;
			
			for (int i = 0; i < 10; i++) {
				int randomX = ThreadLocalRandom.current().nextInt(button.trapX + 10, button.trapX + 400 - 10);
				int randomY = ThreadLocalRandom.current().nextInt(button.trapY + 10, button.trapY + 100 - 10);
				bombs[i] = new Circle(randomX, randomY, 10);
				
				for (int j = 0; j < i; j++) {
					if (bombs[i].intersects(bombs[j])) {
						i--;
						break;
					}
				}
			}
			
			button.unpressed();
		} 
		
		long timeDifference = System.currentTimeMillis() - spawntime;
		
		if (timeDifference < 2500) {
			
			for (int i = 0; i < (timeDifference / 250) % 10; i++) {
				graphics.setColor(Color.lightGray);
				graphics.fill(bombs[i]);
			}
			
		} else {
			
			for (Circle bomb : bombs) {
				
				if (timeDifference < 3000) {
					System.out.println("gray");
					graphics.setColor(Color.lightGray);
				} else {
					System.out.println("red");
					graphics.setColor(Color.red);
				}
				System.out.println("in else");
				graphics.fill(bomb);
				
			}	
		}
	}
	
	public static void setSuperbombs(Graphics graphics, long time, Button button) {
		
		if (button.isPressed()) {
			
			spawntime = time;
			
			int randomX = 100;
			
			for (int i = 0; i < 10; i++) {
				
				randomX = randomX + ThreadLocalRandom.current().nextInt(100, 200);
				int choice = ThreadLocalRandom.current().nextInt(0, 2);
				
				int randomY;
				if (choice == 0) {
					randomY = 1020;
				} else {
					randomY = 1060;
				}
					
				superbombs[i] = new Circle(randomX, randomY, 10);
				
				for (int j = 0; j < i; j++) {
					if (superbombs[i].intersects(superbombs[j])) {
						i--;
						break;
					}
				}
			}
		} 
		
		long timeDifference = System.currentTimeMillis() - spawntime;
		
		if (timeDifference < 1000) {
			
			for (int i = 0; i < (timeDifference / 100) % 10; i++) {
				graphics.setColor(Color.lightGray);
				graphics.fill(superbombs[i]);
			}
			
		} else {
			
			for (Circle bomb : superbombs) {
				
				if (timeDifference < 1000) {
					graphics.setColor(Color.lightGray);
				} else if (timeDifference < 1500) {
					graphics.setColor(Color.red);
				} else {
					//int randomMovement = ThreadLocalRandom.current().nextInt(0);
					bomb.setX(bomb.getX() - 7);
				}
				
				if (bomb.getX() > 100) {
					graphics.fill(bomb);
				}
				
			}
			
		}
	}
	
	public static void setBullets(Graphics graphics, long time, Button button) {
		
		long timeDifference = System.currentTimeMillis() - spawntime;
		
		if (button.isPressed()) {
			
			spawntime = time;
			
			for (int i = 0; i < 20; i++) {
				int randomX = ThreadLocalRandom.current().nextInt(button.trapX + 10, button.trapX + 90);
				bullets[i] = new Circle(randomX, button.trapY, 5);
			}
			
		} else {
			
			graphics.setColor(Color.red);
			
			for (int i = 0; i < (timeDifference / 175) % 20; i++) {
				
				if (bullets[i].getY() < 700 || button.trapX == 1820) {
					graphics.fill(bullets[i]);
					bullets[i].setY(bullets[i].getY() + 20);
				}
				
			}

		}
		
	}

}
