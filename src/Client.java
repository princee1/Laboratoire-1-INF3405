import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Client {
	private static Socket socket;
	private static boolean quitter = true;
	private static DataOutputStream out;
	private static DataInputStream in;
	private static String dossierTemp = "Répertoire actuelle: ";
	private static boolean erreur;
	private static boolean stripPath = false;
	private static boolean append = true;

	public static void main(String arg[]) throws Exception {

		connection();

		try {

			do {
				verifier_Command();
			} while (quitter);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("\nFermeture brusque");

		} finally {
			// TODO deconnecter
			System.out.println("Au revoir");
			Client.socket.close();

		}

	}

	/**
	 * Transforme l'entr�e du client en une commande que le serveur peut traiter.
	 * 
	 * @return une commande.
	 */
	private static String clientEntry_toCommand() {
		// System.out.print("Repertoire actuelle : "); // TODO affichier le r�pertoire
		// actuelle
		String command = new Scanner(System.in).nextLine().strip();

		StringTokenizer stringTokenizer = new StringTokenizer(command, " ", false);
		int cpt = 0;
		int count = stringTokenizer.countTokens();

		String token = "";

		while (stringTokenizer.hasMoreTokens() && cpt < count) {
			// TODO Rendre la command et la cmd option lowerCase seulement !
			if (cpt == Utilitaire.getPosFile())
				token += stringTokenizer.nextToken() + Utilitaire.getCommandRegex();
			else
				token += stringTokenizer.nextToken().toLowerCase() + Utilitaire.getCommandRegex();

			cpt++;
		}

		return token;
	}

	/**
	 * V�rifie la commande du client. Dans le cas ou la commande est bonne, elle
	 * est envoy� au serveur
	 */
	private static void verifier_Command() {

		// String cd=new String(Utilitaire.getCommandCd());

		do {

			printCurrentDirectory();
			erreur = false;
			String command = clientEntry_toCommand();
			String tab[] = command.split(Utilitaire.getCommandRegex());

			try {
				switch (tab[Utilitaire.getPosCommand()]) {

				case "cd":
					if (tab.length != 2)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "cd..":
					if (tab.length != 1)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					else
						break;
				case "delete":
					if (tab.length != 2)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "download":
					if (tab.length == 2 || tab.length == 3) {

						if (tab.length == 3 && !tab[2].equals(Utilitaire.getCommandDlZip())) {
							System.out.println("\tErreur\n\tOption zip doit etre : " + Utilitaire.getCommandDlZip());
							erreur = true;
						}
					} else
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "ls":
					if (tab.length != 1)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "mkdir":
					if (tab.length != 2)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "upload":
					if (tab.length != 2)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "-q":
					erreur = false;
					quitter = false;
					break;
				default:
					System.out.println("\tErreur pour la command: " + tab[0]);
					erreur = true;
					break;
				}

//				if (tab[Utilitaire.getPosCommand()].equals("cd")) {
//					append = true;
//				} else
//					append = false;
//				
				envoie_reception(command);

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("\tErreur rien d'entrer: " + e.getMessage());
				erreur = true;
			} catch (WrongLgthCmdException e) {
				System.out.println(e.getMessage());
				erreur = true;
			} catch (SocketException e) {
				// TODO reset connection
			}
			// catch (Exception e) {System.out.println("\tErrer: " + e.getMessage()); }
			catch (IOException e) {
				System.out.println(e.getClass());
				System.out.println("\tErreur: " + e.getMessage());
			}
		} while (erreur);
	}

	private static void envoie_reception(String command) throws IOException {
		if (!erreur) {
			out.writeUTF(command); // envoie de commande

			if (command.equals(Utilitaire.getCommandCdDot())) {
				if (!in.readBoolean()) {
					System.out.println(in.readUTF());
					stripPath = false;
				} else {
					stripPath = true;
				}
			} else {
				System.out.println(in.readUTF());
			}
		}
	}

	private static void printCurrentDirectory() {
		try {
			// System.out.println(stripPath);
			if (!erreur) {
				// TODO append le dossier afficheer
				dossierTemp=in.readUTF();
//				if (append)
//					dossierTemp += in.readUTF() + ">";
//				else {
//					if (stripPath)
//						Utilitaire.previous_dir(dossierTemp, ">");
//				}
			}
			System.out.print(dossierTemp + " ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur reception du dossier");
		}
	}

	private static void connection() {
		// String serverAddress = Utilitaire.ipAdress_validation();
		// int port = Utilitaire.port_validation();

		String serverAddress = "127.0.0.1";
		int port = 5000;
		try {
			socket = new Socket(serverAddress, port);
			// System.out.println(socket);

			System.out.format("The server is running on %s:%d%n", serverAddress, port);
			System.out.println("");

			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (ConnectException e) {
			// TODO: handle exception

		} catch (IOException e) {
		}
	}

}
