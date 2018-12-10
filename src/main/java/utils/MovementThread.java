package utils;

import sender.MessageSender;

import java.util.concurrent.ExecutorService;

import static main.Master.needToProcess;

public class MovementThread implements Runnable{
    MessageSender messageSender;
    ExecutorService executorService;

    @Override
    public void run() {
        synchronized (needToProcess) {
            while (true) {
//                int key = MovementUtils.getKey();
                int key = 0;
                System.out.println("Pressed key: " + key);
                if(key != 0) {
                    needToProcess = true;
                    messageSender.setKey(key);
                    executorService.execute(messageSender);
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public MovementThread setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public MessageSender getMessageSender() {
        return messageSender;
    }

    public MovementThread setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
        return this;
    }
}
