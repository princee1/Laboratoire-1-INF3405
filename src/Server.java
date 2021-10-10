import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class Server {

	private static int clientNumber = 0;
	private static ServerSocket listener;
	private static final String ROOTPATH_JAR = System.getProperty("user.dir") + "\\" + "Test Data";
	private static final int INDEX_BEGIN = (System.getProperty("user.dir") + "\\").length();
	private static HashMap<Integer, ClientHandler> mapClient = new HashMap<Integer, ClientHandler>();
	private static final int MAX_CLIENT = 100;

	public static void main(String arg[]) throws Exception {

	//	System.out.println(ROOTPATH_JAR);
		connection();

		try {

			while (true) {
				new ClientHandler(listener.accept(), clientNumber++).start();
			}

		} finally {

			listener.close();

		}

	}

	private static void connection() {
		int serverPort;
		String serverAddress;

		// serverAddress = Utilitaire.ipAdress_validation();
		// serverPort = Utilitaire.port_validation();

		serverAddress = "127.0.0.1";
		serverPort = 5000;

		try {
			listener = new ServerSocket();
			listener.setReuseAddress(true);
			InetAddress serverIP = InetAddress.getByName(serverAddress);

			listener.bind(new InetSocketAddress(serverIP, serverPort));
			System.out.format("This server is running on %s:%d%n", serverAddress, serverPort);
		} catch (UnknownHostException e) {

		} catch (BindException e) {

		} catch (SocketException e) {

		} catch (IOException e) {

		}
	}

	public static int getIndexBegin() {
		return INDEX_BEGIN;
	}

	public static final String getRootPath_jar() {
		return ROOTPATH_JAR;
	}

}

