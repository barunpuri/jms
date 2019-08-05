import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
 
public class Client {
 
	// URL of the JMS server
    private static String url = "tcp://localhost:61616";
    // default broker URL is : tcp://localhost:61616"
    
    public static Object lock = new Object();
    
    public static void main(String[] args) throws JMSException, InterruptedException, IOException {
    	
    	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
//    	System.out.println("Enter url to connect : ");
//    	url = b.readLine();
    	
    	//initial connection
    	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
    	Connection connection = connectionFactory.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        String connectionType = "queue";
        System.out.println("-----Client-----");
//        System.out.println("Enter connection Type (ex. queue / topic): ");
//        connectionType = b.readLine();
        
        int cnt=0;
 		while(true){
 			System.out.println("Enter message or file Path and name, end to terminate: ");
 			String input = b.readLine();
 			
 		
 			if (input.equals("end"))  
 		        break;
 			
 			Sender sender = new Sender(session, connectionType, input);
 			sender.run();
 			//sender.start();
 			cnt ++;
		}
 		
    }
}

