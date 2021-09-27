import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
	}