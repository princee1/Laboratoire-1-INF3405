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

		boolean erreurAddressIp;
		int clientNumber = 0;
		Scanner scannerIn = new Scanner(System.in);

		String serverAddress;
		String tabString[];

	
		do {
			erreurAddressIp = false;
			System.out.println("Adresse Ip avec le format 4 octets XXX.XXX.XX.XXX?");

			serverAddress = scannerIn.nextLine();
			tabString = serverAddress.trim().split("\\.");

			if (tabString.length == 4) {

				try {
				for (int i = 0; i < 4; i++) {
					int a = Integer.parseInt(tabString[i].trim());
					if (a < 0 || a > 255) {
						System.out.println("L'adresse IP doit contenir que des octets");
						erreurAddressIp=true;
						break;
					}
				}}catch(NumberFormatException e) {
					
					System.out.println("Addresse ip doit contenir que des chiffres: "+e.getMessage());
					erreurAddressIp = true;

				}
			} else {

				System.out.println("l'adresse IP doit contenir 4 octects");
				erreurAddressIp = true;
			}

		} while (erreurAddressIp);

		int serverPort;
		System.out.println("Port Ip?");
		serverPort = scannerIn.nextInt();
		while (serverPort < 5002 || serverPort > 5049) {
			System.out.println("Erreur !Port Invalide!");

			serverPort = scannerIn.nextInt();
		}

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
