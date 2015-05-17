import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
	ArrayList<Client> clients = new ArrayList<>();
	static int ServerTCPPort = 9999;
	static int ServerUDPPort = 6666;
	static int id = 1;

	public Server() {

		new Thread() {
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(ServerTCPPort);

					while (true) {
						Socket s = ss.accept();
						DataInputStream dis = new DataInputStream(
								s.getInputStream());
						String IP = s.getInetAddress().getHostAddress();
						int UDPport = dis.readInt();
						clients.add(new Client(IP, UDPport, id));
						DataOutputStream dos = new DataOutputStream(
								s.getOutputStream());
						dos.writeInt(ServerUDPPort);
						dos.writeInt(id++);
						s.close();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					DatagramSocket ds = new DatagramSocket(ServerUDPPort);
					byte[] data = new byte[1024];
					DatagramPacket dp = new DatagramPacket(data, data.length);
					while (true) {
						ds.receive(dp);
						ByteArrayInputStream bais = new ByteArrayInputStream(
								data);
						DataInputStream in = new DataInputStream(bais);
						int MsgType = in.readInt();
						Iterator<Client> i = clients.iterator();
						while (i.hasNext()) {
							Client c = i.next();
							if (MsgType == 4) {
								int disPort = in.readInt();
								if (dp.getAddress().getHostAddress()
										.equals(c.IP)
										&& c.UDPport == disPort) {
									i.remove();
									System.out.println("ExitMsg");
								}
							}
							if (!dp.getSocketAddress().equals(
									new InetSocketAddress(c.IP, c.UDPport))) {
								dp.setSocketAddress(new InetSocketAddress(c.IP,
										c.UDPport));

								ds.send(dp);
							}
						}
						// for (Client c : clients) {
						// if(MsgType == 4){
						// int disPort = in.readInt();
						// if(dp.getAddress().getHostAddress().equals(c.IP) &&
						// c.UDPport == disPort);{
						// clients.remove(c);
						// System.out.println("ExitMsg");
						// }
						// }
						// if (!dp.getSocketAddress().equals(
						// new InetSocketAddress(c.IP, c.UDPport))) {
						// dp.setSocketAddress(new InetSocketAddress(c.IP,
						// c.UDPport));
						//
						// ds.send(dp);
						// }
						// }
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	class Client {
		String IP;
		int UDPport;
		int id;

		public Client(String IP, int UDPport, int id) {
			this.id = id;
			this.IP = IP;
			this.UDPport = UDPport;

		}
	}

	public static void main(String[] args) {
		new Server();
	}
}
