import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public final class Utilitaire {

	/**
	 * Commande
	 */
	private static final String COMMAND_CD_DOT = "cd..", COMMAND_UPLOAD = "upload", COMMAND_DOWNLOAD = "download";

	/**
	 * Position des commandes
	 */
	private static final int POS_COMMAND = 0, POS_FILE_DIR = 1, POS_CMD_OPTION = 2;

	private static final String COMMAND_ERROR = "cd; ..";

	private static final String COMMAND_DL_ZIP = "-z";
	private static final String COMMAND_REGEX = ";";

	/**
	 * Permet de recevoir un fichier envoyé par un client ou le serveur et le creer
	 * dans le repertoire
	 * 
	 * @param fileName Nom du fichier
	 * @throws IOException       IOException Si un I/O error se passe pendant
	 *                           l'envoie de la réponse.
	 * @throws SecurityException Si le fichier ne peut etre creer
	 */
	public static void receiveFile(DataInputStream in, String fileName) throws SecurityException, IOException {

		int length = in.readInt();

		byte[] bytes = new byte[length];
		int bytesread = 0;
		while (bytesread < length) {
			bytesread += in.read(bytes, bytesread, length - bytesread);
		}
		FileOutputStream fos;
		fos= new FileOutputStream(fileName);
		fos.write(bytes);
		fos.close();
	
	}

	/**
	 * Envoie le fichier demander au client ou au serveur
	 * 
	 * @param file Nom du fichier a envoyé
	 * @throws IOException       Si un I/O error se passe pendant l'envoie de la
	 *                           réponse.
	 * @throws SecurityException Si le fichier ne peut etre accédé
	 */
	public static void sendFile(DataOutputStream out, String file) throws SecurityException, IOException {

		boolean sent = false;
		BufferedInputStream fileIn = null;
	
		byte[] bytes;
		bytes = Files.readAllBytes(Paths.get(new File(file).getAbsolutePath()));
		out.writeInt(bytes.length);
		out.write(bytes, 0, bytes.length);


	}

	/**
	 * Boucle pendant que le port entrée est non valide.Permet de validé l'entré du
	 * port.
	 * 
	 * @return le port validé
	 */
	public static int port_validation() {

		Scanner scannerIn = new Scanner(System.in);

		int serverPort;
		System.out.println("Port Ip?");
		serverPort = scannerIn.nextInt();
		while (serverPort < 5002 || serverPort > 5049) {
			System.out.println("Erreur !Port Invalide!");

			serverPort = scannerIn.nextInt();
		}

		// scannerIn.close();
		return serverPort;
	}

	/**
	 * Boucle pendant que l'adresse IP entrée est non valide.Permet de valider
	 * l'entré l'adresse IP
	 * 
	 * @return L'addresse IP validé
	 */
	public static String ipAdress_validation() {

		boolean erreurAddressIp;

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
							erreurAddressIp = true;
							break;
						}
					}
				} catch (NumberFormatException e) {

					System.out.println("Addresse ip doit contenir que des chiffres: " + e.getMessage());
					erreurAddressIp = true;

				}
			} else {

				System.out.println("l'adresse IP doit contenir 4 octects");
				erreurAddressIp = true;
			}

		} while (erreurAddressIp);

		// scannerIn.close();
		return serverAddress;
	}

	/**
	 * 
	 * @return Une commande qui génère des erreur soit "cd .."
	 */
	public static String getCommandError() {
		return COMMAND_ERROR;
	}

	/**
	 * 
	 * @return L'option zip
	 */
	public static String getCommandDlZip() {
		return COMMAND_DL_ZIP;
	}

	/**
	 * 
	 * @return La position du fichier dans la command
	 */
	public static final int getPosFile() {
		return POS_FILE_DIR;
	}

	/**
	 * 
	 * @return Le regex qui sépare les valeurs d'une commande
	 */
	public static final String getCommandRegex() {
		return COMMAND_REGEX;
	}

	/**
	 * 
	 * @return La position d'une command
	 */
	public static final int getPosCommand() {
		return POS_COMMAND;
	}

	/**
	 * 
	 * @return La position d'une option d'une command
	 */
	public static final int getPosCmdOption() {
		return POS_CMD_OPTION;
	}

	/**
	 * 
	 * @return download
	 */
	public static String getCommandDownload() {
		return COMMAND_DOWNLOAD;
	}

	/**
	 * 
	 * @return upload
	 */
	public static String getCommandUpload() {
		return COMMAND_UPLOAD;
	}

}
