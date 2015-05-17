import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.rmi.ssl.SslRMIClientSocketFactory;

public class ClientNet {
	Socket s;
	String ServerIP;
	int ServerTCPPort;
	int ServerUDPport;
	int ClientUDPport = 3338;
	static int myid;
	DataOutputStream dos = null;
	DataInputStream dis = null;
	DatagramSocket ds = null;

	/*
	 * MsgType 1.hero 2.skill 3.preparing skill
	 */
	public ClientNet(String ip, int port, int UdpPort) {
		this.ServerIP = ip;
		this.ServerTCPPort = port;
		this.ClientUDPport = UdpPort;
		while (true) {
			try {
				s = new Socket(ServerIP, ServerTCPPort);
				dos = new DataOutputStream(s.getOutputStream());
				dos.writeInt(ClientUDPport);
				dis = new DataInputStream(s.getInputStream());
				ServerUDPport = dis.readInt();
				myid = dis.readInt();
				System.out.println("Server port: " + ServerUDPport);
				dos.close();
				dis.close();
				s.close();
				ds = new DatagramSocket(ClientUDPport);
				break;
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (SocketException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// send myhero
		new Thread() {
			public void run() {
				try {
					while (true) {
						if (GameFrame.myHero != null) {
							byte[] data = new byte[1024];
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							dos = new DataOutputStream(baos);
							dos.writeInt(1); // type : 1.hero 2.skill
							dos.writeInt(myid);
							dos.writeInt(GameFrame.myHero.kind);
							dos.writeInt(GameFrame.myHero.location.x);
							dos.writeInt(GameFrame.myHero.location.y);
							dos.writeInt(GameFrame.myHero.width);
							dos.writeInt(GameFrame.myHero.height);
							dos.writeInt(GameFrame.myHero.healthPoint);
							data = baos.toByteArray();
							DatagramPacket dp = new DatagramPacket(data,
									data.length, new InetSocketAddress(
											ServerIP, ServerUDPport));
							ds.send(dp);
							sleep(100);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		// send skills
		new Thread() {
			public void run() {
				try {
					while (true) {
						System.out.println(GameFrame.skills.size());
						byte[] data = new byte[1024];
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						dos = new DataOutputStream(baos);
						dos.writeInt(2);
						dos.writeInt(myid);
						dos.writeInt(GameFrame.skills.size());
						for (int i = 0; i < GameFrame.skills.size(); i++) {
							Skill s = GameFrame.skills.get(i);
							dos.writeInt(s.Type);
							dos.writeInt(s.location.x);
							dos.writeInt(s.location.y);
							dos.writeInt(s.width);
							dos.writeInt(s.height);
						}
						// dos.writeInt(2); // type : 1.hero 2.skill
						// // 3.skill-DeadArrow
						// dos.writeUTF(ds.getLocalAddress().getHostAddress());
						// dos.writeInt(GameFrame.skills.size());
						// for (Skill s : GameFrame.skills) {
						// if (!s.prepering) {
						// dos.writeInt(s.location.x);
						// dos.writeInt(s.location.y);
						// dos.writeInt(s.width);
						// dos.writeInt(s.height);
						// }
						// }
						data = baos.toByteArray();
						DatagramPacket dp = new DatagramPacket(data,
								data.length, new InetSocketAddress(ServerIP,
										ServerUDPport));
						ds.send(dp);
						sleep(100);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		// receive
		new Thread() {
			public void run() {
				while (true) {
					byte[] recData = new byte[1024];
					DatagramPacket dp2 = new DatagramPacket(recData,
							recData.length);
					int msgType;
					int id;
					int type;
					int kind;
					int size;
					int x = 0;
					int y = 0;
					int width = 0;
					int height = 0;
					int healthPoint = 0;
					try {
						ds.receive(dp2);
						dis = new DataInputStream(new ByteArrayInputStream(
								recData));
						msgType = dis.readInt();
						if (msgType == 1) {
							id = dis.readInt();
							kind = dis.readInt();
							x = dis.readInt();
							y = dis.readInt();
							width = dis.readInt();
							height = dis.readInt();
							healthPoint = dis.readInt();
							// if (GameFrame.EnemyHeros.size() < id) {
							// GameFrame.EnemyHeros.add(new Hero(id, x, y,
							// width, height, healthPoint));
							// }
							if (id == myid)
								continue;
							boolean found = false;
							for (Hero h : GameFrame.EnemyHeros) {
								if (h != null && h.id == id) {
									h.location.x = x;
									h.location.y = y;
									h.width = width;
									h.height = height;
									h.healthPoint = healthPoint;
									found = true;
									if (h.healthPoint <= 0) {
										GameFrame.EnemyHeros.remove(h);
									}
									if (found)
										break;
								}
								found = false;
							}
							if (!found && healthPoint > 0) {
								if (kind == 1) {
									GameFrame.EnemyHeros.add(new RedHero(id, x,
											y, width, height, healthPoint));
								} else if (kind == 2) {
									GameFrame.EnemyHeros.add(new BlueHero(id,
											x, y, width, height, healthPoint));
								} else if (kind == 3) {
									GameFrame.EnemyHeros.add(new GreenHero(id,
											x, y, width, height, healthPoint));
								}
							}
						} else if (msgType == 2) {
							boolean found = false;
							id = dis.readInt();
							size = dis.readInt();
							type = dis.readInt();
							x = dis.readInt();
							y = dis.readInt();
							width = dis.readInt();
							height = dis.readInt();
							if (id == myid)
								continue;
							if (size == 0) {
								Iterator<Skill> i = GameFrame.EnemySkills
										.iterator();
								while (i.hasNext()) {
									Skill s = i.next();
									if (s.id == id) {
										i.remove();
									}
								}
								continue;
							} else {
								for (int i = 0; i < size; i++) {
									if (type == 1) {
										for (Skill s : GameFrame.EnemySkills) {
											if (s.Type == type && s.id == id) {
												s.location.x = x;
												s.location.y = y;
												found = true;
												break;
											}
										}
										if (found)
											continue;
										GameFrame.EnemySkills
												.add(new PurpleStar(x, y, id));

									} else if (type == 2) {
										for (Skill s : GameFrame.EnemySkills) {
											if (s.Type == type && s.id == id) {
												s.location.x = x;
												s.location.y = y;
												found = true;
												break;
											}
										}
										if (found)
											continue;
										GameFrame.EnemySkills.add(new DeadBall(
												x, y, id));
									} else if (type == 3) {
										for (Skill s : GameFrame.EnemySkills) {
											if (s.Type == type && s.id == id) {
												s.location.x = x;
												s.location.y = y;
												found = true;
												break;
											}
										}
										if (found)
											continue;
										GameFrame.EnemySkills
												.add(new HeartFire(x, y, id));
									} else if (type == 4) {
										for (Skill s : GameFrame.EnemySkills) {
											if (s.Type == type && s.id == id) {
												s.location.x = x;
												s.location.y = y;
												found = true;
												break;
											}
										}
										if (found)
											continue;
										GameFrame.EnemySkills.add(new LightHit(
												x, y, width, height, id));
									}
								}
							}
							// width = dis.readInt();
							// height = dis.readInt();
							// if (GameFrame.EnemySkills.size() == 0) {
							// GameFrame.EnemySkills.add(new LightHit(ip, x, y,
							// width, height));
							// System.out.println("first skill");
							// }
							// for (Skill s : GameFrame.EnemySkills) {
							// if (s != null && s.ip != null) {
							// if (s.ip.equals(ip)) {
							// s.location.x = x;
							// s.location.y = y;
							// s.width = width;
							// s.height = height;
							// } else {
							// GameFrame.EnemyHeros.add(new Hero(ip,
							// x, y, width, height,
							// healthPoint));
							// }
							// }
							// }
						} else if (msgType == 3) {

						} else if (msgType == 4) {
							int udpport = dis.readInt(); // no use
							int exitID = dis.readInt();
							for (Hero h : GameFrame.EnemyHeros) {
								if (h.id == exitID) {
									GameFrame.EnemyHeros.remove(h);
								}
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public void ExitMsg() {
		try {
			byte[] data = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			dos.writeInt(4);
			dos.writeInt(ClientUDPport);
			dos.writeInt(ClientNet.myid);
			data = baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(data, data.length,
					new InetSocketAddress(ServerIP, ServerUDPport));
			ds.send(dp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// new ClientNet();
	}
}