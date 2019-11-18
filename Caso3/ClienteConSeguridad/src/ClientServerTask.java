import uniandes.gload.core.Task;

public class ClientServerTask extends Task {

	private int puerto;
	private String servidor;

	public ClientServerTask(int pPuerto, String pServidor) {
		puerto = pPuerto;
		servidor = pServidor;
	}

	@Override
	public void execute() {
		Cliente cliente = new Cliente();
		try {
			cliente.iniciar(puerto, servidor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		System.out.println(Task.OK_MESSAGE);

	}
}
