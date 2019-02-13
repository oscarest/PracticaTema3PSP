package es.studium;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClienteChat extends JFrame implements ActionListener{
	private static final long serialVersionUID = 3876973990022961575L;
	Socket socket;
	DataInputStream fentrada;
	DataOutputStream fsalida;
	String nombre;
	static JTextField mensaje = new JTextField();
	private JScrollPane scrollpane;
	static JTextArea textarea;
	JButton boton = new JButton("Enviar");
	JButton desconectar = new JButton("Salir");
	boolean repetir = true; 
	public ClienteChat(Socket socket, String nombre)
	{
		super(" Conexión del cliente char: " + nombre);
		setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);
		textarea = new JTextArea();
		scrollpane = new JScrollPane(textarea);
		scrollpane.setBounds(10, 50, 400, 300);
		add(scrollpane);
		boton.setBounds(420, 10, 100, 30);
		add(boton);
		desconectar.setBounds(420, 50, 100, 30);
		add(desconectar);
		textarea.setEditable(false);
		boton.addActionListener(this);
		desconectar.addActionListener(this);
		textarea.setText("Introduzca un número entre 1 y 100 para intentar adivinar el número oculto");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.socket = socket;
		this.nombre = nombre; 
		try
		{
			fentrada = new DataInputStream(socket.getInputStream());
			fsalida = new DataOutputStream(socket.getOutputStream());
			String texto = "> Entra en el chat... " + nombre + "\n";
			fsalida.writeUTF(texto);
		}
		catch (IOException ex)
		{
			System.out.println("Error de E/S");
			ex.printStackTrace();
			System.exit(0);
		}
	} 
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==boton) {
			String texto = nombre + "> " + mensaje.getText();
			try{
				mensaje.setText("");
				fsalida.writeUTF(texto);
				//Thread.sleep(3000);
			}catch (IOException ex){
				ex.printStackTrace();
			} 
			/*catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
		} 
		else if(e.getSource()==desconectar) {
			String texto = " > Abandona el chat... " + nombre;
			try{
				fsalida.writeUTF(texto);
				fsalida.writeUTF("*");
				repetir = false;
			}catch (IOException ex){
				ex.printStackTrace();
			}
		}
	} 
	public void ejecutar() {
		String texto = "";
		while(repetir) {
			try {
				texto = fentrada.readUTF();
				textarea.setText(texto);
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:2>>",
				JOptionPane.ERROR_MESSAGE);
				repetir = false;
			}
		}
		try{
			socket.close();
			System.exit(0);
		}catch (IOException ex){
			ex.printStackTrace();
		}
	} 
	public static void main(String[] args) throws Exception
	{
		int puerto = 44444;
		String nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:");
		Socket socket = null;
		try{
			socket = new Socket("127.0.0.1", puerto);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:1>>", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		if(!nombre.trim().equals("")) {
			ClienteChat cliente = new ClienteChat(socket, nombre);
			cliente.setBounds(0,0,540,400);
			cliente.setVisible(true);
			cliente.ejecutar();
		}
		else{
			System.out.println("El nombre está vacío...");
		}
	}
}