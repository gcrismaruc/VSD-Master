package sender;

import entities.ProcessingObject;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static main.Master.SLAVES_NUMBER;

public class MessageSender implements Runnable {

    List<ProcessingObject> processingObjects = new ArrayList<ProcessingObject>(Arrays.asList(
            new ProcessingObject(0, 1, 0, -10, 5, -65, 0, 0, "dragon.obj"),
            new ProcessingObject(0, 1, 0, 10, 5, -55, 0, 0, "dragon.obj"),
            new ProcessingObject(0, 1, 0, 0, -5, -45, 0, 0, "dragon.obj")
    ));
    private Session session;
    private MessageProducer messageProducer;
    private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;

    public MessageSender(Session session, MessageProducer messageProducer) {
        this.session = session;
        this.messageProducer = messageProducer;
    }

    public void run() {

        while (true) {
            try {
//                processingObjects.forEach(processingObject -> processingObject.setRy(processingObject.getRy()++));
                Random random
                        = new Random();
                int objIndex = random.nextInt(SLAVES_NUMBER);
//                System.out.println("Sending to slave obj no: " + objIndex);

                processingObjects.get(objIndex).increaseRotationOnY();

                ObjectMessage objectMessage = session
                        .createObjectMessage(
                                processingObjects.get(objIndex));

                messageProducer.send(objectMessage, DELIVERY_MODE, Message.DEFAULT_PRIORITY,
                        Message.DEFAULT_TIME_TO_LIVE);

            } catch (JMSException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
