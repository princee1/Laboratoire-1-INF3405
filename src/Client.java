import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.print.CancelablePrintJob;

public class Client {
	/**
	 * Variable de connection
	 */
	private static Socket socket;
	private static DataOutputStream out;
	private static DataInputStream in;
	private static int port;
	private static String serverAddress;

	private static String dossierTemp = "Répertoire actuelle: ";
	private static boolean quitter = true, erreur, connected;
	private static Thread mainThread;
	private static final long period = 3000;
	private static final long maxDelayReconnection = 1000 * 60 * 2;

	public static void main(String arg[]) throws Exception {
		// serverAddress = Utilitaire.ipAdress_validation();
		// port = Utilitaire.port_validation();

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

			try {
				String command = clientEntry_toCommand();
				if (command.equals(Utilitaire.getCommandError()))
					throw new CmdException("cd..");

				String tab[] = command.split(Utilitaire.getCommandRegex());

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
				connected = false;
				connection();
				reconnection(e.getMessage());
				// System.out.println("Connection established!\n ");
				// erreur =false;
			} catch (IOException e) {
				System.out.println(e.getClass());
				System.out.println("\tErreur: " + e.getMessage());

			} 
		} while (erreur);
	}

	/**
	 * Permet de se reconnecter apres une erreur de connection au server
	 * 
	 * @param errorMessage: Message d'erreur de l'exception
	 */
	private static void reconnection(String errorMessage) {
		System.out.println("\tErreur de connection: " + errorMessage);
		System.out.println("\tReseting connection...\n");

		Date startReconnection = Date.from(Instant.now());
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Client.mainThread.suspend();
				System.out.println("Retrying a connection with the server");
				connection();
				if (Client.connected) {
					Client.mainThread.resume();
					Client.printCurrentDirectory();// TODO pourquoi reappler la methode?
					timer.cancel();
				} else if ((Date.from(Instant.now()).getTime()
						- startReconnection.getTime()) >= Client.maxDelayReconnection) {
					System.out.println("Couldnt establish a connection");
					System.exit(0);// TODO trouver une autre facon de quitter
				}
			}
		}, Date.from(Instant.now()), Client.period);

	}

	/**
	 * Affiche le répertoire où se situe le client dans le serveur
	 */
	private static void printCurrentDirectory() {
		try {
			if (!erreur) {
				dossierTemp = in.readUTF();
			}
			System.out.print(dossierTemp + "> ");
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
			System.out.println("Connection established!\n ");

			System.out.format("The server is running on %s:%d%n", serverAddress, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			connected = true;
		} catch (ConnectException e) {
			// TODO: handle exception
			connected = false;
			System.out.println("Connection Error");
		} catch (IOException e) {

		} catch (Exception e) {

		}
	}

}
