import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Server extends JFrame {
	// Text area for displaying contents
	  private JTextArea jta = new JTextArea();

	  public static void main(String[] args) {
	    new Server();
	  }

	  public Server() {
	    // Place text area on the frame
	    setLayout(new BorderLayout());
	    add(new JScrollPane(jta), BorderLayout.CENTER);
	    setTitle("Server");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true); // It is necessary to show the frame here!

	    try {
	    	
	    	// Create a server socket
		    ServerSocket serverSocket = new ServerSocket(8000);
		    jta.append("Server started at " + new Date() + '\n');

		    while (true) {	 
		    	Socket s1=serverSocket.accept();
		    	myClient c = new myClient(s1);
		    	c.start();
		    }
	    }
	    catch(IOException ex) {
	    	System.err.println(ex);
	    }
	 } 
	      
	  
	  private class myClient extends Thread {
		private DataInputStream fromClient;
		private DataOutputStream toClient;
		// The Constructor for the client
		public myClient(Socket socket) throws IOException {
			//Initialise input/output streams	
			fromClient = new DataInputStream(socket.getInputStream());
			toClient = new DataOutputStream(socket.getOutputStream());
		}

		
		public void run() {
			try {
				// Send+Receive
				while (true) {
					String message = fromClient.readUTF();
			        toClient.writeChars(message);
			        jta.append("Message: " + message + '\n');
				}
			} catch (Exception e) {
				System.err.println(e + " Error");
			}
		}
	}
}
