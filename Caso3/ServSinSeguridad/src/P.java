

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class P {
	private static ServerSocket ss;	
	private static final String MAESTRO = "MAESTRO: ";
	private static X509Certificate certSer; /* acceso default */
	private static KeyPair keyPairServidor; /* acceso default */
	private static int nPool = 2;
	public static int perdidas = 0;
	
	public static synchronized void perdidas() {
		perdidas++;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		System.out.println(MAESTRO + "Establezca puerto de conexion:");
		int ip = Integer.parseInt(br.readLine());
		
		System.out.println(MAESTRO + "Establezca el numero de threads:");
		nPool = Integer.parseInt(br.readLine());
		
		System.out.println(MAESTRO + "Empezando servidor maestro en puerto " + ip);
		// Adiciona la libreria como un proveedor de seguridad.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		

		// Crea el archivo de log
		File file = null;
		keyPairServidor = S.grsa();
		certSer = S.gc(keyPairServidor);
		String ruta = "./resultados.txt";
   
        file = new File(ruta);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
        fw.close();
        
        D.init(certSer, keyPairServidor, file);
        
        ExecutorService pool = Executors.newFixedThreadPool(nPool); 
        
		// Crea el socket que escucha en el puerto seleccionado.
		ss = new ServerSocket(ip);
		System.out.println(MAESTRO + "Socket creado.");
		

		BufferedWriter writer = null;
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		System.out.println(MAESTRO + "Establezca nombre del monitor:");
		String nombre = br.readLine();
        File logFile = new File(System.getProperty("user.dir")+"/"+(nombre.equals("")?timeLog:nombre)+".csv");
        writer = new BufferedWriter(new FileWriter(logFile));
        writer.write("Delegado;Tiempo de respuesta;Uso de CPU;Transacciones perdidas");
        writer.close();
        
        
        int i = 0;
		while(true) {
			try {
				writer = new BufferedWriter(new FileWriter(logFile, true));
				Socket sc = ss.accept();
				System.out.println(MAESTRO + "Cliente " + i + " aceptado.");
				D d = new D(sc,i,writer);
				i++;
				pool.execute(d);
			} catch (IOException e) {
				perdidas();
				pool.shutdown();
				System.out.println(MAESTRO + "Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
		
	}
}
