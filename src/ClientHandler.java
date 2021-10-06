import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;

public class ClientHandler extends Thread {

	private Socket socket;
	private int clientNumber;
	private DataInputStream in;
	private DataOutputStream out;
	private String path="";

	public ClientHandler(Socket socket, int clientNumber) {

		this.clientNumber = clientNumber;
		this.socket = socket;
	
		//this.path = System.getProperty("user.dir");
				System.out.println(path);
				
				
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
				String command = in.readUTF();
				printCommand(command);

				String[] tabString = command.split(Utilitaire.getCommandRegex());

				
				switch (tabString[Utilitaire.getPosCommand()]) {
				case "mkdir":
										
						// creer fichier

						File directory = new File(path+"\\"+tabString[Utilitaire.getPosFile()]);

						if (directory.mkdir()) {
							out.writeUTF(tabString[Utilitaire.getPosFile()] + " is created!");

							
						} else {

							out.writeUTF("Couldnt create: " + tabString[Utilitaire.getPosFile()] + "\nTry again");

						}
					break;
					
				case "cd":
					// change la variable path
					
					if (path.equals(tabString[1])) {
						out.writeUTF("You are alread in this directory");
					
					}else {
						
						if (new File(path+tabString[1]).isDirectory()) {
							path += tabString[1] ;
							out.writeUTF("You are now in the directory " + tabString[1]);
						}else {
							out.writeUTF(tabString[1] + " is not a folder");
						}
					
					}
						break;
					
				case "delete":
					
					File delete = new File(path+"\\"+tabString[1]);
					if(delete.delete()) {
						out.writeUTF("The folder "+tabString[1]+" has been deleted");
					}else {
						out.writeUTF("The folder "+tabString[1]+" couldn't be deleted");
					}
					break;
					
				case "ls":
					break;
					
				case "upload":
					break;
					
				case "download":
					break;
				
				
				}
				
				
				

				// out.writeUTF("\tCommmande recu! client#" + clientNumber);

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

		System.out.println("\tClient #" + this.clientNumber + "[" + get_Adress_Port() // address et port du client
				+ "//" + LocalDate.now() + " @ " + LocalTime.now().toString().split("\\.")[0] + "]: " + command);

	}

	private String get_Adress_Port() {

		return this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
	}

	public Socket getSocket() {
		return socket;
	}

}
