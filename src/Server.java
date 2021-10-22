import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Server {

	private static int clientNumber = 0;
	private static ServerSocket listener;
	private static final String ROOTPATH_JAR = System.getProperty("user.dir");
	private static final int INDEX_BEGIN = new File(ROOTPATH_JAR).getParent().length() + 1;

	public static void main(String arg[]) throws Exception {

		try {
			Utilitaire.startMessage(ROOTPATH_JAR);
			connection();

			while (true) {
				new ClientHandler(listener.accept(), clientNumber++).start();
			}

		}

		finally {

			listener.close();

		}

	}

	/**
	 * Permet de créer un Serveur socket
	 */
	private static void connection() {
		int serverPort;
		String serverAddress;

		serverAddress = Utilitaire.ipAdress_validation();
		serverPort = Utilitaire.port_validation();

		try {
			listener = new ServerSocket();
			listener.setReuseAddress(true);
			InetAddress serverIP = InetAddress.getByName(serverAddress);

			listener.bind(new InetSocketAddress(serverIP, serverPort), 1);

			System.out.format("This server is running on %s:%d%n", serverAddress, serverPort);
		} catch (UnknownHostException e) {
			System.out.println("Try again with another one-> " + e.getMessage());
			System.exit(0);
		} catch (BindException e) {
			System.out.println("Try again later-> " + e.getMessage());
			System.exit(0);
		} catch (SocketException e) {
			System.out.println("Error while creating or accessing the socket" + e.getMessage());
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Coulnd't determine the precise error\nI/O Error: " + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * 
	 * @return Retourne a partir de où on ne peut pas reculer dans le path
	 */
	public static int getIndexBegin() {
		return INDEX_BEGIN;
	}

	/**
	 * 
	 * @return Retourne le répertoire ou se trouve le jar
	 */
	public static final String getRootPath_jar() {
		return ROOTPATH_JAR;
	}

}
