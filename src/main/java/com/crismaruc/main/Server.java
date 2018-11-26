package com.crismaruc.main;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Server {
    public static void main(String[] args) throws Exception {
        try {
            // The configuration for the Qpid InitialContextFactory has been supplied in
            // a jndi.properties file in the classpath, which results in it being picked
            // up automatically by the InitialContext constructor.
            Context context = new InitialContext();

            ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
            Destination queue = (Destination) context.lookup("myQueueLookup");

            Connection connection = factory.createConnection("admin", "admin");
            connection.setExceptionListener(new MyExceptionListener());
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //Create a temporary queue and consumer to receive responses, and a producer to send requests.
            TemporaryQueue responseQueue = session.createTemporaryQueue();
            MessageConsumer messageConsumer = session.createConsumer(responseQueue);
            MessageProducer messageProducer = session.createProducer(queue);

            //Send some requests and receive the responses.
            String[] requests = new String[] { "Twas brillig, and the slithy toves",
                    "Did gire and gymble in the wabe.",
                    "All mimsy were the borogroves,",
                    "And the mome raths outgrabe." };

            for (String request : requests) {
                TextMessage requestMessage = session.createTextMessage(request);
                requestMessage.setJMSReplyTo(responseQueue);

                messageProducer.send(requestMessage, DeliveryMode.NON_PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

                TextMessage responseMessage = (TextMessage) messageConsumer.receive(100000);
                if (responseMessage != null) {
                    System.out.println("[CLIENT] " + request + " ---> " + responseMessage.getText());
                } else {
                    System.out.println("[CLIENT] Response for '" + request +"' was not received within the timeout, exiting.");
                    break;
                }
            }

            connection.close();
        } catch (Exception exp) {
            System.out.println("[CLIENT] Caught exception, exiting.");
            exp.printStackTrace(System.out);
            System.exit(1);
        }
    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            System.out.println("[CLIENT] Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }
}