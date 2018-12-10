package sender;

import entities.ProcessingFrame;
import entities.Scene;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MessageSender implements Runnable {

    private Scene scene;
    private Session session;
    private MessageProducer messageProducer;
    private static final int DELIVERY_MODE = DeliveryMode.NON_PERSISTENT;
    private Random random;

    public MessageSender(Session session, MessageProducer messageProducer) {
        this.session = session;
        this.messageProducer = messageProducer;
        random = new Random();
    }

    int key;

    public void run() {
        Instant start = Instant.now();

        List<ProcessingFrame> frames = scene.getFrames();

        Collections.shuffle(frames);
        frames
                .forEach(frame -> {
                    try {
                        System.out.println("Sending frame: " + frame.getName() + " with key: " + key);
                        frame.setKeyboard(key);
                        ObjectMessage objectMessage = session.createObjectMessage(frame);
                        messageProducer.send(objectMessage, DELIVERY_MODE, Message.DEFAULT_PRIORITY,
                                Message.DEFAULT_TIME_TO_LIVE);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                });

//        scene.getFrames()
//                .forEach(frame -> {
//                    frame.setKeyboard(0);
//                });
        //        System.out.println("Sending messages = " + Duration.between(start, Instant.now
        // ()).toMillis() + " ms");

    }

    public MessageSender setScene(Scene scene) {
        this.scene = scene;
        return this;
    }

    public int getKey() {
        return key;
    }

    public MessageSender setKey(int key) {
        this.key = key;
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
