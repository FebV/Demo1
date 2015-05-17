import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class GameFrame extends Frame {
	Graphics g = null;
	static ClientNet cn;
	final static int refreshRate = 50;
	protected static GameFrame gf = null;
	public static Hero myHero;
	static int actionCount;
	static Point mousePoint;
	static int screenWidth;
	static int screenHeight;
	static int status;
	static ArrayList<Hero> EnemyHeros = new ArrayList<>();
	static ArrayList<Skill> EnemySkills = new ArrayList<>();
	static boolean Netable = false;
	// status 0: move mode
	// status 1: skill range
	SkillBar skillBar;
	static String ip;
	static int port;
	static int UdpPort;
	static String ServerIP = "192.168.199.110";

	static ArrayList<Skill> skills = new ArrayList<>();

	// main method to launch
	public static void main(String[] args) {
		new GameFrame();

	}

	// initial game frame by constructor
	public GameFrame() {
		 this.setUndecorated(true);
		 this.setExtendedState(MAXIMIZED_BOTH);
//		this.setSize(800, 600);
		this.setResizable(false);
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					new EscMenu();
				}
			}
		});
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		gf = this;

		// setup listener
		this.addMouseListener(new MouseMonitor());
		this.addKeyListener(new SkillMonitor());
		this.addMouseMotionListener(new MouseLocator());

		// setup hero chooser
		new HeroChooser(this);
		new Thread(new Redraw()).start();
		screenWidth = this.getWidth();
		screenHeight = this.getHeight();
		g = this.getGraphics();
		skillBar = new SkillBar();

	}

	public void paint(Graphics g) {
		 Image ig = new ImageIcon("background.jpg").getImage();
		 g.drawImage(ig, 0, 0, screenWidth, screenHeight, null);
		g.drawString("" + ClientNet.myid, 200, 200);

		if (skillBar != null)
			skillBar.draw(g);

		Iterator<Skill> i = skills.iterator();

		while (i.hasNext()) {
			Skill s = i.next();
			// if (this.location.x < 0 || this.location.x >
			// GameFrame.screenWidth
			// || this.location.y < 0
			// || this.location.y > GameFrame.screenHeight)
			s.draw(g);
		}

		synchronized (EnemySkills) {
			for (Skill s : EnemySkills) {
				if (s.location.x < 0 || s.location.x > screenWidth
						|| s.location.y < 0 || s.location.y > screenHeight) {
					GameFrame.skills.remove(s);
				} else if (s != null && myHero != null) {
					myHero.underAttack(s);
					s.draw(g);
				}
			}
		}

		if (myHero != null) {
			myHero.draw(g);
		}

		synchronized (EnemyHeros) {
			for (Hero h : EnemyHeros) {
				if (h != null) {
					h.draw(g);
				}
			}
		}
		g.drawString("" + myHero.killCounter, 50, 50);
	}

	// double buffer
	Image bufferImage;

	public void update(Graphics g) {
		bufferImage = createImage(this.getWidth(), this.getHeight());
		paint(bufferImage.getGraphics());
		g.drawImage(bufferImage, 0, 0, null);
	}

	// repaint thread
	class Redraw implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(refreshRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// monitor
	class SkillMonitor extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_Q) {
				if (status == 0 && myHero.healthPoint > 0) {
					myHero.skillQ(myHero, mousePoint);

					status = 1;
				} else if (status == 1 && myHero.healthPoint > 0) {
					myHero.skillQ(mousePoint);
					status = 0;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_W) {
				if (GameFrame.myHero.kind == 1 && myHero.healthPoint > 0)
					myHero.skillW();
				if (GameFrame.myHero.kind == 2 && myHero.healthPoint > 0) {
					myHero.skillW(GameFrame.myHero);
				} else if (GameFrame.myHero.kind == 3 && myHero.healthPoint > 0) {
					myHero.skillW(GameFrame.myHero);
				}
				myHero.move(myHero.location, ++actionCount);
			}
			if (e.getKeyCode() == KeyEvent.VK_E) {
				if (myHero.healthPoint > 0)
					myHero.skillE();
			}
			if (e.getKeyCode() == KeyEvent.VK_S)
				myHero.move(myHero.location, ++actionCount);
			if (e.getKeyCode() == KeyEvent.VK_C)
				new ConnectMenu();

		}
	}

	class MouseLocator extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent e) {
			mousePoint = e.getPoint();
		}
	}

	// class SkillBar extends Panel{
	// public SkillBar(){
	// this.add(new SkillQIcon());
	// }
	// class SkillQIcon extends Label{
	// public SkillQIcon(){
	// super("Q");
	// }
	// }
	// }
}

class ConnectMenu extends Frame {
	public ConnectMenu() {
		this.setLayout(new FlowLayout());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ConnectMenu.this.dispose();
			}
		});
		TextField ip;
		TextField port;
		TextField ClientPort;
		Button ok;
		this.add(new Label("ip:"));
		this.add(ip = new TextField(GameFrame.ServerIP));
		this.add(new Label("port:"));
		this.add(port = new TextField("" + Server.ServerTCPPort));
		this.add(new Label("ClientPort"));
		this.add(ClientPort = new TextField("2333"));
		ok = new Button("连接");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameFrame.ip = ip.getText();
				GameFrame.port = Integer.parseInt(port.getText());
				GameFrame.UdpPort = Integer.parseInt(ClientPort.getText());
				GameFrame.cn = new ClientNet(GameFrame.ip, GameFrame.port,
						GameFrame.UdpPort);
				GameFrame.Netable = true;
				ConnectMenu.this.dispose();
			}
		});
		this.add(ok);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

}

