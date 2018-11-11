import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class MultiThreadedServerA2 extends JFrame {
	
	//for connecting to SQL Server
		private final String userName = "root";
		private final String password = "";
		private final String serverName = "localhost";
		private final int portNumber = 3306;
		private final String dbName = "assign2";
	
	
	// Text area for displaying contents
	  private JTextArea jta = new JTextArea();

	  public static void main(String[] args) {
	    new MultiThreadedServerA2();
	  }

	  public MultiThreadedServerA2() {
	    // Place text area on the frame
	    setLayout(new BorderLayout());
	    add(new JScrollPane(jta), BorderLayout.CENTER);
	    setTitle("Server");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true); // It is necessary to show the frame here!
	    
	    try {
	    	// Get connection to SQL Server
	    	Connection conn = getConnection();
	    	// Create a server socket
		    ServerSocket serverSocket = new ServerSocket(8000);
		    jta.append("Server started at " + new Date() + '\n');

		    while (true) {	 
		    	//Create socket for client
		    	Socket s1=serverSocket.accept();

		    	myClient c = new myClient(s1);
		    	int stuNum = 0;
		    	//Get student number entry from client and convert it to int
		    	String sFromClient = c.fromClient.readUTF();
		    	if (testInt(sFromClient) == true) {
		    		stuNum = Integer.parseInt(sFromClient);
		    	}
		    	//get corresponding username from the SQL server and send it back to the client
		    	String userName = authenticate(stuNum, conn);
		    	c.toClient.writeUTF(userName);
		    	//If the username is valid start a thread else close the socket
		    	if(!userName .equals("")){
		    		c.start();
		    	} else {
		    		s1.close();
		    	}
		    	
		    }
	    }
	    catch(IOException ex) {
	    	System.err.println(ex);
	    } catch (SQLException e) {
	    	System.err.println(e);
		}
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
	  
	  //get connection to SQL server
	  public Connection getConnection() throws SQLException {
			Connection conn = null;
			Properties connectionProps = new Properties();
			connectionProps.put("user", this.userName);
			connectionProps.put("password", this.password);
			//"?useSSL=False to avoid an error/warning with MYSQL"
			conn = DriverManager.getConnection("jdbc:mysql://"
					+ this.serverName + ":" + this.portNumber + "/" + this.dbName+"?useSSL=false",
					connectionProps);
			System.out.println("Connected to SQL Server");
			return conn;
		}
	  
	  //check if user exists and return back name of user
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
	  
	  private class myClient extends Thread {
		private InetAddress address;
		private DataInputStream fromClient;
		private DataOutputStream toClient;
		// The Constructor for the client
		public myClient(Socket socket) throws IOException {
			//Initialise input/output streams and IP Address	
			address = socket.getInetAddress();
			fromClient = new DataInputStream(socket.getInputStream());
			toClient = new DataOutputStream(socket.getOutputStream());
		}

		
		public void run() {
			try {
				// Send+Receive messages
				while (true) {
					String message = fromClient.readUTF();
			        toClient.writeChars(message);
			        jta.append(address + ": "+ message + '\n');
				}
			} catch (Exception e) {
				System.err.println(e + " Error");
			}
		}
	}
}
