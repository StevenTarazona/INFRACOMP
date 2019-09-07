package caso1;

public class Mensaje {

	private int msg;

	public Mensaje(int msg) {
		this.msg = msg;
	}

	public void enviar(Buffer buffer) {
		buffer.enviarMsg(this);

		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void fin(Buffer buffer) {
		buffer.sacarCliente();
	}

	public void responder() {
		msg++;
	}

	public int getMsg() {
		return msg;
	}

}
