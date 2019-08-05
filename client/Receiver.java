import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;


public class Receiver extends Thread{
    private long startTime;
    private MessageConsumer consumer;
    
    Receiver(MessageConsumer consumer, long startTime ) throws JMSException{
		this.consumer = consumer;
		this.startTime = startTime ;
    }

    public void run(){
        try {
        	Message message = this.consumer.receive();
        	
            long workTime = System.currentTimeMillis() - startTime - 10;
            
            if (message instanceof BytesMessage) {
             	BytesMessage bMessage = (BytesMessage) message;
             	byte[] bytes = new byte[(int) bMessage.getBodyLength()];
            	
             	bMessage.readBytes(bytes);
                	
             	FileOutputStream fos = new FileOutputStream(bMessage.getStringProperty("fileName"));
             	fos.write(bytes);
             	fos.close();
            	
             	System.out.println("File has saved");
             	
            }
            else if(message instanceof TextMessage) {
            	TextMessage textMessage = (TextMessage) message;
            	System.out.println(textMessage.getText());
            }

            synchronized(Client.lock){
                FileWriter fw = new FileWriter("result.txt", true);
                fw.write(Long.toString(workTime) + "\n");
                fw.close();
            }
                

        } catch (JMSException | IOException  e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
		}
    }
}