package sender;

import entities.ProcessingObject;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.List;

public class MessageSender implements Runnable {

    private List<ProcessingObject> processingObjects;
    private Session session;
    private MessageProducer messageProducer;
    private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;

    private int objectKeyboardNumber = 0;

    public MessageSender(Session session, MessageProducer messageProducer) {
        this.session = session;
        this.messageProducer = messageProducer;
    }

    public void run() {
        processingObjects.forEach(processingObject -> {
            try {

                ObjectMessage objectMessage = session
                        .createObjectMessage(processingObject);

                messageProducer.send(objectMessage, DELIVERY_MODE, Message.DEFAULT_PRIORITY,
                        Message.DEFAULT_TIME_TO_LIVE);
                processingObject.increaseRotationOnY();

                System.out.println(
                        "Processing object: " + processingObject.getObjectName() + " Y = "
                                + processingObject.getRy());

            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MessageSender setProcessingObjects(
            List<ProcessingObject> processingObjects) {
        this.processingObjects = processingObjects;
        return this;
    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