// control menu
class EscMenu extends Frame {
	final static int ESCMENU_WIDTH = 240;
	final static int ESCMENU_HEIGHT = 320;

	public EscMenu() {

		// initial
		this.setUndecorated(true);
		this.setSize(ESCMENU_WIDTH, ESCMENU_HEIGHT);
		this.setBackground(Color.GRAY);

		// add exit button
		this.setLocationRelativeTo(null);
		this.setLayout(new GridLayout(3, 1));
		this.add(new ExitButton());
		this.add(new RechooseButton());

		// kill when lose focus
		this.addWindowListener(new WindowAdapter() {
			public void windowDeactivated(WindowEvent e) {
				EscMenu.this.dispose();
			}
		});

		this.setVisible(true);
	}

	class ExitButton extends Button {
		public ExitButton() {
			super("退出游戏");
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (GameFrame.Netable) {
						GameFrame.myHero.healthPoint = 0;
						try {
							Thread.sleep(300);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}

					System.exit(0);
				}
			});

			// kill when press ESC
			this.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
						EscMenu.this.dispose();
				}
			});
		}
	}

	class RechooseButton extends Button {
		public RechooseButton() {
			super("重选英雄");
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GameFrame.myHero.healthPoint = 0;
					new HeroChooser(GameFrame.gf);
					EscMenu.this.dispose();
				}
			});
		}
	}
}

// hero chooser menu
class HeroChooser extends Dialog {

	final static int HEROCHOOSER_WIDTH = 480;
	final static int HEROCHOOSER_HEIGHT = 350;

	// initial by constructor
	public HeroChooser(GameFrame gameFrame) {

		super(gameFrame);
		this.setSize(HEROCHOOSER_WIDTH, HEROCHOOSER_HEIGHT);
		this.setUndecorated(true);
		this.setLocationRelativeTo(null);
		this.setBackground(Color.LIGHT_GRAY);

		// add hero buttons
		this.setLayout(new GridLayout(4, 1));

		Label lbl = new Label("选择英雄");
		lbl.setFont(new Font("", 0, 30));
		Panel titlePanel = new Panel();
		titlePanel.setLayout(new GridLayout(1, 3));
		titlePanel.add(new Label());
		titlePanel.add(lbl);
		titlePanel.add(new Label());

		// red panel
		Panel redPanel = new Panel();
		redPanel.setLayout(new GridLayout(1, 3));
		redPanel.add(new RedButton());
		redPanel.add(new Label("Red is a Assassin"));
		redPanel.add(new Label());

		// blue panel
		Panel bluePanel = new Panel();
		bluePanel.setLayout(new GridLayout(1, 3));
		bluePanel.add(new BlueButton());
		bluePanel.add(new Label("Blue can use magic power from far away"));
		bluePanel.add(new Label());

		// green panel
		Panel greenPanel = new Panel();
		greenPanel.setLayout(new GridLayout(1, 3));
		greenPanel.add(new GreenButton());
		greenPanel.add(new Label(
				"Green can use magic power at a middle distance"));
		greenPanel.add(new Label());

		this.add(titlePanel);
		this.add(redPanel);
		this.add(bluePanel);
		this.add(greenPanel);
		this.setVisible(true);
	}

	class RedButton extends Button {
		public RedButton() {
			super("Red");
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GameFrame.myHero = new RedHero(new Point((int) (Math
							.random() * GameFrame.screenWidth), (int) (Math
							.random() * GameFrame.screenHeight)));
					System.out.println("hero created");
					HeroChooser.this.setVisible(false);

				}
			});
		}
	}

	class BlueButton extends Button {
		public BlueButton() {
			super("Blue");
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GameFrame.myHero = new BlueHero(new Point((int) (Math
							.random() * GameFrame.screenWidth), (int) (Math
							.random() * GameFrame.screenHeight)));
					System.out.println("hero created");
					HeroChooser.this.setVisible(false);

				}
			});
		}
	}

	class GreenButton extends Button {
		public GreenButton() {
			super("Green");
			this.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GameFrame.myHero = new GreenHero(new Point((int) (Math
							.random() * GameFrame.screenWidth), (int) (Math
							.random() * GameFrame.screenHeight)));
					System.out.println("hero created");
					HeroChooser.this.setVisible(false);

				}
			});
		}
	}

}

class MouseMonitor extends MouseAdapter {
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK
				&& GameFrame.status == 1) {

			GameFrame.myHero.move(new Point(e.getX(), e.getY()),
					++GameFrame.actionCount);
			GameFrame.status = 0;
			Iterator<Skill> i = GameFrame.skills.iterator();
			while (i.hasNext()) {
				Skill s = i.next();
				if (s.preparing)
					i.remove();
			}
		}

		if (e.getModifiers() == InputEvent.BUTTON3_MASK
				&& GameFrame.status == 0) {
			GameFrame.myHero.move(new Point(e.getX(), e.getY()),
					++GameFrame.actionCount);

		}
	}
}