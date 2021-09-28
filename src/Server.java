import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Server {

	private static ServerSocket listener;

	public static void main(String arg[]) throws Exception {
		int clientNumber = 0;
		int serverPort;
		String serverAddress;

		serverAddress = Utilitaire.ipAdress_validation();
		serverPort = Utilitaire.port_validation();

		// String serverAddress= "132.207.29.108";
		// int serverPort=5005;

		listener = new ServerSocket();
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);

		listener.bind(new InetSocketAddress(serverIP, serverPort));

		System.out.format("This server is running on %s:%d%n", serverAddress, serverPort);

		try {

			while (true) {
				new ClientHandler(listener.accept(), clientNumber++).start();

			}

		} finally {

			listener.close();

		}

	}

}
