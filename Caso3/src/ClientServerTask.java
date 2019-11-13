import uniandes.gload.core.Task;

public class ClientServerTask extends Task{
	
	private int puerto;
	
	public ClientServerTask(int pPuerto) {
		puerto = pPuerto;
	}

	@Override
	public void execute() {
		Principal principal = new Principal();
		try {
			principal.iniciar(puerto);
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
