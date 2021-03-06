package common.src.main;

import java.util.Random;
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
	static Bomb[] bombs = new Bomb[10];
	static Bomb[] superbombs = new Bomb[10];
	static Bomb[] bullets = new Bomb[20];
	private static long spawntime;
	
	private static Random generator = new Random(987235907);

	public Trap(int size, int X, int Y, Button button) {
		this.size = size;
		this.X = X;
		this.Y = Y;
		this.button = button;
		
	}

	public static void setBombs(Graphics graphics, long time, Button button) {
		
		if (button.isPressed()) {
			
			spawntime = time;
			
			for (int i = 0; i < 10; i++) {
				//int randomX = ThreadLocalRandom.current().nextInt(button.trapX + 10, button.trapX + 400 - 10);
				//int randomY = ThreadLocalRandom.current().nextInt(button.trapY + 10, button.trapY + 100 - 10);
				
				int upperboundX = button.trapX + 400 - 10;
				int lowerboundX = button.trapX + 10;
				int randomX = generator.nextInt(upperboundX - lowerboundX) + lowerboundX;
				
				int upperboundY = button.trapY + 100 - 10;
				int lowerboundY = button.trapY + 10;
				int randomY = generator.nextInt(upperboundY - lowerboundY) + lowerboundY;
				
				bombs[i] = new Bomb(randomX, randomY, 10, Color.lightGray);
				
				for (int j = 0; j < i; j++) {
					if (bombs[i].getShape().intersects(bombs[j].getShape())) {
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
				graphics.fill(bombs[i].getShape());
			}
			
		} else {
			
			for (Bomb bomb : bombs) {
				
				if (timeDifference < 3000) {
					graphics.setColor(Color.lightGray);
				} else {
					graphics.setColor(Color.red);
					bomb.setColor(Color.red);
				}

				graphics.fill(bomb.getShape());

			}	
		}
	}
	
	public static void setSuperbombs(Graphics graphics, long time, Button button) {
		
		if (button.isPressed()) {
			
			spawntime = time;
			
			int randomX = 100;
			
			for (int i = 0; i < 10; i++) {
				
				/*
				randomX = randomX + ThreadLocalRandom.current().nextInt(100, 200);
				int choice = ThreadLocalRandom.current().nextInt(0, 2);
				
				int randomY;
				if (choice == 0) {
					randomY = 1020;
				} else {
					randomY = 1060;
				}*/
				
				randomX = randomX + generator.nextInt(200 - 100) + 100;
				
				int choice = generator.nextInt(2);
				
				int randomY;
				
				if (choice == 0) {
					randomY = 1020;
				} else {
					randomY = 1060;
				}
					
				superbombs[i] = new Bomb(randomX, randomY, 10, Color.lightGray);
				
				for (int j = 0; j < i; j++) {
					if (superbombs[i].getShape().intersects(superbombs[j].getShape())) {
						i--;
						break;
					}
				}
			}
			button.unpressed();
		} 
		
		long timeDifference = System.currentTimeMillis() - spawntime;
		
		if (timeDifference < 1000) {
			
			for (int i = 0; i < (timeDifference / 100) % 10; i++) {
				graphics.setColor(Color.lightGray);
				graphics.fill(superbombs[i].getShape());
			}
			
		} else {
			
			for (Bomb bomb : superbombs) {
				
				if (timeDifference < 1000) {
					graphics.setColor(Color.lightGray);
				} else if (timeDifference < 1500) {
					graphics.setColor(Color.red);
					bomb.setColor(Color.red);
				} else {
					//int randomMovement = ThreadLocalRandom.current().nextInt(0);
					bomb.getShape().setX(bomb.getShape().getX() - 7);
				}
				
				if (bomb.getShape().getX() > 100) {
					graphics.fill(bomb.getShape());
				}
				
			}
			
		}
	}
	
	public static void setBullets(Graphics graphics, long time, Button button) {
		
		long timeDifference = System.currentTimeMillis() - spawntime;
		
		if (button.isPressed()) {
			
			spawntime = time;
			
			for (int i = 0; i < 20; i++) {
				//int randomX = ThreadLocalRandom.current().nextInt(button.trapX + 10, button.trapX + 90);
				int upperboundX = button.trapX + 90;
				int lowerboundX = button.trapX + 10;
				int randomX = generator.nextInt(upperboundX - lowerboundX) + lowerboundX;
				bullets[i] = new Bomb(randomX, button.trapY, 5, Color.red);
			}
			
			button.unpressed();
			
		} else {
			
			graphics.setColor(Color.red);
			
			for (int i = 0; i < (timeDifference / 175) % 20; i++) {
				
				if (bullets[i].getShape().getY() < 700 || button.trapX == 1820) {
					graphics.fill(bullets[i].getShape());
					bullets[i].getShape().setY(bullets[i].getShape().getY() + 20);
				}
				
			}

		}
		
	}

}
