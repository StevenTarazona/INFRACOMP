package caso1;

import java.util.LinkedList;
import java.util.Queue;

import javax.sql.rowset.spi.SyncResolver;

public class Buffer {

	private Queue<Mensaje> mensajes;
	private int capacidad;
	private int nClientes;

	public Buffer(int capacidad, int nClientes) {
		mensajes = new LinkedList<Mensaje>();
		this.capacidad = capacidad;
		this.nClientes = nClientes;
	}

	public synchronized void enviarMsg(Mensaje mensaje) {
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
		while (mensajes.size() <= 0){
			System.out.println(2);
			//Thread.yield();
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mensajes.poll();
	}

	public void sacarCliente() {
		nClientes--;
	}
}
