

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Principal {

	private int PUERTO;
	private static final String SERVIDOR = "localhost";
	private Socket socket;
	private PrintWriter escritor;
	private BufferedReader lector;

	private void inicializar() throws UnknownHostException, IOException {
		socket = new Socket(SERVIDOR, PUERTO);
		escritor = new PrintWriter(socket.getOutputStream(), true);
		lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		System.out.println(socket);
		System.out.println(escritor);
		System.out.println(lector);
	}

	private void finalizar() throws IOException {
		escritor.close();
		lector.close();
		socket.close();
	}

	public void iniciar(int puerto) throws UnknownHostException, IOException {

			PUERTO = puerto;
			inicializar();
			
			Cliente cliente = new Cliente(escritor, lector);
			cliente.procesar();
			finalizar();
	}
}
