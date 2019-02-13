package es.studium;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServidorChat extends JFrame implements ActionListener {
	/**
	 *
	 */
	//Crea una id Específica con la que interpreta el problema. Al cambiar de id todos los datos modificados estarían vacíos
	private static final long serialVersionUID = -3754989941364640731L;
	static ServerSocket servidor;
	static final int PUERTO = 44444;
	static int CONEXIONES = 0;
	static int ACTUALES = 0;
	static int MAXIMO = 100;
	static int random;
	static JTextField mensaje = new JTextField("");
	static JTextField mensaje2 = new JTextField("");
	private JScrollPane scrollpane1;
	static JTextArea textarea;
	JButton salir = new JButton("Salir");
	static Socket[] tabla = new Socket[100]; 
	public ServidorChat()
	{
		super(" VENTANA DEL SERVIDOR DE CHAT ");
		setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);
		mensaje.setEditable(false);
		mensaje2.setBounds(10, 348, 400, 30);
		add(mensaje2);
		mensaje2.setEditable(false);
		textarea = new JTextArea();
		scrollpane1 = new JScrollPane(textarea);
		scrollpane1.setBounds(10, 50, 400, 300);
		add(scrollpane1);
		salir.setBounds(420, 10, 100, 30);
		add(salir);
		textarea.setEditable(false);
		salir.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()== salir)
		{
			try{
				servidor.close();
			}catch(IOException ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}
	}
	public static void main(String args[]) throws Exception
	{
		servidor = new ServerSocket(PUERTO);
		random = (int) Math.floor(Math.random() * 100 + 1);
		System.out.println(random);
		System.out.println("Servidor iniciado...");
		ServidorChat pantalla = new ServidorChat();
		pantalla.setBounds(0, 0, 540, 400);
		pantalla.setVisible(true);
		mensaje.setText("Número de conexiones actuales: " + 0); 
		//Mientras que las conexiones sean menores al máximo, se seguirá permitiendo dejar entrar a los diferentes usuarios
		while(CONEXIONES < MAXIMO)
		{
			Socket socket;
			try {
				socket = servidor.accept();
			}catch (SocketException setx){
				// sale por aquí si pulsamos el botón salir
				break;
			}
			//Crear una tabla en la que puedan entrar todas las conexiones que han entrado en nuestro servidor
			tabla[CONEXIONES] = socket;
			//Se añaden conexiones a los contadores
			CONEXIONES++;
			ACTUALES++;
			//Iniciaremos el hilo que realizará el juego
			HiloServidor hilo = new HiloServidor(socket);
			hilo.start();
		} 
		if(!servidor.isClosed()){
			try{
				mensaje2.setForeground(Color.red);
				mensaje2.setText("Máximo Nº de conexiones establecidas: " + CONEXIONES);
				servidor.close();
			}catch (IOException ex){
				ex.printStackTrace();
			}
		}
		else{
			System.out.println("Servidor finalizado...");
		}
	}
}