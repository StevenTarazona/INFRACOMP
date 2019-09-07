package caso1;

public class Servidor extends Thread {

	private int id;
	private Buffer buffer;

	public Servidor(int id, Buffer buffer) {
		this.id = id;
		this.buffer = buffer;
	}

	public void run() {
		System.out.println("El servidor " + id + " ha sido creado");
		while (buffer.getnClientes() > 0) {
			Mensaje mensaje = null;
			while (mensaje == null) {
				if (buffer.getnClientes() == 0) {
					System.out.println("El servidor " + id + " ha finalizado");
					return;
				}
				mensaje = buffer.leerMsg();
				yield();
			}
			System.out.println("leido: " + mensaje.getMsg());
			mensaje.responder();
			synchronized (mensaje) {

				mensaje.notify();
			}
		}
		System.out.println("El servidor " + id + " ha finalizado");
	}
}
