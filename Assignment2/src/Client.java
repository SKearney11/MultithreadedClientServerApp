import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.mysql.cj.xdevapi.Statement;

public class Client extends JFrame implements ActionListener{
	
	//for connecting to SQL Server
	private final String userName = "root";
	private final String password = "";
	private final String serverName = "localhost";
	private final int portNumber = 3306;
	private final String dbName = "assign2";
	private final String tableName = "mystudents";
	
	
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
		new Client();
	}
  
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);
		//the "?useSSL=False needs to be here to avoid an error/warning with MYSQL on my computer"
		conn = DriverManager.getConnection("jdbc:mysql://"
				+ this.serverName + ":" + this.portNumber + "/" + this.dbName+"?useSSL=false",
				connectionProps);
		System.out.println("Connected to SQL Server");
		return conn;
	}
	
	public Client() throws SQLException {
		setLayout(new CardLayout());
		jtf = new JTextField("Message");
		add(makeGuiLogin());
		add(makeGuiMain());
		
		setTitle("Client");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true); // It is necessary to show the frame here!

	    try {
	      // Create a socket to connect to the server
	      Socket socket = new Socket("localhost", 8000);
	      // Create an input stream to receive data from the server
	      fromServer = new DataInputStream(socket.getInputStream());

	      // Create an output stream to send data to the server
	      toServer = new DataOutputStream(socket.getOutputStream());
	    }
	    catch (IOException ex) {
	      jta.append(ex.toString() + '\n');
	    }
	  }

	private JPanel makeGuiMain() {
		// Panel p to hold the label and text field
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
		JPanel main = new JPanel();
	    main.setLayout(new BorderLayout());
	    main.add(new JLabel("Enter Sudent ID:"), BorderLayout.WEST);
	    //text field for student ID
	    JTextField stuNum = new JTextField("Sudent Number");
	    stuNum.setPreferredSize( new Dimension( 20, 20) );
		main.add(stuNum);
	    JButton t = new JButton("Login");
	    
	    t.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CardLayout cl = (CardLayout)(getContentPane().getLayout());
				
				try {
					Connection conn = getConnection();
					if(testInt(stuNum.getText())== true) {
						int ID = Integer.parseInt(stuNum.getText());
						String user = authenticate(ID, conn);
						if(user .equals("")) {
							JOptionPane.showMessageDialog(null, "User does not exist");
						}else {
							JOptionPane.showMessageDialog(null, "Welcome " + user);
							cl.next(getContentPane());
						}	
					}else {
						JOptionPane.showMessageDialog(null, "Invalid entry");
					}
				} catch (SQLException e) {
					System.err.println(e + "NO SQL Connection");
				}
			}
	    });
	    main.add(t, BorderLayout.EAST);
	    return main;
	}
	
	Boolean testInt(String test) {
		try {
	        Integer.parseInt(test);
	        return true;
	    }
	    catch( Exception e ) {
	        return false;
	    }
	}
	
	@SuppressWarnings("finally")
	String authenticate(int number, Connection conn) {
		String command = "SELECT * FROM mystudents \n WHERE STUD_ID ="+number+";";
		java.sql.Statement s = null;
		ResultSet rs = null;
		String result = "";
	    try {
	    	s = conn.createStatement ();
			s.executeQuery(command);
			rs = s.getResultSet();
			rs.next();
			result = rs.getString(3) + " " + rs.getString(4);
	    }finally {
	    	return result;
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
