import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.FileAlreadyExistsException;
import java.time.Instant;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class Client {
	/**
	 * Variable de connection
	 */
	private static Socket socket;
	private static DataOutputStream out;
	private static DataInputStream in;
	private static String dossierTemp = "Rï¿½pertoire actuelle: ";
	private static String currDirectory = System.getProperty("user.dir");

	private static int port;
	private static String serverAddress;

	private static boolean quitter = true, erreur, connected;
	private static Thread mainThread;
	private static final long period = 3000;
	private static final long maxDelayReconnection = 1000 * 60 * 2;

	private static Timer timerDeconnection;

	public static void main(String arg[]) throws Exception {
		// serverAddress = Utilitaire.ipAdress_validation();
		// port = Utilitaire.port_validation();

		serverAddress = "127.0.0.1";
		port = 5000;
		mainThread = Thread.currentThread();
		// socket = new Socket();
		// safeDeconnection();

		connection();
		// verfierConnection();

		try {

			do {
				verifier_enoie_Command();
			} while (quitter);

		} catch (NullPointerException e) {
			// si le socket n'est pas connectï¿½

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Erreur intrétable: " + e.getMessage());
			System.out.println("\nFermeture brusque");

		} finally {
			// TODO: deconnecter
			deconnection();
		}
	}

	/**
	 * 
	 */
	private static void safeDeconnection() {
		timerDeconnection = new Timer(true);
		timerDeconnection.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (!mainThread.isAlive()) {
					try {
						Client.socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block

					}

				}

			}
		}, Date.from(Instant.now()), 1000);
	}

	/**
	 * 
	 */
	private static void deconnection() {
		System.out.println("deconnection...\n");
		try {
			Client.socket.close();
		} catch (NullPointerException e) {
			System.out.println("Already deconnected... : " + e.getMessage());
		} catch (SocketException e) {
			// TODO: handle exception
			System.out.println("Couldnt close the socket " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} finally {
			System.out.println("Au revoir");
			System.exit(0);
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
	 * Vérifie la commande du client. Dans le cas ou la commande est bonne, elle
	 * est envoyé au serveur
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
						if (new File(currDirectory + "\\" + tab[Utilitaire.getPosFile()]).exists()) {
							throw new FileAlreadyExistsException(tab[Utilitaire.getPosFile()],
									tab[Utilitaire.getPosFile()],
									"The file entered already exist in the current directory\n\tThe file would've been overwritten...");
						}
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
					else if (!new File(tab[Utilitaire.getPosFile()]).isFile()) {
						throw new FileNotFoundException("Erreur: File not found");
					}
					break;
				case "-q":
					erreur = false;
					quitter = false;
					break;
				default:
					System.out.println("\tErreur pour la command: " + tab[Utilitaire.getPosCommand()]);
					erreur = true;
					break;
				}

				envoieCommand(command);

			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("\tErreur rien d'entrer: " + e.getMessage());
				erreur = true;
			} catch (CmdException e) {
				System.out.println(e.getMessage());
				erreur = true;
			} catch (ConnectException e) {
				connected = false;
				System.out.println("\t" + e.getMessage());
			} catch (SocketException e) {
				// TODO reset connection
				// System.out.println("\t" + e.getMessage());
				connected = false;
				reconnection(e.getMessage());
			} catch (FileNotFoundException e) {
				System.out.println("\t" + e.getMessage());
				erreur = true;
			} catch (FileAlreadyExistsException e) {
				System.out.println("\t" + e.getMessage());
				erreur = true;
			} catch (SecurityException e) {
				System.out.println("\tErreur: " + e.getMessage());
				 erreur=true;
			} catch (IOException e) {
				System.out.println("\tErreur: " + e.getMessage());
				erreur = false;
				quitter = false;
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
				System.out.println("Retrying a new connection with the server");
				connection();
				if (Client.connected) {
					Client.mainThread.resume();
					Client.printCurrentDirectory();// TODO pourquoi reappler la methode?
					timer.cancel();
				} else if ((Date.from(Instant.now()).getTime()
						- startReconnection.getTime()) >= Client.maxDelayReconnection) {
					System.out.println("Couldnt establish a connection");
					deconnection();
					// System.exit(0);// TODO trouver une autre facon de quitter
				}
			}
		}, Date.from(Instant.now()), Client.period);
	}

	/**
	 * 
	 * @param command
	 * @throws IOException
	 */
	private static void envoieCommand(String command) throws IOException {
		if (!erreur) {

			out.writeUTF(command); // envoie de commande
			try {

				if (command.split(Utilitaire.getCommandRegex())[Utilitaire.getPosCommand()].equals("upload")) {
					Utilitaire.sendFile(out, command.split(Utilitaire.getCommandRegex())[1]);
				} else if (command.split(Utilitaire.getCommandRegex())[Utilitaire.getPosCommand()].equals("download")) {
					if (in.readBoolean())
						Utilitaire.receiveFile(in,
								currDirectory + "\\" + command.split(Utilitaire.getCommandRegex())[1]);
				}
				if (quitter)
					System.out.println(in.readUTF());
			} catch (SocketException e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				deconnection();// TODO: déconnecter ou quitter la boucle
			}catch(SecurityException e) {
				deconnection();// TODO: déconnecter ou quitter la boucle
			}

		}
	}

	/**
	 * Affiche le rï¿½pertoire oï¿½ se situe le client dans le serveur
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
			// socket.connect(new InetSocketAddress(serverAddress, port), 0);
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
			System.out.println("I/O error: " + e.getMessage());
			deconnection();
		}
	}

}
