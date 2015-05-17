import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

public class HeartFire extends Skill {

	final static int WIDTH = 200;
	final static int HEIGHT = 200;
	final static int DAMAGE = 2; // damage per 100ms;
	final static int COLDDOWN = 6; //ori 6
	final static int SHOW_TIME = 1;
	final static int TYPE = 3;
	static int coldDown;
	static boolean able = true;

	public HeartFire(int x, int y, int id) {
		this.Type = TYPE;
		this.id = id;
		this.location = new Point(x, y);
		this.width = WIDTH;
		this.height = HEIGHT;
		this.damage = DAMAGE;
		this.effect = true;
	}

	public HeartFire(Hero h) {
		this.Type = TYPE;
		this.location = h.location;
		this.damage = DAMAGE;
		this.width = WIDTH;
		this.height = HEIGHT;
		this.able = false;
		new Thread() {
			public void run() {
				try {
					coldDown = COLDDOWN;
					while (coldDown > 0) {
						sleep(1000);
						coldDown -= 1;
						if (coldDown == COLDDOWN - SHOW_TIME)
							GameFrame.skills.remove(HeartFire.this);
					}
					able = true;
				} catch (InterruptedException e) {
				}
			}
		}.start();
	}

	public void draw(Graphics g) {
		Color ori = g.getColor();
		g.setColor(Color.RED);
//		g.fillOval(location.x - width / 2, location.y - height / 2, width,
//				height);
		Image i = new ImageIcon("HeartFire.png").getImage();
		g.drawImage(i, location.x - width / 2, location.y - height / 2, width,
				height, null);
		g.setColor(ori);
	}

	public int getType() {
		return TYPE;
	}

	public Rectangle2D getShape() {
		return new Rectangle(location.x - width / 2, location.y - height / 2,
				width, height);

	}
}
