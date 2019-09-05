package caso1;

public class Cliente extends Thread{
	
	int id;
	int nMensajes;
	
	
	public Cliente(int pId, int nMsg) {
		id = pId;
		nMensajes = nMsg;
	}
	
	public void run(Buffer buffer){
		System.out.println("El cliente "+id+" ha sido creado");
		for (int i = 0; i < nMensajes; i++) {
			int numero = (int)(Math.random()*(0-1000+1)+1000);
			Mensaje mensaje = new Mensaje(numero);
			mensaje.enviar(buffer);
			try {
				mensaje.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Mensaje mensaje = new Mensaje(0);
		mensaje.fin(buffer);
		System.out.println("El cliente "+id+" ha salido");
		
	}
}
