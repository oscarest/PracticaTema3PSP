package es.studium;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HiloServidor extends Thread {
	DataInputStream fentrada;
	Socket socket;

	public HiloServidor(Socket socket) {
		this.socket = socket;
		try {
			fentrada = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			System.out.println("Error de E/S");
			e.printStackTrace();
		}
	}

	public synchronized void run() {
		ServidorChat.mensaje.setText("Número de conexiones actuales: " + ServidorChat.ACTUALES);
		String texto = ServidorChat.textarea.getText();
		EnviarMensajes(texto);
		while (true) {
			String cadena = "";
			try {
				cadena = fentrada.readUTF();
				if (cadena.trim().equals("*")) 
				{
					ServidorChat.ACTUALES--;
					ServidorChat.mensaje.setText("Número de conexiones actuales: " + ServidorChat.ACTUALES);
					break;
				}
				else 
				{
					// ServidorChat.textarea.append(cadena + "\n");
					if (cadena.contains("...")) 
					{
						ServidorChat.textarea.append(cadena + "\n");
					}
					else
					{
						String[] parts = cadena.split("> ");
						String part = parts[0];
						String part1 = parts[1];
						Thread.sleep(3000);
						if (Integer.parseInt(part1) < ServidorChat.random) 
						{
							ServidorChat.textarea.append(
									part + ">" + "piensa que el número es " + part1 + ", pero el número es mayor \n");
						} else if (Integer.parseInt(part1) > ServidorChat.random) {
							ServidorChat.textarea.append(
									part + ">" + "piensa que el número es " + part1 + ", pero el número es menor \n");
						} else if (Integer.parseInt(part1) == ServidorChat.random) 
						{
							// Victoria
							ServidorChat.textarea.append(part + ">" + "piensa que el número es " + part1 + " y ha acertado! \n" + "Se cerrará la aplicación en 10 segundos debido a que el usuario " + part + " ha ganado");
							texto = ServidorChat.textarea.getText();
							EnviarMensajes(texto);
							Thread.sleep(10000);
							System.exit(0);	
						}	
					}
					texto = ServidorChat.textarea.getText();
					EnviarMensajes(texto);
				}
			} catch (Exception ex) {
				try {
					fentrada.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ex.printStackTrace();
				break;
			}
		}
	}
	private void EnviarMensajes(String texto) {
		for (int i = 0; i < ServidorChat.CONEXIONES; i++) {
			Socket socket = ServidorChat.tabla[i];
			try {
				DataOutputStream fsalida = new DataOutputStream(socket.getOutputStream());
				fsalida.writeUTF(texto);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}