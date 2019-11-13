import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
	
	private LoadGenerator generator;
	
	public Generator() throws NumberFormatException, IOException {
		Task work = createTask();
		int numberOfTasks = 100;
		int gapBetweenTasks = 1000;
		generator = new LoadGenerator("Cliente - Server Load Test", numberOfTasks, work, gapBetweenTasks);
		generator.generate();
	}
	
	private Task createTask() throws NumberFormatException, IOException{
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String puerto = stdIn.readLine();
		stdIn.close();
		return new ClientServerTask(Integer.parseInt(puerto));
	}
	
	public static void main(String[] args) {
		System.out.println("Eztablezca puerto de conexion:");
		try {
			Generator gen = new Generator();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
