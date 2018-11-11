import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientA2 extends JFrame implements ActionListener{
	
	//make buttons
	private JButton btnSend, btnExit;
	
	// Text field for receiving message
	private JTextField jtf;
 
	// Text area to display contents
	private JTextArea jta;

	// IO streams
	private DataOutputStream toServer;
	private DataInputStream fromServer;

	public static void main(String[] args) throws SQLException {
		new ClientA2();
	}
	
	public ClientA2() throws SQLException {
		setLayout(new CardLayout());
		jtf = new JTextField("Message");
		add(makeGuiLogin());
		add(makeGuiMain());
		
		setTitle("Client");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true); // It is necessary to show the frame here!
	  }
	
	void serverConnect() {
		try {
		      // Create a socket to connect to the server
		      Socket socket = new Socket("localhost", 8000);
		      // Create data input and output stream to sennd and receive data from the server
		      fromServer = new DataInputStream(socket.getInputStream());
		      toServer = new DataOutputStream(socket.getOutputStream());  
		    }
		    catch (IOException ex) {
		      jta.append(ex.toString() + '\n');
		    }
	}

	private JPanel makeGuiMain() {
		// Main panel for when user logs in.
	    JPanel main = new JPanel();
	    main.setLayout(new BorderLayout());
	    
	    JPanel p = new JPanel();
	    p.setLayout(new BorderLayout());
	    jtf.addActionListener(this); // Register listener
	    p.add(jtf, BorderLayout.CENTER);
	    p.add(new JLabel("Enter message"), BorderLayout.WEST);
	    main.add(p, BorderLayout.NORTH);
	    
	    btnSend = new JButton("Send");
	    btnSend.addActionListener(this);
	    main.add(btnSend, BorderLayout.WEST);
	    jta = new JTextArea();
	    main.add(new JScrollPane(jta), BorderLayout.CENTER);
	    btnExit = new JButton("Exit");
	    btnExit.addActionListener(this);
	    main.add(btnExit, BorderLayout.EAST);
	    
	    return main;
	}
	
	private JPanel makeGuiLogin(){
		//Login panel to allow user to authenticate
		JPanel main = new JPanel();
	    main.setLayout(new BorderLayout());
	    main.add(new JLabel("Enter Sudent ID:"), BorderLayout.WEST);
	    //text field for student ID
	    JTextField stuNum = new JTextField("Sudent Number");
		main.add(stuNum);
	    JButton loginBtn = new JButton("Login");
	    
	    
	    loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cl = (CardLayout)(getContentPane().getLayout());
				//test if the input is valid(int only)
				if(testInt(stuNum.getText())== true) {
					
					try {
						//connect to server
					    serverConnect();
					    //send user ID to server
						String ID = stuNum.getText().trim();
						toServer.writeUTF(ID);
						//Read result from server and load the main view if a valid username is returned
						String user = fromServer.readUTF();
						if(user .equals("")) {
							JOptionPane.showMessageDialog(null, "User does not exist");
						}else {
							JOptionPane.showMessageDialog(null, "Welcome " + user);
							cl.next(getContentPane());
						}
					} catch (IOException e) {
						System.out.println(e);
					}
				}else {
					JOptionPane.showMessageDialog(null, "Invalid entry, Numerical characters only");
				}

			}
	    });
	    
	    main.add(loginBtn, BorderLayout.EAST);
	    return main;
	}
	
	//test if string can be cast to int
	 Boolean testInt(String test) {
		try {
		       Integer.parseInt(test);
		       return true;
		   }
		   catch( Exception e ) {
		       return false;
		   }
	 }
	
	@Override
	public void actionPerformed(ActionEvent e) {		
		if (e.getSource() == btnSend) {
			try {
		        // Get the message from the text field
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


