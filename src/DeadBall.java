import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

public class DeadBall extends Skill {

	final static int WIDTH = 200;
	final static int HEIGHT = 200;
	final static int COLDDOWN = 6; //ori 6
	final static int DAMAGE = 20;
	final static int SPEED = 8;
	final static int REFRESH_RATE = 20;
	final static int TYPE = 2;
	static boolean able = true;

	public DeadBall(int x, int y, int id) {
		this.Type = TYPE;
		this.location = new Point(x, y);
		this.id = id;
		this.width = WIDTH;
		this.height = HEIGHT;
		this.damage = DAMAGE;
		this.effect = true;
	}

	public DeadBall(String ip, int x, int y, int width, int height) {
		this.location = new Point(x, y);
		this.width = width;
		this.height = height;
	}

	public DeadBall(Hero h) {
		this.Type = 3;
		preparing = true;
		location = h.location;
	}

	public DeadBall(Point p) {
		Hero h = GameFrame.myHero;
//		h.healthPoint -= 10;
		this.belong = h;
		preparing = false;
		able = false;
		damage = DAMAGE;
		effect = true;
		this.Type = TYPE;
		location = (Point) h.location.clone();
		this.width = WIDTH;
		this.height = HEIGHT;
		if(p.distance(h.location) < 1){
			p.move((int)p.getX() + 10, (int)p.getY());
		}
		double slope = p.distance(h.location);
		double Yrate = (double) (p.y - h.location.y) / slope;
		double Xrate = (double) (p.x - h.location.x) / slope;
		new Thread() {
			public void run() {
				while (true) {
					DeadBall.this.location.x += (int) (SPEED * Xrate);
					DeadBall.this.location.y += (int) (SPEED * Yrate);
					try {
						sleep(REFRESH_RATE / 2);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
		new Thread() {
			public void run() {
				try {
					coldDown = COLDDOWN;
					while (coldDown > 0) {
						sleep(1000);
						coldDown -= 1;
						if (DeadBall.this.location.x > GameFrame.screenWidth
								|| DeadBall.this.location.y > GameFrame.screenHeight
								|| DeadBall.this.location.x < 0
								|| DeadBall.this.location.y < 0)
							GameFrame.skills.remove(DeadBall.this);
					}
					able = true;
				} catch (InterruptedException e) {
				}
			}
		}.start();
	}

	public DeadBall(Hero h, Point p) {
		for (Skill s : GameFrame.skills) {
			if (s.Type == 0)
				GameFrame.skills.remove(s);
		}
//		h.healthPoint -= 10;
		this.belong = h;
		preparing = false;
		able = false;
		damage = DAMAGE;
		effect = true;
		this.Type = TYPE;
		location = (Point) h.location.clone();
		this.width = WIDTH;
		this.height = HEIGHT;
		if(p.distance(h.location) < 1){
			p.move((int)p.getX() + 10, (int)p.getY());
		}
		double slope = p.distance(h.location);
		double Yrate = (double) (p.y - h.location.y) / slope;
		double Xrate = (double) (p.x - h.location.x) / slope;

		new Thread() {
			public void run() {
				while (true) {
					DeadBall.this.location.x += (int) (SPEED * Xrate);
					DeadBall.this.location.y += (int) (SPEED * Yrate);
					try {
						sleep(REFRESH_RATE / 2);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
		new Thread() {
			public void run() {
				try {
					coldDown = COLDDOWN;
					while (coldDown > 0) {
						sleep(1000);
						coldDown -= 1;
						if (DeadBall.this.location.x > GameFrame.screenWidth
								|| DeadBall.this.location.y > GameFrame.screenHeight
								|| DeadBall.this.location.x < 0
								|| DeadBall.this.location.y < 0)
							GameFrame.skills.remove(DeadBall.this);
					}
					able = true;
				} catch (InterruptedException e) {
				}
			}
		}.start();
	}

	public void draw(Graphics g) {
		// if (this.location.x < 0 || this.location.x > GameFrame.screenWidth
		// || this.location.y < 0
		// || this.location.y > GameFrame.screenHeight) {
		// if (this.belong == GameFrame.myHero)
		// GameFrame.skills.remove(this);
		// }

		if (belong != GameFrame.myHero) {
			Color ori = g.getColor();
			g.setColor(Color.BLACK);
			// g.fillOval(location.x - width / 2, location.y - height / 2,
			// width,
			// height);
			Image i = new ImageIcon("DeadBall.png").getImage();
			g.drawImage(i, location.x - width / 2, location.y - height / 2,
					width, height, null);
			g.setColor(ori);
			return;
		}

		if (!preparing) {
			Color ori = g.getColor();
			g.setColor(Color.BLACK);
			// g.fillOval(location.x - width / 2, location.y - height / 2,
			// width,
			// height);
			Image i = new ImageIcon("DeadBall.png").getImage();
			g.drawImage(i, location.x - width / 2, location.y - height / 2,
					width, height, null);
			g.setColor(ori);
		} else if (GameFrame.status == 1 && able) {
			Color ori = g.getColor();
			g.setColor(Color.BLUE);
			g.drawString("ʮ", GameFrame.mousePoint.x - 8,
					GameFrame.mousePoint.y + 8);
			g.setColor(ori);
		}
	}

	public int getType() {
		return TYPE;
	}

	public Rectangle2D getShape() {
		return new Rectangle(location.x - width / 2, location.y - height / 2,
				width, height);
	}
}
