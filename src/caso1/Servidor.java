package caso1;

public class Servidor extends Thread{
	
	int id;
	
	public Servidor(int pId) {
		id = pId;
	}
	
	public void run(Buffer buffer){
		System.out.println("El servidor "+id+" ha sido creado");
		while (true) {
			System.out.println(1);
			Mensaje mensaje = buffer.leerMsg();
			mensaje.responder();
			mensaje.notify();
		}
	}
}
