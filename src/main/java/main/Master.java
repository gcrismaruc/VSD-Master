package main;

import com.jogamp.opengl.GL;
import entities.ProcessedObject;
import entities.ProcessingFrame;
import entities.Scene;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import sender.MessageSender;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Master {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static final int FPS_CAP = 60;

    public static int SLAVES_NUMBER = 2;

    private static final float[] depths = new float[WIDTH * HEIGHT];

    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle("Master");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        ByteBuffer renderBuffer;

        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, HEIGHT, 0, WIDTH, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        ByteBuffer byteBuffer;
        boolean isFirstImage = true;

        Scene scene = new Scene();
        scene.setFrames(Arrays.asList(new ProcessingFrame().setName("frame1"),
                new ProcessingFrame().setName("frame2")));

        try {
            Context context = new InitialContext();

            ConnectionFactory factory = (ConnectionFactory) context.lookup("myFactoryLookup");
            Destination queue = (Destination) context.lookup("myQueueLookup");
            Destination slaveQueue = (Destination) context.lookup("slaveQueue");

            Connection connection = factory.createConnection("admin", "admin");
            connection.setExceptionListener(new Master.MyExceptionListener());
            connection.start();

            Session consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer messageConsumer = consumerSession.createConsumer(queue);

            Session producerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = producerSession.createProducer(slaveQueue);

            //Start sending requests to slaves
            MessageSender messageSender = new MessageSender(producerSession, messageProducer);
            messageSender.setScene(scene);

            ExecutorService executorService = Executors.newFixedThreadPool(SLAVES_NUMBER + 1);

            executorService.execute(messageSender);
            Message receivedMessage;

            List<ProcessedObject> processedObjects = new ArrayList<>();
            List<DecompressingThread> decompressingThreads = new ArrayList<>();

            Thread.sleep(3000L);

            ProcessedObjectsQueue processedObjectsQueue = new ProcessedObjectsQueue();

            boolean needToReceive = true;

            while (!Display.isCloseRequested()) {

                // Clear the screen and depth buffer
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

               try {
                   System.out.println(
                           "***********************************************************************");

                   Instant startLoop = Instant.now();

                   Instant startReceiving = Instant.now();
                   for (int k = 0; k < SLAVES_NUMBER; k++) {
                       Instant msg = Instant.now();
                       receivedMessage = messageConsumer.receive(100000);
                       System.out.println("Receiving one msg = " + Duration
                               .between(msg, Instant.now()).toMillis() + " ms");

                       ObjectMessage objectMessage = (ObjectMessage) receivedMessage;
                       DecompressingThread decompressingThread = new DecompressingThread()
                               .setBytes((byte[]) objectMessage.getObject())
                               .setProcessedObjectsQueue(processedObjectsQueue);

                       executorService.execute(
                               decompressingThread);

                       decompressingThreads.add(decompressingThread);
                   }
                   //wait the decompressing threads to finish their job
                   executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
                   System.out.println(
                           "Receiving time = " + Duration.between(startReceiving, Instant.now())
                                   .toMillis() + " ms");

                   Instant startMapping = Instant.now();
                   processedObjects
                           .addAll(decompressingThreads
                                   .stream()
                                   .map(decompressingThread -> decompressingThread
                                           .getProcessedObject())
                                   .collect(Collectors.toList()));
                   System.out.println(
                           "Mapping time = " + Duration.between(startMapping, Instant.now())
                                   .toMillis()
                                   + " ms");

                   if (isFirstImage) {
                       byteBuffer = ByteBuffer
                               .allocateDirect(processedObjects
                                       .get(0)
                                       .getPixels().length);
                       byteBuffer.put(processedObjects.get(0)
                               .getPixels());
                       byteBuffer.rewind();
                       GL11.glDrawPixels(WIDTH, HEIGHT, GL.GL_RGB, GL11.GL_UNSIGNED_BYTE,
                               byteBuffer);
                       Display.update();
                       isFirstImage = false;
                   } else {

                       Instant start = Instant.now();
                       renderBuffer = updateBuffer(processedObjects);

                       System.out.println(
                               "Merging time = " + Duration.between(start, Instant.now())
                                       .toMillis()
                                       + " ms");

                       GL11.glDrawPixels(WIDTH, HEIGHT, GL.GL_RGB, GL11.GL_UNSIGNED_BYTE,
                               renderBuffer);
                       Display.update();
                   }

                   executorService.execute(messageSender);

                   decompressingThreads.removeAll(decompressingThreads);
                   processedObjects.removeAll(processedObjects);

                   System.out.println(
                           "Total loop time: " + Duration.between(startLoop, Instant.now())
                                   .toMillis()
                                   + " ms");
                   System.out.println(
                           "----------------------------------------------------------------------------------");
               } catch (Exception e) {
                   System.out.println(e.getMessage());
               }
            }

            connection.close();
        } catch (Exception exp) {
            System.out.println("Caught exception, exiting.");
            exp.printStackTrace(System.out);
            System.exit(1);
        }

        Display.destroy();
    }

    private static ByteBuffer updateBuffer(List<ProcessedObject> processedObjects) {
        int buffersSize = processedObjects.get(0).getPixels().length;

        ByteBuffer bufferToReturn = ByteBuffer
                .allocateDirect(buffersSize);

        int listSize = processedObjects.size();

        if (listSize == 2) {
            bufferToReturn
                    .put(getImage(processedObjects.get(0),
                            processedObjects.get(1).getPixels(),
                            processedObjects.get(1).getDepthBuffer()).getPixels());
        } else {
            if (listSize > 2) {
                ProcessedObject processedObject = getImage(processedObjects.get(0),
                        processedObjects.get(1).getPixels(),
                        processedObjects.get(1).getDepthBuffer());
                for (int i = 2; i < listSize; i++) {
                    processedObject = getImage(processedObject, processedObjects.get(i).getPixels(),
                            processedObjects.get(i).getDepthBuffer());
                }
                bufferToReturn.put(processedObject.getPixels());
            }
        }

        bufferToReturn.rewind();
        return bufferToReturn;
    }

    private static ProcessedObject getImage(ProcessedObject processedObject, byte[] pixelsBuffer,
            byte[] depthBuffer) {

        byte[] newPixels = new byte[processedObject.getPixels().length];
        byte[] newDepths = new byte[processedObject.getDepthBuffer().length];

        int pixelIndex = 0;
        for (int i = 0; i < processedObject.getDepthBuffer().length; i += 4) {
            if (depthBuffer[i + 2] > processedObject.getDepthBuffer()[i + 2]) {
                newPixels[pixelIndex] = pixelsBuffer[pixelIndex];
                newPixels[pixelIndex + 1] = pixelsBuffer[pixelIndex + 1];
                newPixels[pixelIndex + 2] = pixelsBuffer[pixelIndex + 2];
                newDepths[i] = depthBuffer[i + 2];
            } else {
                newPixels[pixelIndex] = processedObject.getPixels()[pixelIndex];
                newPixels[pixelIndex + 1] = processedObject.getPixels()[pixelIndex + 1];
                newPixels[pixelIndex + 2] = processedObject.getPixels()[pixelIndex + 2];
                newDepths[i] = processedObject.getDepthBuffer()[i + 2];
            }
            pixelIndex += 3;
        }

        return processedObject
                .setPixels(newPixels)
                .setDepthBuffer(newDepths);
    }

    public static void main(String[] argv) {
        Master quadExample = new Master();
        quadExample.start();
    }

    private static class MyExceptionListener implements ExceptionListener {
        public void onException(JMSException exception) {
            System.out.println("Connection ExceptionListener fired, exiting.");
            exception.printStackTrace(System.out);
            System.exit(1);
        }
    }

}