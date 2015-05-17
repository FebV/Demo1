import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;


public class SkillBar{
	
	final static int Q_ICON_X = GameFrame.screenWidth/4 - 100;
	final static int Q_ICON_Y = GameFrame.screenHeight - 10;
	final static int W_ICON_X = GameFrame.screenWidth/2 - 100;
	final static int W_ICON_Y = GameFrame.screenHeight - 10;
	final static int E_ICON_X = GameFrame.screenWidth/4*3 - 100;
	final static int E_ICON_Y = GameFrame.screenHeight - 10;
	public SkillBar(){
		
	}
	
	public void draw(Graphics g){
		Color c = g.getColor();
		g.setColor(Color.BLUE);
		if(GameFrame.myHero != null){
			if(GameFrame.myHero.kind == 1){
				g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,20));
				g.drawString("Q : PurpleSatr " + PurpleStar.coldDown, Q_ICON_X, Q_ICON_Y);
				g.drawString("W : Blink " + RedHero.wColdDown, W_ICON_X, W_ICON_Y);
				g.drawString("E : UnBeatable " + RedHero.eColdDown, E_ICON_X, E_ICON_Y);
				g.setColor(c);
			}else if(GameFrame.myHero.kind == 2){
				g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,20));
				g.drawString("Q : DeadBall " + DeadBall.coldDown, Q_ICON_X, Q_ICON_Y);
				g.drawString("W : HeartFire " + HeartFire.coldDown, W_ICON_X, W_ICON_Y);
				g.drawString("E : Recover " + BlueHero.eColdDown, E_ICON_X, E_ICON_Y);
				g.setColor(c);
			}else if(GameFrame.myHero.kind == 3){
				g.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,20));
				g.drawString("Q : LightHit " + LightHit.coldDown, Q_ICON_X, Q_ICON_Y);
				g.drawString("W : Stronger " + GreenHero.wColdDown, W_ICON_X, W_ICON_Y);
				g.drawString("E : Refresh " + GreenHero.eColdDown, E_ICON_X, E_ICON_Y);
				g.setColor(c);
			}
		}
	}
}
