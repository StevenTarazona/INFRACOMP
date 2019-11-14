
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class Cliente {

	private Socket socket;
	private PrintWriter escritor;
	private BufferedReader lector;

	private X509Certificate certificadoServidor;
	private SecretKey llaveSimetrica;

	private String[] algoritmos;
	private String[] ALGS = { "DES", "AES", "Blowfish" };
	private String[] ALGHMAC = { "HMACSHA1", "HMACSHA256", "HMACSHA384", "HMACSHA512" };

	public void iniciar(int puerto, String servidor) {
		try {
			socket = new Socket((servidor.equals("")?"localhost":servidor), puerto);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// System.out.println("• Etapa 1...");
			etapa1();
			// System.out.println("• Etapa 2...");
			etapa2();
			// System.out.println("• Etapa 3...");
			etapa3();
			// System.out.println("• Etapa 4...");
			etapa4();

			escritor.close();
			lector.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("cerrando...");
		}

	}

	private void etapa1() throws Exception {
		escritor.println("HOLA");
		String fromServer;
		if ((fromServer = lector.readLine()) != null) {
			// System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK")) {
				String line;
				escritor.println(line = algoritmos());
				algoritmos = line.split(":");
			} else
				throw new Exception("");
		} else
			throw new Exception("");
	}

	private void etapa2() throws Exception {
		String fromServer;
		if ((fromServer = lector.readLine()) != null) {
			// System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK") && (fromServer = lector.readLine()) != null) {
				// System.out.println("Certificado del Servidor:+(certificadoServidor = Seguridad.generarCertificado(fromServer)).toString() + "\n");
				certificadoServidor = Seguridad.generarCertificado(fromServer);
				certificadoServidor.checkValidity();

				// System.out.println("Generando y enviando llave simetrica...");
				escritor.println(
						Seguridad.cifrar((llaveSimetrica = Seguridad.generarLlaveSimetrica(algoritmos)).getEncoded(),
								certificadoServidor.getPublicKey(), algoritmos[2], 1));

				String reto = Seguridad.completar("reto");
				escritor.println(reto);
				if ((fromServer = lector.readLine()) != null) {
					String line;
					// System.out.println("Verificando...");
					if (reto.equals(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(fromServer), llaveSimetrica,
							algoritmos[1], 2)))
						escritor.println(line = "OK");
					else
						escritor.println(line = "ERROR");
					// System.out.println(line);
				} else
					throw new Exception("");
			} else
				throw new Exception("");
		} else
			throw new Exception("");
	}

	private void etapa3() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		escritor.println(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(Seguridad.completar("123456789")),
				llaveSimetrica, algoritmos[1], 1));
		escritor.println(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(Seguridad.completar("123456")),
				llaveSimetrica, algoritmos[1], 1));

	}

	private void etapa4() throws Exception {
		String fromServer;
		if ((fromServer = lector.readLine()) != null) {
			String valor = Seguridad.cifrar(DatatypeConverter.parseBase64Binary(fromServer), llaveSimetrica,
					algoritmos[1], 2);

			// System.out.println("Su valor es: " + valor);

			if ((fromServer = lector.readLine()) != null) {
				String line;

				// System.out.println("Verificando...");

				if (Seguridad.hmac(DatatypeConverter.parseBase64Binary(valor), llaveSimetrica, algoritmos[3])
						.equals(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(fromServer),
								certificadoServidor.getPublicKey(), algoritmos[2], 2)))
					escritor.println(line = "OK");
				else
					escritor.println(line = "ERROR");
				// System.out.println(line);
			} else
				throw new Exception("");
		} else
			throw new Exception("");
	}
	
	private String algoritmos() {
		String rta = "ALGORITMOS:" + ALGS[(int) (Math.random() * (ALGS.length - 1))];
		rta += ":RSA:" + ALGHMAC[(int) (Math.random() * (ALGHMAC.length - 1))];
		return rta;
	}

	private static class Seguridad {
		/**
		 * @param algoritmo
		 *            : DES, AES, Blowfish, RSA, ECIES, RC4, HMACMD5, HMACSHA1,
		 *            HMACSHA256, HMACSHA384, HMACSHA512
		 * @param accion
		 *            : 1 (cifrar), 2(decifrar)
		 */
		public static String cifrar(byte[] msg, Key key, String algoritmo, int accion) throws NoSuchAlgorithmException,
				NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
			final Cipher cifrador = Cipher.getInstance(algoritmo);
			cifrador.init(accion, key);
			return DatatypeConverter.printBase64Binary(cifrador.doFinal(msg));
		}

		public static String hmac(final byte[] msg, final Key key, final String algoritmo)
				throws NoSuchAlgorithmException, InvalidKeyException {
			Mac mac = Mac.getInstance(algoritmo);
			mac.init(key);
			return DatatypeConverter.printBase64Binary(mac.doFinal(msg));
		}

		public static X509Certificate generarCertificado(String certificado) throws CertificateException {
			byte[] bytes = DatatypeConverter.parseBase64Binary(certificado);
			return (X509Certificate) CertificateFactory.getInstance("X.509")
					.generateCertificate(new ByteArrayInputStream(bytes));
		}

		public static SecretKey generarLlaveSimetrica(String[] alg) throws NoSuchAlgorithmException {
			return KeyGenerator.getInstance(alg[1]).generateKey();
		}

		public static String completar(String msg) {
			while (msg.length() % 4 != 0)
				msg += '0';
			return msg;
		}
	}
}
