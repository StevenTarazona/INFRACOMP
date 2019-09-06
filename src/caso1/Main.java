package caso1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

	public static void main(String[] args) {
		System.out.println("Elija el archivo de configuracion");

		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("JSON file", "json");
		jfc.setFileFilter(filtro);

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			System.out.println(file.getAbsolutePath());

			int nClientes = 10;
			int nServidores = 5;
			int bufferSize = 10;

			Buffer buffer = new Buffer(bufferSize, nClientes);
			
			for (int i = 0; i < nServidores; i++) {
				Servidor s = new Servidor(i, buffer);
				s.start();
			}
			
			for (int i = 0; i < nClientes; i++) {
				Cliente c = new Cliente(i, 10, buffer);
				c.start();
			}
		}

	}
}
