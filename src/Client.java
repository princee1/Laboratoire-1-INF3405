import java.io.DataInputStream;
import java.net.Socket;
import java.util.Scanner;


public class Client {
private static Socket socket;
	public static void main(String arg[]) throws Exception {
	
		Scanner scannerIn = new Scanner(System.in);
		System.out.println("Adresse Ip avec le format 4 octets XXX.XXX.XX.XXX?");
		String serverAddress = scannerIn.nextLine();
		System.out.println("Port Ip?");
		int port=scannerIn.nextInt();
		
		
		//String serverAddress= "127.0.0.1";
		//int port=5000;
		
		socket = new Socket(serverAddress, port);
		
		System.out.format("The server is running on %s:%d%n", serverAddress, port);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		String helloMessageFromServer = in.readUTF();
		System.out.println(helloMessageFromServer);
		
		socket.close();
		
	}
}
