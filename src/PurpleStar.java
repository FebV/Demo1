import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.ImageIcon;

public class PurpleStar extends Skill {

	final static int WIDTH = 100;
	final static int HEIGHT = 100;
	final static int SHOW_TIME = 2;
	final static int COLDDOWN = 5;
	final static int RANGE = 200;
	final static int DAMAGE = 3;// damage per 100ms;
	final static int TYPE = 1;
	static boolean able = true;

	public PurpleStar(String ip, int x, int y, int width, int height) {
		this.location = new Point(x, y);
		this.width = width;
		this.height = height;
	}

	public PurpleStar(int x, int y, int id) {
		this.id = id;
		this.location = new Point(x, y);
		this.width = WIDTH;
		this.height = HEIGHT;
		this.damage = DAMAGE;
		this.effect = true;
	}

	public PurpleStar(Hero h) {
		this.Type = 0;
		preparing = true;
		range = RANGE;
		location = h.location;
		belong = GameFrame.myHero;
	}

	public PurpleStar(Hero h, Point p) {
		Iterator<Skill> i = GameFrame.skills.iterator();
		while(i.hasNext()){
			Skill s = i.next();
			if(s.preparing)
				i.remove();
		}
//		synchronized (GameFrame.skills) {
//			for (Skill s : GameFrame.skills) {
//				if (s.Type == 0)
//					GameFrame.skills.remove(s);
//			}
//		}
		this.Type = TYPE;
		toSend = true;
		belong = GameFrame.myHero;
		preparing = false;
		able = false;
		damage = DAMAGE;
		effect = true;
		location = p;
		this.width = WIDTH;
		this.height = HEIGHT;
		showTime = SHOW_TIME;
		new Thread() {
			public void run() {
				try {
					coldDown = COLDDOWN;
					while (coldDown > 0) {
						sleep(1000);
						coldDown -= 1;
						if (coldDown == 3)
							GameFrame.skills.remove(PurpleStar.this);
					}
					able = true;
				} catch (InterruptedException e) {
				}
			}
		}.start();
	}

	// public LightHit(Point p, int i) {
	// preparing = false;
	// able = false;
	// damage = DAMAGE;
	// effect = true;
	// location = p;
	// this.width = WIDTH;
	// this.height = HEIGHT;
	// showTime = SHOW_TIME;
	// new Thread() {
	// public void run() {
	// try {
	// coldDown = COLDDOWN;
	// while (coldDown > 0) {
	// sleep(1000);
	// coldDown -= 1;
	// if (coldDown == 3)
	// GameFrame.skills.remove(LightHit.this);
	// }
	// able = true;
	// } catch (InterruptedException e) {
	// }
	// }
	// }.start();
	// }

	public void draw(Graphics g) {
		if (belong == null || belong != GameFrame.myHero) {
			Color ori = g.getColor();
			g.setColor(Color.YELLOW);
//			g.fillRect(location.x - width / 2, location.y - height / 2, width,
//					height);
			Image i = new ImageIcon("PurpleStar.gif").getImage();
			g.drawImage(i, location.x - width / 2, location.y - height / 2,
					width, height, null);
			g.setColor(ori);
			return;
		}

		if (!preparing) {
			Color ori = g.getColor();
			g.setColor(Color.YELLOW);
//			g.fillRect(location.x - width / 2, location.y - height / 2, width,
//					height);
			Image i = new ImageIcon("PurpleStar.gif").getImage();
			g.drawImage(i, location.x - width / 2, location.y - height / 2,
					width, height, null);
			g.setColor(ori);
		} else if (GameFrame.status == 1 && able) {
			Color ori = g.getColor();
			g.setColor(Color.RED);
			g.drawOval(location.x - range, location.y - range, range * 2,
					range * 2);
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
