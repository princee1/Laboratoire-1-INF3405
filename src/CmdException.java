
public class CmdException extends Exception {

	private String command;

	public CmdException(String command) {
		super("\tErreur dans la taille de la commande : " + command);
		this.command = command;

	}

	@Override
	public String getMessage() {
		String message = super.getMessage()+"\n\tTry: ";
		switch (command) {
			case "cd":
				return message += "cd <Nom d'un répertoire sur le serveur>";
			case "ls":
				return message += "ls";
			case "mkdir":
				return message += "mkdir <Nom du nouveau dossier>";
			case "delete":
				return message += "Delete <Nom du dossier | Nom du fichier>";
			case "upload":
				return message += "upload <Nom du fichier>";
			case "download":
				return message += "download <Nom du fichier> <-z>";
			case "cd..":
				return message += "cd..";

		}

		return super.getMessage();
	}

}
