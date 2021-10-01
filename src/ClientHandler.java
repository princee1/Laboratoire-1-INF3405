import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClientHandler extends Thread {

	private Socket socket;
	private int clientNumber;
	private DataInputStream in;
	private DataOutputStream out;

	public ClientHandler(Socket socket, int clientNumber) {

		this.clientNumber = clientNumber;
		this.socket = socket;
		System.out.println("New connection with client#" + clientNumber + " at " + socket);
		try {
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated constructor stub
	}

	public void run() {

		try {

			// DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			// out.writeUTF("Hello from server - you are clients#" + clientNumber);

			while (true) { // condition d'arret

				printCommand(in.readUTF());
				out.writeUTF("\tCommmande recu! client#" + clientNumber);

			} // execution de demande

		} catch (IOException e) {

			System.out.println("Error handling client#" + clientNumber);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Couldn't close a socket, what's going on?");
			}
			System.out.println("Connection with client# " + clientNumber + " closed");
		}
	}

	/**
	 * Affiche dans la console du serveur la commande du client: affiche
	 * l'adresse,le port, l'heure et la date
	 * 
	 * @param command: La commande du client
	 */
	private void printCommand(String command) {

		System.out.println("\t[" + get_Adress_Port() // address et port du client
				+ "//" + LocalDate.now() + " @ " + LocalTime.now().toString().split("\\.")[0] + "]: " + command);

	}

	private String get_Adress_Port() {

		return this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
	}

	public Socket getSocket() {
		return socket;
	}

}
