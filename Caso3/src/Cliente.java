

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class Cliente {

	private PrintWriter escritor;
	private BufferedReader lector;
	private BufferedReader stdIn;
	private static String[] algoritmos;
	private X509Certificate certificadoServidor;
	private SecretKey llaveSimetrica;

	public Cliente(PrintWriter escritor, BufferedReader lector, BufferedReader stdIn) {
		this.escritor = escritor;
		this.lector = lector;
		this.stdIn = stdIn;
	}

	public void procesar() {
		System.out.println("â€¢ Etapa 1...");
		try {
			etapa1();
			System.out.println("• Etapa 2...");
			etapa2();
			System.out.println("• Etapa 3...");
			etapa3();
			System.out.println("• Etapa 4...");
			etapa4();
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("cerrando...");
		}
	}

	private void etapa1() throws Exception {
		System.out.println("Escriba el mensaje para enviar: (HOLA)");
		escritor.println(stdIn.readLine());
		String fromServer;
		if ((fromServer = lector.readLine()) != null) {
			System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK")) {
				System.out.println("Ingrese algoritmos: (ALGORITMOS:<ALGS>:<ALGA>:<ALGHMAC>)");
				System.out.println(
						"-----------------------------------\nALGS:		DES, AES, Blowfish, RC4\nALGA:		RSA\nALGHMAC:	HMACSHA1, HMACSHA256, HMACSHA384, HMACSHA512\n-----------------------------------");
				String line;
				escritor.println(line = stdIn.readLine());
				algoritmos = line.split(":");
			} else
				throw new Exception("");
		} else
			throw new Exception("");
	}

	private void etapa2() throws Exception {
		String fromServer;
		if ((fromServer = lector.readLine()) != null) {
			System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK") && (fromServer = lector.readLine()) != null) {
				System.out.println("Certificado del Servidor: "
						+ (certificadoServidor = Seguridad.generarCertificado(fromServer)).toString() + "\n");
				certificadoServidor.checkValidity();

				System.out.println("Generando y enviando llave simetrica...");
				escritor.println(Seguridad.cifrar((llaveSimetrica = Seguridad.generarLlaveSimetrica()).getEncoded(),
						certificadoServidor.getPublicKey(), algoritmos[2], 1));

				System.out.println("Ingrese el reto:");
				String reto = Seguridad.completar(stdIn.readLine().replace(" ", ""));
				escritor.println(reto);
				if ((fromServer = lector.readLine()) != null) {
					String line;
					System.out.println("Verificando...");
					if (reto.equals(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(fromServer), llaveSimetrica,
							algoritmos[1], 2)))
						escritor.println(line = "OK");
					else
						escritor.println(line = "ERROR");
					System.out.println(line);
				} else
					throw new Exception("");
			} else
				throw new Exception("");
		} else
			throw new Exception("");
	}

	private void etapa3() throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		System.out.println("Ingrese el numero de cedula:");
		escritor.println(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(Seguridad.completar(stdIn.readLine())),
				llaveSimetrica, algoritmos[1], 1));
		System.out.println("Ingrese la clave:");
		escritor.println(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(Seguridad.completar(stdIn.readLine())),
				llaveSimetrica, algoritmos[1], 1));
	}

	private void etapa4() throws Exception {
		String fromServer;
		if ((fromServer = lector.readLine()) != null) {
			String valor = Seguridad.cifrar(DatatypeConverter.parseBase64Binary(fromServer), llaveSimetrica,
					algoritmos[1], 2);

			System.out.println("Su valor es: " + valor);

			if ((fromServer = lector.readLine()) != null) {
				String line;

				System.out.println("Verificando...");

				if (Seguridad.hmac(DatatypeConverter.parseBase64Binary(valor), llaveSimetrica, algoritmos[3])
						.equals(Seguridad.cifrar(DatatypeConverter.parseBase64Binary(fromServer),
								certificadoServidor.getPublicKey(), algoritmos[2], 2)))
					escritor.println(line = "OK");
				else
					escritor.println(line = "ERROR");
				System.out.println(line);
			} else
				throw new Exception("");
		} else
			throw new Exception("");
	}

	private static class Seguridad {
		/**
		 * @param algoritmo : DES, AES, Blowfish, RSA, ECIES, RC4, HMACMD5, HMACSHA1,
		 *                  HMACSHA256, HMACSHA384, HMACSHA512
		 * @param accion    : 1 (cifrar), 2(decifrar)
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

		public static SecretKey generarLlaveSimetrica() throws NoSuchAlgorithmException {
			return KeyGenerator.getInstance(algoritmos[1]).generateKey();
		}

		public static String completar(String msg) {
			while (msg.length() % 4 != 0)
				msg += '0';
			return msg;
		}
	}
}
