package ys.tarazona.Caso2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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

import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.encoders.Hex;

public class ProtocoloCliente {

	private static String[] algoritmos;

	public static void procesar(BufferedReader stdIn, BufferedReader pIn,
			PrintWriter pOut) throws IOException, CertificateException {

		etapa1(stdIn, pIn, pOut);
		etapa2(stdIn, pIn, pOut);
		etapa3(stdIn, pIn, pOut);
		etapa4(stdIn, pIn, pOut);
	}

	public static void etapa1(BufferedReader stdIn, BufferedReader pIn,
			PrintWriter pOut) throws IOException {
		System.out.println("Escriba el mensaje para enviar:");
		pOut.println(stdIn.readLine());
		String fromServer = "";
		if ((fromServer = pIn.readLine()) != null) {
			System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK")) {
				System.out
						.println("Seleccione algoritmos: (ALGORITMOS:<ALGS>:<ALGA>:<ALGHMAC>)");
				System.out
						.println("-----------------------------------\nALGS:		DES, AES, Blowfish, RC4\nALGA:		RSA\nALGHMAC:	HMACSHA1, HMACSHA256, HMACSHA384, HMACSHA512\n-----------------------------------");
				String alg = stdIn.readLine();
				pOut.println(alg);
				algoritmos = alg.split(":");
			}
		}
	}

	public static void etapa2(BufferedReader stdIn, BufferedReader pIn,
			PrintWriter pOut) throws IOException, CertificateException {
		pOut.println(stdIn.readLine());
		String fromServer = "";
		if ((fromServer = pIn.readLine()) != null) {
			System.out.println("Respuesta del servidor: " + fromServer);
			if (fromServer.equals("OK")
					&& (fromServer = pIn.readLine()) != null) {
				System.out.println("Certificado del Servidor: "
						+ generarCertificado(fromServer).toString() + "\n");

				System.out.println("Ingrese el reto:");
				pOut.println(stdIn.readLine());
				if ((fromServer = pIn.readLine()) != null)
					System.out.println("Respuesta del servidor: " + fromServer);
			}
		}
	}

	public static void etapa3(BufferedReader stdIn, BufferedReader pIn,
			PrintWriter pOut) {

	}

	public static void etapa4(BufferedReader stdIn, BufferedReader pIn,
			PrintWriter pOut) {

	}

	/**
	 * @param msg
	 * @param key
	 * @param algoritmo
	 *            : DES, AES, Blowfish, RSA, ECIES, RC4, HMACMD5, HMACSHA1,
	 *            HMACSHA256, HMACSHA384, HMACSHA512
	 * @param accion
	 *            : 1 (encriptar), 2(decriptar)
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static byte[] CK(final byte[] msg, final Key key,
			final String algoritmo, int accion)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		final Cipher cifrador = Cipher.getInstance(algoritmo);
		cifrador.init(accion, key);
		return cifrador.doFinal(msg);
	}

	public static X509Certificate generarCertificado(String sertificado)
			throws CertificateException {
		byte[] bytes = DatatypeConverter.parseBase64Binary(sertificado);
		return (X509Certificate) CertificateFactory.getInstance("X.509")
				.generateCertificate(new ByteArrayInputStream(bytes));
	}

	public SecretKey generarLlaveSimetrica() throws NoSuchAlgorithmException {
		return KeyGenerator.getInstance(algoritmos[1]).generateKey();
	}

	public KeyPair generarLlavesAsimetricas() throws NoSuchAlgorithmException {
		KeyPairGenerator llaves = KeyPairGenerator.getInstance(algoritmos[2]);
		llaves.initialize(1024, new SecureRandom());
		return llaves.generateKeyPair();
	}

}