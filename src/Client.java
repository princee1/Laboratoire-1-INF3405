import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Date;
import java.time.Instant;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
	private static Socket socket;
	private static boolean quitter = true, erreur;
	private static DataOutputStream out;
	private static DataInputStream in;
	private static String dossierTemp = "Répertoire actuelle: ";

	private static int port;
	private static String serverAddress;
	private static Thread mainThread;

	public static void main(String arg[]) throws Exception {
		// String serverAddress = Utilitaire.ipAdress_validation();
		// int port = Utilitaire.port_validation();

		serverAddress = "127.0.0.1";
		port = 5000;
		mainThread = Thread.currentThread();
		connection();

		try {

			do {
				verifier_enoie_Command();
			} while (quitter);

		} catch (NullPointerException e) {
			// si le socket n'est pas connecté
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getClass() + e.getMessage());
			System.out.println("\nFermeture brusque");

		} finally {
			// TODO deconnecter
			System.out.println("Au revoir");
			if (socket != null)
				Client.socket.close();

		}

	}

	/**
	 * Transforme l'entrée du client en une commande que le serveur peut traiter.
	 * 
	 * @return une commande.
	 */
	private static String clientEntry_toCommand() {
		// System.out.print("Repertoire actuelle : "); // TODO affichier le rï¿½pertoire
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
	 * Vérifie la commande du client. Dans le cas ou la commande est bonne, elle est
	 * envoyï¿½ au serveur
	 * 
	 * @throws IOException
	 */
	private static void verifier_enoie_Command() {
		do {
			printCurrentDirectory();
			erreur = false;

			String command = clientEntry_toCommand();
			String tab[] = command.split(Utilitaire.getCommandRegex());

			try {
				switch (tab[Utilitaire.getPosCommand()]) {

				case "cd":
					if (tab.length != 2)
						throw new CmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "cd..":
					if (tab.length != 1)
						throw new CmdException(tab[Utilitaire.getPosCommand()]);
					else
						break;
				case "delete":
					if (tab.length != 2)
						throw new CmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "download":
					if (tab.length == 2 || tab.length == 3) {

						if (tab.length == 3 && !tab[2].equals(Utilitaire.getCommandDlZip())) {
							System.out.println("\tErreur\n\tOption zip doit etre : " + Utilitaire.getCommandDlZip());
							erreur = true;
						}
					} else
						throw new CmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "ls":
					if (tab.length != 1)
						throw new CmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "mkdir":
					if (tab.length != 2)
						throw new CmdException(tab[Utilitaire.getPosCommand()]);
					break;
				case "upload":
					if (tab.length != 2)
						throw new CmdException(tab[Utilitaire.getPosCommand()]);
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

				if (!erreur) {
					out.writeUTF(command); // envoie de commande
					if (!tab[Utilitaire.getPosCommand()].equals(Utilitaire.getQuit()))
						System.out.println(in.readUTF());
				}

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("\tErreur rien d'entrer: " + e.getMessage());
				erreur = true;
			} catch (CmdException e) {
				System.out.println(e.getMessage());
				erreur = true;
			} catch (SocketException e) {
				// TODO reset connection
				//System.out.println(e.getClass() + e.getMessage());
				
			} catch (IOException e) {
				System.out.println(e.getClass());
				System.out.println("\tErreur: " + e.getMessage());

			} catch (Exception e) {

				System.out.println("\tErrer intretable: " + e.getMessage());
				quitter = true;
			}
		} while (erreur);
	}

	private static void reconnection(String e) {
		System.out.println("\tErreur de connection: " + e);
		System.out.println("\tReseting connection...\n");

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Client.mainThread.suspend();
				System.out.println("Retrying a connection with the server");
				connection();

				if (Client.socket.isConnected()) {
					Client.mainThread.resume();
					timer.purge();
					timer.cancel();
				}
			}
		}, Date.from(Instant.now()), 2000);
	}

	/**
	 * Affiche le répertoire où se situe le client dans le serveur
	 */
	private static void printCurrentDirectory() {
		try {
			if (!erreur) {
				dossierTemp = in.readUTF();
			}
			System.out.print(dossierTemp + ": ");
		} catch (SocketException e) {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Erreur reception du dossier");
			// TODO quitter ou une reconnextion
		}
	}

	/**
	 * Permet de se connecter au server
	 */
	private static void connection() {

		try {
			socket = new Socket(serverAddress, port);
			// socket.setReuseAddress(true);
			// System.out.println(socket);
			System.out.format("The server is running on %s:%d%n", serverAddress, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());

		} catch (ConnectException e) {
			// TODO: handle exception
			System.out.println("Connection Error");
		} catch (IOException e) {

		}catch(Exception e) {
			
		}
	}

}
