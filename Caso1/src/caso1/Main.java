package caso1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {

	public static void main(String[] args) {
		System.out.println("Elija el archivo de configuracion");

		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
		FileNameExtensionFilter filtro = new FileNameExtensionFilter("PROPERTIES file", "properties");
		jfc.setFileFilter(filtro);

		int returnValue = jfc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			try (InputStream input = new FileInputStream(jfc.getSelectedFile())) {

				Properties prop = new Properties();
				prop.load(input);

				int nClientes = Integer.parseInt(prop.getProperty("numero_de_clientes"));
				int nServidores = Integer.parseInt(prop.getProperty("numero_de_servidores"));
				int bufferSize = Integer.parseInt(prop.getProperty("tamanio_del_buffer"));
				String[] nMensajes = prop.getProperty("numero_de_mensajes").split(",");

				System.out.println("Numero de clientes: " + nClientes);
				System.out.println("Numero de servidores: " + nServidores);
				System.out.println("Tamaño del buffer: " + bufferSize);

				Buffer buffer = new Buffer(bufferSize, nClientes);

				for (int i = 0; i < nServidores; i++) {
					Servidor s = new Servidor(i, buffer);
					s.start();
				}

				for (int i = 0; i < nClientes; i++) {
					Cliente c = new Cliente(i, Integer.parseInt(nMensajes[i]), buffer);
					c.start();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
}
