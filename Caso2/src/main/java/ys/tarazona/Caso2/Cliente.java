package ys.tarazona.Caso2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public class Cliente {

	private Socket socket;
	private PrintWriter escritor;
	private BufferedReader lector;
	private BufferedReader stdIn;
	private String[] algoritmos;
	X509Certificate certificadoServidor;

	public Cliente(Socket socket, PrintWriter escritor, BufferedReader lector, BufferedReader stdIn) {
		this.socket = socket;
		this.escritor = escritor;
		this.lector = lector;
		this.stdIn = stdIn;
	}

	public void procesar() throws IOException, CertificateException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		etapa1();
		etapa2();
		etapa3();
		etapa4();
	}

	private void etapa1() throws IOException {
		System.out.println("Escriba el mensaje para enviar:");
		escritor.println(stdIn.readLine());
		String fromServer = "";
		if ((fromServer = lector.readLine()) != null) {
			System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK")) {
				System.out.println("Seleccione algoritmos: (ALGORITMOS:<ALGS>:<ALGA>:<ALGHMAC>)");
				System.out.println(
						"-----------------------------------\nALGS:		DES, AES, Blowfish, RC4\nALGA:		RSA\nALGHMAC:	HMACSHA1, HMACSHA256, HMACSHA384, HMACSHA512\n-----------------------------------");
				String line;
				escritor.println(line=stdIn.readLine());
				algoritmos = line.split(":");
			}
		}
	}

	private void etapa2() throws IOException, CertificateException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String fromServer = "";
		if ((fromServer = lector.readLine()) != null) {
			System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK") && (fromServer = lector.readLine()) != null) {
				System.out.println("Certificado del Servidor: "
						+ (certificadoServidor = generarCertificado(fromServer)).toString() + "\n");
				certificadoServidor.checkValidity();
				

				escritor.println(cifrar(generarLlaveSimetrica().getEncoded(), certificadoServidor.getPublicKey(),algoritmos[2], 1));

				System.out.println("Ingrese el reto:");
				escritor.println(stdIn.readLine());
				if ((fromServer = lector.readLine()) != null)
					System.out.println("Respuesta del servidor: " + fromServer);
				if ((fromServer = lector.readLine()) != null)
					System.out.println("Respuesta del servidor: " + fromServer);
				else
					System.out.println("fuck");
			}
		}
	}

	private void etapa3() {

	}

	private void etapa4() {

	}

	/**
	 * @param msg
	 * @param key
	 * @param algoritmo : DES, AES, Blowfish, RSA, ECIES, RC4, HMACMD5, HMACSHA1,
	 *                  HMACSHA256, HMACSHA384, HMACSHA512
	 * @param accion    : 1 (encriptar), 2(decriptar)
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] cifrar(byte[] msg, Key key, String algoritmo, int accion) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		final Cipher cifrador = Cipher.getInstance(algoritmo);
		cifrador.init(accion, key);
		return cifrador.doFinal(msg);
	}

	private X509Certificate generarCertificado(String sertificado) throws CertificateException {
		byte[] bytes = DatatypeConverter.parseBase64Binary(sertificado);
		return (X509Certificate) CertificateFactory.getInstance("X.509")
				.generateCertificate(new ByteArrayInputStream(bytes));
	}

	private SecretKey generarLlaveSimetrica() throws NoSuchAlgorithmException {
		return KeyGenerator.getInstance(algoritmos[1]).generateKey();
	}

	private KeyPair generarLlavesAsimetricas() throws NoSuchAlgorithmException {
		KeyPairGenerator llaves = KeyPairGenerator.getInstance(algoritmos[2]);
		llaves.initialize(1024, new SecureRandom());
		return llaves.generateKeyPair();
	}
}
