package sender;

import entities.Scene;
import utils.MovementUtils;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.time.Instant;

public class MessageSender implements Runnable {

    private Scene scene;
    private Session session;
    private MessageProducer messageProducer;
    private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;

    public MessageSender(Session session, MessageProducer messageProducer) {
        this.session = session;
        this.messageProducer = messageProducer;
    }

    public void run() {
        Instant start = Instant.now();
        int key = MovementUtils.getKey();
        int dWheel = MovementUtils.getMouseDWheel();
        scene.getFrames().forEach(frame -> {
            try {

                frame.setMouseWheel(dWheel);
                frame.setKeyboard(key);
                ObjectMessage objectMessage = session
                        .createObjectMessage(frame);
                messageProducer.send(objectMessage, DELIVERY_MODE, Message.DEFAULT_PRIORITY,
                        Message.DEFAULT_TIME_TO_LIVE);
//
//                System.out.println(
//                        "Processing object: " + frame.getName());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        scene.getFrames().forEach(frame -> {
            frame.setKeyboard(0);
        });
        //        System.out.println("Sending messages = " + Duration.between(start, Instant.now()).toMillis() + " ms");

    }

    public MessageSender setScene(Scene scene) {
        this.scene = scene;
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
