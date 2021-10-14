import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	private String path = "";

	public ClientHandler(Socket socket, int clientNumber) {
		try {
			this.clientNumber = clientNumber;
			this.socket = socket;
			this.path = Server.getRootPath_jar();
			this.in = new DataInputStream(socket.getInputStream());
			this.out = new DataOutputStream(socket.getOutputStream());
			System.out.println("New connection with client#" + clientNumber + " at " + socket);
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
				out.writeUTF(path.substring(Server.getIndexBegin()));
				String command = in.readUTF();
				printCommand(command);
				gerer_command(command);

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

	private void gerer_command(String command) throws IOException {
		String[] tabString = command.split(Utilitaire.getCommandRegex());
		try {
			switch (tabString[Utilitaire.getPosCommand()]) {

			case "mkdir":
				// creer fichier
				create_folder(tabString[Utilitaire.getPosFile()]);
				break;
			case "cd":
				// change la variable path
				changeDirectory(tabString[Utilitaire.getPosFile()]);
				break;
			case "cd..":
				previous_directory();
				break;
			case "delete":
				deleteFile(tabString[Utilitaire.getPosFile()]);
				break;
			case "ls":
				displayFiles();
				break;
			case "upload":
				Utilitaire.receiveFile(in, path+"\\"+ new File(tabString[Utilitaire.getPosFile()]).getName());
					out.writeUTF("\tFichier recu!");
			//	else 
				//	out.writeUTF("\tErreur");
				break;
			case "download":
				 Utilitaire.sendFile(out, path + "\\" + new File(tabString[Utilitaire.getPosFile()]).getName());
					out.writeUTF("\tFichier envoyé");
				//else 
					//out.writeUTF("\tErreur");
				break;
			}
		} catch (NullPointerException e) {

		} catch (FileNotFoundException e) {
			out.writeUTF("\tError the file or the directory " + tabString[Utilitaire.getPosFile()] + ""
					+ "does not exist : " + e.getMessage());
		} catch (IOException e) {
			out.writeUTF("\t");

		}

	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void deleteFile(String file) throws IOException {

		File fileDelete = new File(path + "\\" + file);
		if (fileDelete.isDirectory()) {
			if (fileDelete.delete()) {
				out.writeUTF("\tThe folder " + file + " and all the associated has been deleted");
			} else {
				out.writeUTF("\tThe folder " + file + " couldn't be deleted");
			}
		} else {
			if (fileDelete.delete()) {
				out.writeUTF("\tThe file " + file + " has been deleted");
			} else {
				out.writeUTF("\tThe file " + file + " couldn't be deleted");
			}
		}

	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void changeDirectory(String file) throws IOException {

		if (new File(path).getName().equals(file)) {
			out.writeUTF("\tYou are already in this directory");
		} else {
			if (new File(path + "\\" + file).isDirectory()) {
				path += "\\" + file;
				out.writeUTF("\tYou are now in the directory " + file);
			} else {
				out.writeUTF("\t" + file + " is not a folder");
			}

		}

	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void create_folder(String file) throws IOException {
		File directory = new File(path + "\\" + file);
		// TODO verifier si c un folder

		// if (directory.isDirectory()) {
		if (directory.mkdirs()) {
			out.writeUTF("\t" + file + " is created!");
		} else {
			out.writeUTF("\tCouldnt create: " + file + " Try again");

		}
		// } else {
		// out.writeUTF(file+" is not a directory");
		// }
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

	/**
	 * 
	 */
	private void previous_directory() throws IOException {
		if (!this.path.equals(Server.getRootPath_jar())) {
			for (int i = this.path.length() - 1; i >= 0; i--) {
				if (this.path.substring(i - 1, i).equals("\\")) {
					this.path = this.path.substring(0, i - 1);
					break;
				}
			}
			out.writeUTF("\tprevous directory...");
		} else {
			out.writeUTF("\tYou can't [...]"); // TODO trouver une phrase pour cette explication

		}

	}

	
	/**
	 * 
	 * @throws IOException
	 */
	private void displayFiles() throws IOException {

		String envoie = "";
		// creates a file object containing current directory
		File directory = new File(this.path);

		// returns an array of all files
		String[] fileList = directory.list();

		// prints out files
		for (String file : fileList) {
			envoie += file + "\n";
			// System.out.println(file);
		}
		out.writeUTF(envoie);
	}

	/**
	 * 
	 * @return
	 */
	private String get_Adress_Port() {

		return this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
	}

	/**
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return socket;
	}

}
