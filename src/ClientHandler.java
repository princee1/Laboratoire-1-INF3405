import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClientHandler extends Thread {

		private Socket socket;
		private int clientNumber;

		public ClientHandler(Socket socket, int clientNumber) {

			this.clientNumber = clientNumber;
			this.socket = socket;

			System.out.print("New connection with client#" + clientNumber + " at " + socket);

			// TODO Auto-generated constructor stub
		}

	
	public void run() {

			try {

				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF("Hello from server -  you are clients#" + clientNumber);

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
	 * @param command: La commande du client
	 */
	private  void printCommand(String command) {

		System.out.println("\t[" +get_Adress_Port() // address et port du client
				+ "//" + LocalDate.now() + " @ " + LocalTime.now().toString().split("\\.")[0] + "]: " + command);

	}

	private String get_Adress_Port() {
		
		return this.socket.getInetAddress().getHostAddress()+":"+this.socket.getPort();
	}

}

