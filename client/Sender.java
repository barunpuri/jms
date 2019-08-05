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
		long startTime = System.currentTimeMillis();
		Receiver receiver = null;
		
		try {
			this.send(inputText);
			receiver = new Receiver(consumer, startTime );
			
			receiver.start();
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void send(String input) throws JMSException {
		
		TextMessage message = session.createTextMessage(input);
		Destination tmpQueue = this.session.createTemporaryQueue();
		this.consumer = session.createConsumer(tmpQueue);
		
		message.setJMSReplyTo(tmpQueue);
		producer.send(message);
	}
	
	/*
	 * 
MessageProducer producer = session.createProducer(queue);
Queue tmpQueue = session.createTemporaryQueue();
MessageConsumer consumer = session.createConsumer(tmpQueue);

// Send request
Message request = session.createTextMessage("how are you?");
request.setJMSReplyTo(tmpQueue);
producer.send(request);

// Wait for response
Message reply = consumer.receive();
TextMessage tm = (TextMessage) reply;
System.out.println("Got reply: " + tm.getText());
*/

//	public String send(File f) throws JMSException, IOException {
//	int flen = (int) f.length();
//	byte[] bytes = new byte[flen];
//	BytesMessage message = session.createBytesMessage();
//	DataInputStream dis = new DataInputStream(new FileInputStream(f));
//	dis.readFully(bytes);
//	dis.close();
//	
//    message.writeBytes(bytes);
//    message.setStringProperty("fileName", f.getName());
//    
//    this.producer.send(message);
//    
//	System.out.println("File successfully sent");
//	
//	return message.getJMSMessageID();
//}
	
}

	