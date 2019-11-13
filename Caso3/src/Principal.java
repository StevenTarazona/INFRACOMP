

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Principal {

	public static int PUERTO;
	public static final String SERVIDOR = "localhost";
	public static final String JAR = "servidor20192.jar";
	public static Process servidor;
	public static Socket socket;
	public static PrintWriter escritor;
	public static BufferedReader lector;
	public static BufferedReader stdIn;

	private static void inicializarServidor() throws IOException {
		servidor = Runtime.getRuntime().exec(
				"cmd.exe /c start cmd.exe /k \"java -jar \"" + System.getProperty("user.dir") + "\\" + JAR + "\"");
		System.out.println("Eztablezca puerto de conexion (primero en el CMD):");
		PUERTO = Integer.parseInt(stdIn.readLine());
	}

	private static void inicializar() throws UnknownHostException, IOException {
		socket = new Socket(SERVIDOR, PUERTO);
		escritor = new PrintWriter(socket.getOutputStream(), true);
		lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	private static void finalizar() throws IOException {
		stdIn.close();
		escritor.close();
		lector.close();
		socket.close();
	}

	public static void main(String[] args) {

		try {

			stdIn = new BufferedReader(new InputStreamReader(System.in));

			inicializarServidor();

			inicializar();

			Cliente cliente = new Cliente(escritor, lector, stdIn);

			cliente.procesar();

			finalizar();
		} catch (IOException e) {
			e.printStackTrace();
		}
		servidor.destroy();
		try {
			Runtime.getRuntime().exec("taskkill /f /im java.exe");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
