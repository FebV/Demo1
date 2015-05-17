import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class GreenHero extends Hero {
	final static int KIND = 3;

	final static int WIDTH = 50;
	final static int HEIGHT = 50;
	final static int HEALTH_BAR_HEIGHT = 5;
	final static int SPEED = 8;
	final static int HEALTH_POINT = 120;
	final static int W_EFFECT_TIME = 5;
	final static int W_CD = 18;
	final static int E_CD = 8;
	int maxHealthPoint = HEALTH_POINT;
	static boolean wAble;
	static int wColdDown;
	static boolean eAble;
	static int eColdDown;
	static boolean powerStatus;

	public GreenHero(int id, int x, int y, int width, int height,
			int healthPoint) {
		this.id = id;
		this.location = new Point(x, y);
		this.width = width;
		this.height = height;
		this.healthPoint = healthPoint;
	}

	public GreenHero(Point p) {
		killCounter = 0;
		powerStatus = false;
		wAble = true;
		eAble = true;
		wColdDown = 0;
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
		g.setColor(Color.GREEN);
		// g.fillOval(location.x - width / 2, location.y - height / 2, width,
		// height);
		Image i = new ImageIcon("GreenHero.png").getImage();
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
		if (LightHit.able) {
			GameFrame.skills.add(new LightHit(p));
		}
	}

	public void skillW(Hero h) {
		if (wAble) {
			this.speed -= 2;
			this.powerStatus = true;
			wAble = false;
			this.width *= 2;
			this.height *= 2;
			new Thread() {
				public void run() {
					wColdDown = W_CD;
					while (wColdDown > 0) {
						if (wColdDown == W_CD - W_EFFECT_TIME) {
							GreenHero.this.powerStatus = false;
							GreenHero.this.width /= 2;
							GreenHero.this.height /= 2;
							GreenHero.this.speed += 2;
						}
						try {
							sleep(1000);
							wColdDown -= 1;
						} catch (InterruptedException e) {
						}
					}
					wAble = true;
				}
			}.start();
		}
	}

	public void skillE() {
		if (eAble) {
			if (LightHit.coldDown > 1) {
				LightHit.coldDown = 1;
			}
			eAble = false;
			new Thread() {
				public void run() {
					eColdDown = E_CD;
					while (eColdDown > 0) {
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
