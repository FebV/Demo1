import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public class Skill extends Movable implements Serializable{
	
	static int coldDown;
	static int showTime;
	static int range;
//	static boolean able = true;
	boolean preparing;
	int damage;
	static boolean effect;
	Hero belong;
	static boolean toSend;
	int Type;
	int id;
	
	public void drawRange(Graphics g){
		
	}
	
	public void draw(Graphics g){
		
	}
	
	public int getType(){
		return 0;
	}
	
	public Rectangle2D getShape(){
		return null;
		
	}
}
