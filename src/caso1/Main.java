package caso1;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

	public static void main(String[] args) {
		System.out.println("Elija el archivo de configuracion");

		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("JSON file", "json");
		jfc.setFileFilter(filtro);

		int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			System.out.println(file.getAbsolutePath());

			int nClientes = 10;
			int nServidores = 5;
			int bufferSize = 10;

			Buffer buffer = new Buffer(bufferSize, nClientes);

			for (int i = 0; i < nServidores; i++) {
				Servidor servidor = new Servidor(i);
				servidor.run(buffer);
			}
			for (int i = 0; i < nClientes; i++) {
				Cliente cliente = new Cliente(i, 10);
				cliente.run(buffer);
			}
		}

	}
}
