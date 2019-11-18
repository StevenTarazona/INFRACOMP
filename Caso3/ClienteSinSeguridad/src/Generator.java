import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {

	private LoadGenerator generator;
	
	private int numberOfTasks = 400;
	private int gapBetweenTasks = 20;

	public Generator() throws NumberFormatException, IOException {
		Task work = createTask();
		generator = new LoadGenerator("Cliente - Server Load Test", numberOfTasks, work, gapBetweenTasks);
		generator.generate();
	}

	private Task createTask() throws NumberFormatException, IOException {
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Establezca puerto de conexion:");
		String puerto = stdIn.readLine();
		System.out.println("Establezca ip del servidor (en blanco si su servidor es local):");
		String servidor = stdIn.readLine();
		
//		System.out.println("Establezca carga:");
//		numberOfTasks = Integer.parseInt(stdIn.readLine());
//		System.out.println("Establezca retardo:");
//		gapBetweenTasks = Integer.parseInt(stdIn.readLine());
		
		stdIn.close();
		return new ClientServerTask(Integer.parseInt(puerto), servidor);
	}

	public static void main(String[] args) {

		
		try {
			Generator gen = new Generator();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
