import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame implements ActionListener{
	//make buttons
	private JButton btnSend, btnExit;
	
  // Text field for receiving message
  private JTextField jtf = new JTextField();
 
  // Text area to display contents
  private JTextArea jta = new JTextArea();

  // IO streams
  private DataOutputStream toServer;
  private DataInputStream fromServer;

  public static void main(String[] args) {
    new Client();
  }
  
  public Client() {
    // Panel p to hold the label and text field
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(new JLabel("Enter message"), BorderLayout.WEST);
    p.add(jtf, BorderLayout.CENTER);
    jtf.setHorizontalAlignment(JTextField.RIGHT);

    setLayout(new BorderLayout());
    add(p, BorderLayout.NORTH);
    btnSend = new JButton("Send");
    btnSend.addActionListener(this);
	add(btnSend, BorderLayout.WEST);
    add(new JScrollPane(jta), BorderLayout.CENTER);
    btnExit = new JButton("Exit");
    btnExit.addActionListener(this);
	add(btnExit, BorderLayout.EAST);
    jtf.addActionListener(this); // Register listener

    setTitle("Client");
    setSize(500, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true); // It is necessary to show the frame here!

    try {
      // Create a socket to connect to the server
      Socket socket = new Socket("localhost", 8000);
      // Socket socket = new Socket("130.254.204.36", 8000);
      // Socket socket = new Socket("drake.Armstrong.edu", 8000);

      // Create an input stream to receive data from the server
      fromServer = new DataInputStream(socket.getInputStream());

      // Create an output stream to send data to the server
      toServer = new DataOutputStream(socket.getOutputStream());
    }
    catch (IOException ex) {
      jta.append(ex.toString() + '\n');
    }
  }


	@Override
	public void actionPerformed(ActionEvent e) {		
		if (e.getSource() == btnSend) {
			try {
		        // Get the radius from the text field
		        String message = jtf.getText().trim();
		
		        // Send the message to the server
		        toServer.writeUTF(message);
		        toServer.flush();
		
		        // Display to the text area
		        jta.append("Message: " + message + "\n");
		      }
		      catch (IOException ex) {
		        System.err.println(ex);
		      } 
		}
		if (e.getSource() == btnExit) {
			System.exit(0);
		}
	}
}
