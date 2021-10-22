import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Classe qui permet de traiter les commandes du client
 *
 */
public class ClientHandler extends Thread {

	/**
	 * Variable de connexion
	 */
	private Socket socket;
	private int clientNumber;
	private DataInputStream in;
	private DataOutputStream out;

	private String path = "";

	/**
	 * Constructeur du ClientHandler
	 * 
	 * @param socket       Le socket de communication retourner par le serveur
	 * @param clientNumber Le numéro d'identification du client
	 */
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
			out.writeUTF("Hello from server - you are clients#" + clientNumber);

			while (true) { // condition d'arret
				out.writeUTF(path.substring(Server.getIndexBegin()));
				String command = in.readUTF();
				printCommand(command);
				commandManagement(command);

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
	 * Traite la commande du client
	 * 
	 * @param command La command du client à traiter
	 * @throws IOException IOException Si un I/O error se passe pendant l'envoie de
	 *                     la réponse.
	 */
	private void commandManagement(String command) throws IOException {
		String[] tabString = command.split(Utilitaire.getCommandRegex());
		try {
			switch (tabString[Utilitaire.getPosCommand()]) {

			case "mkdir":
				// creer fichier
				create_folder(tabString[Utilitaire.getPosFile()]);
				break;
			case "cd":
				changeDirectory(tabString[Utilitaire.getPosFile()]);
				break;
			case "cd..":
				parent_directory();
				break;
			case "delete":
				deleteFile(tabString[Utilitaire.getPosFile()]);
				break;
			case "ls":
				displayFiles();
				break;
			case "upload":
				Utilitaire.receiveFile(in, path + File.separator + new File(tabString[Utilitaire.getPosFile()]).getName());
				out.writeUTF("\tFichier recu!");
				break;
			case "download":
				String fileName = path + File.separator + new File(tabString[Utilitaire.getPosFile()]).getName();
				if (!new File(fileName).isFile()) {
					out.writeBoolean(false);
					throw new FileNotFoundException("File not found");
				} else
					out.writeBoolean(true);
				if (tabString.length == 3) {
					fileToZip(fileName);
					Utilitaire.sendFile(out, fileName.concat(".zip")); // TODO: arrive pas envoye le zip
					new File(fileName + ".zip").delete();
				} else {
					Utilitaire.sendFile(out, fileName);
				}
				out.writeUTF(tabString.length == 3 ? "\tZip file sent" : "\tFile sent");

				break;
			}
		} catch (NullPointerException e) {

		} catch (UnsupportedOperationException e) {
			out.writeUTF("\t" + e.getMessage());
		} catch (FileNotFoundException e) {
			out.writeUTF("\tError the file or the directory " + tabString[Utilitaire.getPosFile()] + " "
					+ "does not exist : " + e.getMessage());
		} catch (SecurityException e) {
			socket.close();
		} catch (IOException e) {
			socket.close();
		}

	}

	/**
	 * Supprime le fichier ou le répertoire
	 * 
	 * @param file Le nom du fichier ou du repertoire
	 * @throws IOException IOException Si un I/O error se passe pendant l'envoie de
	 *                     la réponse.
	 */
	private void deleteFile(String file) throws IOException {

		File fileDelete = new File(path + File.separator + file);
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
	 * Se déplace au répertoire auquel le client voudrait se mettre
	 * 
	 * @param directory Le nom du répertoire
	 * @throws IOException IOException Si un I/O error se passe pendant l'envoie de
	 *                     la réponse.
	 */
	private void changeDirectory(String directory) throws IOException {

		if (new File(path).getName().equals(directory)) {
			out.writeUTF("\tYou are already in this directory");
		} else {
			if (new File(path + File.separator + directory).isDirectory()) {
				path += File.separator + directory;
				out.writeUTF("\tYou are now in the directory " + directory);
			} else {
				out.writeUTF("\t" + directory + " is not a folder");
			}

		}

	}

	/**
	 * Creer un ou plusieurs fichier dans le répertoire où se trouve le client
	 * actuellement.
	 * 
	 * @param directory Le nom du répertoire
	 * @throws IOException IOException Si un I/O error se passe pendant l'envoie de
	 *                     la réponse.
	 */
	private void create_folder(String directory) throws IOException {
		File dir = new File(path + File.separator + directory);
		// TODO verifier si c un folder

		if (dir.mkdirs()) {
			out.writeUTF("\t" + directory + " is created!");
		} else {
			out.writeUTF("\tCouldnt create: " + directory + " Try again");

		}

	}

	/**
	 * Affiche dans la console du serveur la commande du client: affiche
	 * l'adresse,le port, l'heure et la date
	 * 
	 * @param command: La commande du client
	 */
	private void printCommand(String command) {
		String tabCommand[] = command.split(Utilitaire.getCommandRegex());
		String commandToShow = "";
		for (int i = 0; i < tabCommand.length; i++) {
			commandToShow += tabCommand[i] + " ";
		}

		System.out.println("\tClient #" + this.clientNumber + "[" + get_Adress_Port() // address et port du client
				+ "//" + LocalDate.now() + " @ " + LocalTime.now().toString().split("\\.")[0] + "]: " + commandToShow);

	}

	/**
	 * Permet de compresser le fichier en format zip
	 * 
	 * @param fileName Le nom du fichier
	 * @throws IOException IOException Si un I/O error se passe pendant la creation
	 *                     du dossier compresser
	 */
	private void fileToZip(String fileName) throws IOException {

		File file = new File(fileName);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName + ".zip"));
		zos.putNextEntry(new ZipEntry(file.getName()));

		byte[] bytesToRead = Files.readAllBytes(file.toPath());
		zos.write(bytesToRead, 0, bytesToRead.length);
		zos.closeEntry();
		zos.close();

	}

	/**
	 * Permet de se déplacer dans le repertoire parent du répertoire où se situe
	 * actuellement le client
	 * 
	 * @throws IOException IOException Si un I/O error se passe pendant l'envoie de
	 *                     la réponse.
	 */
	private void parent_directory() throws IOException {
		if (!this.path.equals(Server.getRootPath_jar())) {
			path = new File(path).getParentFile().getAbsolutePath();
			out.writeUTF("\tparent directory...");
		} else {
			out.writeUTF("\tYou can't go back to a parent directory");

		}

	}

	/**
	 * Liste tous les dossiers et fichiers dans le répertoire où se situe
	 * actuellement le client
	 * 
	 * @throws IOException Si un I/O error se passe pendant l'envoie de la réponse.
	 */
	private void displayFiles() throws IOException {

		String envoie = "";
		// creates a file object containing current directory
		File directory = new File(this.path);

		// returns an array of all files
		String[] fileList = directory.list();

		// prints out files
		for (String file : fileList) {
			File listed = new File(this.path + File.separator + file);
			if (!file.equals("Server.jar")) {
				if (listed.isDirectory())
					envoie += "\t[Folder] " + file + "\n";
				else
					envoie += "\t[File] " + file + "\n";
			}

		}

		out.writeUTF(
				(envoie.equals("")) ? "\tThere's no File or Folder at the moment\n\tUpload a file or Create a directory"
						: envoie);
	}

	/**
	 * Retourne l'addresse et le port du socket
	 * 
	 * @return [Adresse IP client : Port client // Date et Heure (min, sec)] :
	 *         "Commande"
	 */
	private String get_Adress_Port() {

		return this.socket.getInetAddress().getHostAddress() + ":" + this.socket.getPort();
	}

	/**
	 * 
	 * @return Le socket
	 */
	public Socket getSocket() {
		return socket;
	}

}
