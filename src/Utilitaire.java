import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

	private static final String COMMAND_ERROR = "cd .. ";

	private static final String COMMAND_DL_ZIP = "-z";
	private static final String COMMAND_REGEX = " ";

	/**
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static void receiveFile(DataInputStream in, String fileName) throws IOException {
		// String name = new File(fileName).getName();

		boolean received = false;
		DataOutputStream fileOut = null;
		// try {
		fileOut = new DataOutputStream(new FileOutputStream(fileName));

		long length = in.readLong();

		byte[] bytes = new byte[4096];
		int count = 0;

		while (length > 0 && (count = in.read(bytes, 0, (int) Math.min(bytes.length, length))) != -1) {
			fileOut.write(bytes, 0, count);
			fileOut.flush();
			length -= count;
		}
		fileOut.close();
		received = true;

		/**
		 * } catch (FileNotFoundException e) { System.out.println(e.getMessage()); }
		 * catch (SecurityException e) { System.out.println(e.getMessage()); } catch
		 * (IOException e) { System.out.println(e.getMessage()); } finally { try {
		 * fileOut.close(); } catch (NullPointerException e) { } }
		 * 
		 */
		// return received;

	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void sendFile(DataOutputStream out, String file) throws IOException {

		boolean sent = false;
		DataInputStream fileIn = null;
		// try {

		// creates a file object to upload it
		File fileToUpload = new File(file);

		// Get the size of the file
		long length = fileToUpload.length();
		byte[] bytes = new byte[4096];

		// We have privates attributes for streams
		fileIn = new DataInputStream(new FileInputStream(fileToUpload));

		// send length
		out.writeLong(length);
		out.flush();
		// copy a stream
		int count = 0;
		while ((count = fileIn.read(bytes)) != -1) {
			out.write(bytes, 0, count);
			out.flush();
		}

		// close stream
		fileIn.close();
		sent = true;

		/**
		 * } catch (FileNotFoundException e) { System.out.println(e.getMessage()); }
		 * catch (SecurityException e) { System.out.println(e.getMessage()); } catch
		 * (IOException e) { System.out.println(e.getMessage()); } finally { try {
		 * fileIn.close(); } catch (NullPointerException e) { }
		 * 
		 * }
		 */
		// return sent;

	}

	/**
	 * 
	 * @return
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
	 * 
	 * @return
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

	public static void previous_dir(String path, String regex) {

		for (int i = path.length() - 1; i >= 0; i--) {
			if (path.substring(i - 1, i).equals(regex)) {
				path = path.substring(0, i - 1);
				break;
			}
		}
	}

	public static void readFile() {
	}

	public static void writeFile() {
	}

	public static String getCommandError() {
		return COMMAND_ERROR;
	}

	public static String getCommandDlZip() {
		return COMMAND_DL_ZIP;
	}

	public static final int getPosFile() {
		return POS_FILE_DIR;
	}

	public static final String getCommandRegex() {
		return COMMAND_REGEX;
	}

	public static final int getPosCommand() {
		return POS_COMMAND;
	}

	public static final int getPosCmdOption() {
		return POS_CMD_OPTION;
	}

	public static String getCommandDownload() {
		return COMMAND_DOWNLOAD;
	}

	public static String getCommandUpload() {
		return COMMAND_UPLOAD;
	}

}
