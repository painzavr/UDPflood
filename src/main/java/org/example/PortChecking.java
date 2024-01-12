package org.example;

import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class PortChecking {
    private Instant start;
    public static ArrayList<Thread> threads = new ArrayList<>();
    public static volatile int openPort=0;

    private String host;
    private static int START_PORT = 1;
    private static int END_PORT = 65535;
    private static int NUM_THREADS = 500;
    public PortChecking(String host){
        this.host = host;
    }
    public int getOpenPort() throws InterruptedException {

        for (int i = 0; i < NUM_THREADS; i++) {
            int threadStart = START_PORT + (i * (END_PORT - START_PORT) / NUM_THREADS);
            int threadEnd = START_PORT + ((i + 1) * (END_PORT - START_PORT) / NUM_THREADS) - 1;
            Thread thread = new Thread(new PortScanner(this.host, threadStart, threadEnd));
            threads.add(thread);
            thread.start();
        }
        start = Instant.now();
        while (true){
            if((openPort != 0) ){
                return openPort;
            }
            if(Duration.between(Instant.now(),start).getSeconds()>60)break;
        }
        return 0;
    }
    static class PortScanner implements Runnable {
        private String host;
        private int startPort;
        private int endPort;


        public PortScanner(String host, int startPort, int endPort) {
            this.host = host;
            this.startPort = startPort;
            this.endPort = endPort;
        }


        @Override
        public void run() {
            for (int port = startPort; port <= endPort; port++) {
                try {
                    Socket socket = new Socket(host, port);
                    socket.close();
                    openPort = port;
                    break;
                } catch (Exception e) {
                    if(openPort != 0)break;
                }
            }
        }
    }
}


