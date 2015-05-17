import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class BlueHero extends Hero {
	
	final static int KIND = 2;
	final static int WIDTH = 50;
	final static int HEIGHT = 50;
	final static int HEALTH_BAR_HEIGHT = 5;
	final static int SPEED = 6;
	final static int HEALTH_POINT = 200;
	final static int E_EFFECT = 10;
	final static int E_CD = 8;
	int maxHealthPoint = HEALTH_POINT;
	static boolean eAble;
	static int eColdDown;

	public BlueHero(int id, int x, int y, int width, int height, int healthPoint) {
		this.id = id;
		this.location = new Point(x, y);
		this.width = width;
		this.height = height;
		this.healthPoint = healthPoint;
	}
	
	public BlueHero(Point p) {
		killCounter = 0;
		eAble = true;
		eColdDown = 0;
		location = p;
		this.width = WIDTH;
		this.height = HEIGHT;
		this.speed = SPEED;
		this.healthPoint = HEALTH_POINT;
		this.kind = KIND;
	}

	public void draw(Graphics g) {
		Color ori = g.getColor();
		g.setColor(Color.BLUE);
//		g.fillOval(location.x - width / 2, location.y - height / 2, width,
//				height);
		Image i = new ImageIcon("BlueHero.png").getImage();
		g.drawImage(i, location.x - width / 2, location.y - height / 2, width,
				height, null);
		g.drawRect(location.x - width / 2, location.y - height / 2 - 7,
				this.width, HEALTH_BAR_HEIGHT);
		g.fillRect(location.x - width / 2, location.y - height / 2 - 7,
				this.healthPoint * this.width / maxHealthPoint,
				HEALTH_BAR_HEIGHT);
		g.drawString("" + this.healthPoint + "  ", location.x - 12, location.y
				- height / 2 - 10);
		g.setColor(ori);
	}

	public void skillQ(Hero h, Point p) {
		if (DeadBall.able) {
			GameFrame.skills.add(new DeadBall(p));
		}

	}

	public void skillW(Hero h) {
		if (HeartFire.able) {
			GameFrame.skills.add(new HeartFire(h));
			System.out.println("skill added");
		}
	}

	public void skillE() {

		if (eAble) {
			if (this.healthPoint > HEALTH_POINT - E_EFFECT)
				this.healthPoint = HEALTH_POINT;
			else
				this.healthPoint += E_EFFECT;

			eAble = false;
			new Thread() {
				public void run() {
					eColdDown = E_CD;
					while (eColdDown > 0) {
						if (eColdDown == 5) {

						}
						try {
							sleep(1000);
							eColdDown -= 1;
						} catch (InterruptedException e) {
						}
					}
					eAble = true;
				}
			}.start();
		}
	}

	public Rectangle getShape() {
		return new Rectangle(location.x - width / 2, location.y - height / 2,
				width, height);

	}
}
