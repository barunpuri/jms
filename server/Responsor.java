import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Responsor extends Thread {
	
	private Session session;
	private MessageProducer producer;
	private TextMessage	message;
    
	public Responsor(Session session, TextMessage message) throws JMSException {
		this.session = session;
		this.producer = session.createProducer(null);
		this.message  = message;
	}

	public void run() {
		try {
			
			File f = new File(message.getText());
			if(f.isFile())
				sendFile(f);
			else
				sendResponse();
			
		} catch (JMSException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendFile(File f) throws JMSException, IOException {
		int flen = (int) f.length();
    	byte[] bytes = new byte[flen];
        System.out.println(f.getName());
    	DataInputStream dis = new DataInputStream(new FileInputStream(f));
    	dis.readFully(bytes);
    	dis.close();
    	BytesMessage responseMessage = session.createBytesMessage();
    	responseMessage.writeBytes(bytes);
    	responseMessage.setStringProperty("fileName", f.getName());
    	responseMessage.setJMSCorrelationID(message.getJMSMessageID());
    	this.producer.send(message.getJMSReplyTo(), responseMessage);
    	
	}
	
	private void sendResponse() throws JMSException, InterruptedException {
		String connectedPartner = message.toString().split("ID:")[1].split("-1:")[0];
        System.out.println(connectedPartner + "(" + message.getJMSDestination() + ") : \"" + message.getText() + "\"");
	
        TextMessage responseMessage = session.createTextMessage("Server:message \'" + message.getText() + "\' received");
        responseMessage.setJMSCorrelationID(message.getJMSMessageID());
        Thread.sleep(10);
        this.producer.send(message.getJMSReplyTo(), responseMessage);
	}
	
}

	