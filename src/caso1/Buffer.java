package caso1;

import java.util.LinkedList;
import java.util.Queue;

import javax.sql.rowset.spi.SyncResolver;

public class Buffer {

	private Queue<Mensaje> mensajes;
	private int capacidad;
	public int nClientes;

	public Buffer(int capacidad, int nClientes) {
		mensajes = new LinkedList<Mensaje>();
		this.capacidad = capacidad;
		this.nClientes = nClientes;
	}

	public void enviarMsg(Mensaje mensaje) {
		System.out.println("enviando: "+mensaje.getMsg());
		while (mensajes.size() >= capacidad) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mensajes.add(mensaje);
	}

	public synchronized Mensaje leerMsg() {
		Mensaje mensaje=mensajes.poll();
		if(mensaje!=null)
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
