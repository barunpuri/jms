import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Sender extends Thread {
	private String queueSubject = "JK_QUEUE";
    private String topicSubject = "JK_TOPIC";
    private String subject = queueSubject;
    
    private MessageProducer producer;
    private MessageConsumer consumer;
    private Session session;
    
    private String inputText;
    
    
    public Sender(Session session, String connectionType, String inputText) throws JMSException {
    	this.session = session;
    	if(connectionType.equals("queue"))
    		this.subject = this.queueSubject;
    	else if(connectionType.equals("topic")) 
    		this.subject = this.topicSubject;
    	this.inputText = inputText;
    	Destination destination = this.session.createQueue(this.subject);
        
        producer = session.createProducer(destination);
    }
    
	public void run() {
		long startTime = System.nanoTime();
		Receiver receiver = null;
		
		try {
			this.send(inputText);
			receiver = new Receiver(consumer, startTime );
			
			receiver.start();
			
		} catch (JMSException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void send(String input) throws JMSException, InterruptedException {
		
		TextMessage message = session.createTextMessage(input);
		Destination tmpQueue = this.session.createTemporaryQueue();
		this.consumer = session.createConsumer(tmpQueue);
		
		message.setJMSReplyTo(tmpQueue);
		producer.send(message);
		Thread.sleep(50);
	}
	
}

	