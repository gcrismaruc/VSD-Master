import com.jogamp.opengl.GL;
import entities.ProcessedObject;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Master {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    private static final int FPS_CAP = 60;

    private static int SLAVES_NUMBER = 3;

    private static final float[] depths = new float[WIDTH * HEIGHT];

    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // init OpenGL
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, HEIGHT, 0, WIDTH, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        ByteBuffer byteBuffer;
        boolean isFirstImage = true;

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

            Thread senderThread = new Thread(messageSender);
            senderThread.setName("Receiving thread");
            senderThread.start();
            Message message1;

            ExecutorService executorService = Executors.newFixedThreadPool(SLAVES_NUMBER);
            List<ProcessedObject> processedObjects = new ArrayList<ProcessedObject>();
            List<DecompressingThread> decompressingThreads = new ArrayList<DecompressingThread>();

            Thread.sleep(10000L);
            //            DecompressingThread decompressingThread = new DecompressingThread();

            ProcessedObjectsQueue processedObjectsQueue = new ProcessedObjectsQueue();

            while (!Display.isCloseRequested()) {
                // Clear the screen and depth buffer
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

                for (int k = 0; k < SLAVES_NUMBER; k++) {
                    message1 = messageConsumer.receive(100000);

                    ObjectMessage objectMessage = (ObjectMessage) message1;
                    //                            decompressingThread.setBytes((byte[]) objectMessage
                    //                                    .getObject());
                    //                                        decompressingThread.run();
                    DecompressingThread decompressingThread = new DecompressingThread()
                            .setBytes((byte[]) objectMessage.getObject())
                            .setProcessedObjectsQueue(processedObjectsQueue);

                    executorService.execute(
                            decompressingThread);

                    decompressingThreads.add(decompressingThread);
//                    entities.ProcessedObject processedObject = decompressingThread
//                            .getProcessedObject();
//                    processedObjects.add(processedObject);
                }

                executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

                processedObjects
                        .addAll(decompressingThreads
                                .stream()
                                .map(decompressingThread -> decompressingThread
                                        .getProcessedObject())
                                .collect(Collectors.toList()));

                if (isFirstImage) {
                    byteBuffer = ByteBuffer
                            .allocateDirect(processedObjects
                                    .get(0)
                                    .getPixels().length);
                    byteBuffer.put(processedObjects
                            .get(0)
                            .getPixels());
                    byteBuffer.rewind();
                    GL11.glDrawPixels(WIDTH, HEIGHT, GL.GL_RGB, GL11.GL_UNSIGNED_BYTE,
                            byteBuffer);
                    Display.update();
                    isFirstImage = false;
                } else {

                    ByteBuffer render = updateBuffer(
                            processedObjects);

                    GL11.glDrawPixels(WIDTH, HEIGHT, GL.GL_RGB, GL11.GL_UNSIGNED_BYTE,
                            render);
                    Display.update();
//                    System.out.println("Displayed " + decompressingThreads.size());
                }

                decompressingThreads.removeAll(decompressingThreads);
                processedObjects.removeAll(processedObjects);
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
                    .put(getImage(processedObjects.get(0), processedObjects.get(1).getPixels(),
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

        return new ProcessedObject(newDepths, newPixels);
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