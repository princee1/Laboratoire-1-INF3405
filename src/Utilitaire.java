
public class Utilitaire {

	/**
	 * Commande
	 */
	private static final String COMMAND_CD = "cd", COMMAND_CD_DOT = "cd..", COMMAND_LS = "ls", COMMAND_MKDIR = "mkdir",
			COMMAND_DELETE = "delete", COMMAND_UPLOAD = "upload", COMMAND_DOWNLOAD = "download";

	/**
	 * Position des commandes
	 */
	private static final int POS_COMMAND = 0, POS_FILE_DIR = 1;

	private static final String COMMAND_DL_ZIP = "-z";
	private static final String COMMAND_REGEX = " ";

	
	public static String getCommandCd() {
		return COMMAND_CD;
	}

	public static String getCommandCdDot() {
		return COMMAND_CD_DOT;
	}

	public static int getPosFileDir() {
		return POS_FILE_DIR;
	}

	public static String getCommandDelete() {
		return COMMAND_DELETE;
	}

	public static String getCommandDownload() {
		return COMMAND_DOWNLOAD;
	}

	public static String getCommandUpload() {
		return COMMAND_UPLOAD;
	}

	public static String getCommandDlZip() {
		return COMMAND_DL_ZIP;
	}

	public static String getCommandLs() {
		return COMMAND_LS;
	}

	public static String getCommandMkdir() {
		return COMMAND_MKDIR;
	}

	public static String getCommandRegex() {
		return COMMAND_REGEX;
	}

	public static int getPosCommand() {
		return POS_COMMAND;
	}
}
