import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Client {
	private static Socket socket;
	private static boolean quitter = true;
	private static DataOutputStream out;
	private static DataInputStream in;
	

	public static void main(String arg[]) throws Exception {

		 //String serverAddress = Utilitaire.ipAdress_validation();
		 //int port = Utilitaire.port_validation();

		String serverAddress = "127.0.0.1";
		int port = 5000;
		

		socket = new Socket(serverAddress, port);
		System.out.println(socket);
		

		System.out.format("The server is running on %s:%d%n", serverAddress, port);

		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
		
		

		try {

			do {
				verifier_envoie_Command();
			} while (quitter);

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("\nFermeture brusque");

		} finally {
			// TODO deconnecter
			System.out.println("Au revoir");
		}

		Client.socket.close();

	}

	
	/**
	 * Transforme l'entrée du client en une commande que le serveur peut traiter.
	 * 
	 * @return une commande.
	 */
	public static String clientEntry_toCommand() {
		System.out.print("Repertoire actuelle : "); // TODO affichier le répertoire actuelle
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
	 * Vérifie la commande du client. Dans le cas ou la commande est bonne, elle est
	 * envoyé au serveur
	 */
	private static void verifier_envoie_Command() {

		// String cd=new String(Utilitaire.getCommandCd());

		boolean erreur;
		do {
			erreur = false;
			String command = clientEntry_toCommand();
			String tab[] = command.split(Utilitaire.getCommandRegex());

			// tab[Utilitaire.getPosCommand()]=
			// tab[Utilitaire.getPosCommand()].toLowerCase();
			// tab[Utilitaire.getPosCmdOption()]=tab[Utilitaire.getPosCmdOption()].toLowerCase();

			try {
				switch (tab[Utilitaire.getPosCommand()]) {

				case "cd":
					if (tab.length != 2)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "cd..":
					if (tab.length != 1)
						throw new WrongLgthCmdException(tab[Utilitaire.getPosCommand()]);
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
					}

					else
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
					erreur =false;
					quitter = false;
					break;
				default:
					System.out.println("\tErreur pour la command: " + tab[0]);
					erreur = true;
					break;

				}

				if (!erreur) {
					// TODO envoyer la commande
					// ce que le serveur afficher apres la commande
					// printCommand(command);

					out.writeUTF(command);
					
					 System.out.println(in.readUTF());
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("\tErreur rien d'entrer: " + e.getMessage());
				erreur = true;
			} catch (WrongLgthCmdException e) {
				System.out.println(e.getMessage());
				erreur = true;
			} // catch (Exception e) {System.out.println("\tErrer: " + e.getMessage()); }
			catch (IOException e) {
				System.out.println("\tErreur: " + e.getMessage());
			}
		} while (erreur);
	}

	
}
