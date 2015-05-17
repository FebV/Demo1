import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class RedHero extends Hero {

	final static int KIND = 1;

	final static int WIDTH = 50;
	final static int HEIGHT = 50;
	final static int HEALTH_BAR_HEIGHT = 5;
	final static int SPEED = 6;
	final static int HEALTH_POINT = 180;
	int maxHealthPoint = HEALTH_POINT;
	final static int W_CD = 8; // cold down time for skill w
	final static int E_CD = 10;

	static int wColdDown = 0;
	static int eColdDown = 0;
	boolean wAble;
	boolean eAble;
	boolean Eing;
	boolean Wing;

	public RedHero(int id, int x, int y, int width, int height, int healthPoint) {
		this.id = id;
		this.location = new Point(x, y);
		this.width = width;
		this.height = height;
		this.healthPoint = healthPoint;
	}

	public RedHero(Point p) {
		killCounter = 0;
		wAble = true;
		eAble = true;
		Wing = false;
		location = p;
		this.width = WIDTH;
		this.height = HEIGHT;
		this.speed = SPEED;
		this.healthPoint = HEALTH_POINT;
		this.kind = KIND;
	}

	public void draw(Graphics g) {
		Color ori = g.getColor();
		g.setColor(Color.RED);
		// g.fillOval(location.x - width / 2, location.y - height / 2, width,
		// height);
		Image i = new ImageIcon("RedHero.png").getImage();
		g.drawImage(i, location.x - width / 2, location.y - height / 2, width,
				height, null);
		g.drawRect(location.x - width / 2, location.y - height / 2 - 7,
				this.width, HEALTH_BAR_HEIGHT);
		g.fillRect(location.x - width / 2, location.y - height / 2 - 7,
				this.healthPoint * this.width / maxHealthPoint,
				HEALTH_BAR_HEIGHT);
		if (Eing) {
			g.setColor(Color.YELLOW);
		}
		g.drawString("" + this.healthPoint + "  ", location.x - 12, location.y
				- height / 2 - 10);
		g.setColor(ori);

	}

	public void skillQ(Point p) {
		if (PurpleStar.able
				&& GameFrame.myHero.location.distance(p) < PurpleStar.RANGE) {
			GameFrame.skills.add(new PurpleStar(GameFrame.myHero, p));
		}
		// for(Skill s : GameFrame.skills){
		// if(s.preparing)
		// GameFrame.skills.remove(s);
		// }

	}

	public void skillQ(Hero h, Point p) {
		if (PurpleStar.able) {
			GameFrame.skills.add(new PurpleStar(h));
		}
	}

	public void skillW() {
		if (wAble) {
			// Wing = true;
			// wAble = false;
			// this.width *= 2;
			// this.height *= 2;
			// this.healthPoint *= 2;
			// this.maxHealthPoint *= 2;

			this.location = GameFrame.mousePoint;
			wAble = false;
			new Thread() {
				public void run() {
					wColdDown = W_CD;
					while (wColdDown > 0) {
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
			this.UnBeatable = true;
			int oriHP;
			Eing = true;
			eColdDown = E_CD;
			eAble = false;
			if (Wing) {
				oriHP = this.healthPoint / 2;
			} else {
				oriHP = this.healthPoint;

			}
			new Thread() {
				public void run() {
					wColdDown = W_CD;
					while (eColdDown > 0) {
						if (eColdDown == 7) {
							RedHero.this.UnBeatable = false;
							RedHero.this.healthPoint = oriHP;
							Eing = false;
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
