import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
 
import org.apache.activemq.ActiveMQConnectionFactory;
 
public class Server {
 
    // URL of the JMS server
    private static String url = "tcp://203.246.113.80:61616";
    // default broker URL is : tcp://localhost:61616"
    
    public static Object lock = new Object();
    
    private static String queueSubject = "JK_QUEUE";
    private static String topicSubject = "JK_TOPIC";
    private static String subject = queueSubject;
    
    
    public static void main(String[] args) throws JMSException, InterruptedException, IOException {
    	
    	System.out.println("-----Server-----");
    	
    	BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("Enter url to connect : ");
    	url = b.readLine();
    	//initial connection
    	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
    	Connection connection = connectionFactory.createConnection();
        connection.start();
        
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        System.out.println("Enter connection Type (ex. queue / topic): ");
        String connectionType = b.readLine();
        if(connectionType.equals("queue"))
    		subject = queueSubject;
    	else if(connectionType.equals("topic")) 
    		subject = topicSubject;
        
    	Destination destination = session.createQueue(subject);
        
        MessageConsumer consumer = session.createConsumer(destination);
    	
    	System.out.println("Waiting for messages");
    	
    	while(true) {
    		Message message = consumer.receive();
    		if(message instanceof TextMessage) {
    			TextMessage textMessage = (TextMessage) message;
    	        Responsor resp = new Responsor(session, textMessage);
    	        
    	        resp.start();
    	        //resp.run();
    		}
    	}
        //connection.close();
    }
}

