import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class Hero extends Movable implements Serializable {

	Point location;
	int speed;
	int width = 50;
	int height;
	int id;
	int killCounter = 0;
	static boolean UnBeatable;

	final static int moveRate = 50;
	int healthPoint;
	int kind; // 1:red;2:blue

	public Hero() {
	}

	public Hero(int id, int x, int y, int width, int height, int healthPoint) {
		this.id = id;
		this.location = new Point(x, y);
		this.width = width;
		this.height = height;
		this.healthPoint = healthPoint;
	}

	public void draw(Graphics g) {
		Color ori = g.getColor();
		g.setColor(Color.GRAY);
		// g.fillOval(location.x - width / 2, location.y - height / 2, width,
		// height);
		Image i = null;
		if (this.kind == 1)
			i = new ImageIcon("snoopy.jpg").getImage();
		if (this.kind == 2)
			i = new ImageIcon("TransFormer.jpg").getImage();
		g.drawImage(i, location.x - width / 2, location.y - height / 2, width,
				height, null);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		g.drawString("" + this.healthPoint, location.x - 10, location.y
				- height / 2 - 10);
		g.setColor(ori);
	}

	public void move(Point p, int actionCount) {
		if(speed == 0)
			return;
		int time = (int) location.distance(p) / speed;
		new Thread() {
			public void run() {

				int count = actionCount;
				for (int i = 0; i < time; i++) {
					if (count != GameFrame.actionCount) {
						break;
					}
					location.x += (p.x - location.x) / (time - i);
					location.y += (p.y - location.y) / (time - i);
					try {
						Thread.sleep(moveRate);
					} catch (InterruptedException e) {
					}
				}

			}
		}.start();
	}

	public void skillQ(Point p) {

	}

	public void skillQ(Hero h, Point p) {

	}

	public void skillQ(Hero h) {

	}

	public void skillW() {

	}

	public void skillW(Hero h) {

	}

	public void skillE() {

	}

	public Shape getShape() {
		return null;
	}

	public void underAttack(Skill s) {
		if (this.getShape().intersects(s.getShape())
				&& s.belong != GameFrame.myHero && s.effect) {
			if(GameFrame.myHero.UnBeatable){
				return;
			}
			this.healthPoint -= s.damage;

			s.effect = false;
			if(s.Type == 3){
				s.effect = true;
			}
			if (this.healthPoint <= 0 ) {
				GameFrame.myHero.speed = 0;
				GameFrame.myHero.healthPoint = 0;
			}
			return;
		}
		if (this.getShape().intersects(s.getShape()) && s.effect
				&& s.belong == this) {
			if(GameFrame.myHero.UnBeatable){
				return;
			}
			this.healthPoint -= s.damage;
			s.effect = false;
			if (this.healthPoint <= 0) {
				GameFrame.myHero = null;
				new HeroChooser(null);
			}
		}
	}

}
