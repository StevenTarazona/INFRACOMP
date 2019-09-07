package caso1;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {

	private Queue<Mensaje> mensajes;
	private int capacidad;
	public int nClientes;

	public Buffer(int capacidad, int nClientes) {
		mensajes = new LinkedList<Mensaje>();
		this.capacidad = capacidad;
		this.nClientes = nClientes;
	}

	public synchronized void enviarMsg(Mensaje mensaje) {
		// System.out.println("enviando: " + mensaje.getMsg());
		while (mensajes.size() >= capacidad) {
			// System.out.println("Ël buffer se encuentra lleno");
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mensajes.add(mensaje);
	}

	public synchronized Mensaje leerMsg() {
		Mensaje mensaje = mensajes.poll();
		if (mensaje != null)
			notify();
		return mensaje;
	}

	public synchronized void sacarCliente() {
		nClientes--;
	}

	public int getnClientes() {
		return nClientes;
	}

}
