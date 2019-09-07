package caso1;

public class Cliente extends Thread {

	private int id;
	private int nMensajes;
	private Buffer buffer;

	public Cliente(int id, int nMsg, Buffer buffer) {
		this.id = id;
		this.nMensajes = nMsg;
		this.buffer = buffer;
	}

	public void run() {
		System.out.println("El cliente " + id + " ha sido creado con " + nMensajes + " mensajes");
		for (int i = 0; i < nMensajes; i++) {
			int numero = (int) (Math.random() * (0 - 1000 + 1) + 1000);
			Mensaje mensaje = new Mensaje(numero);
			System.out.println("creado: " + numero);
			mensaje.enviar(buffer);
			System.out.println("enviado: " + numero);
		}
		Mensaje mensaje = new Mensaje(0);
		mensaje.fin(buffer);
		System.out.println("El cliente " + id + " ha salido");

	}
}
