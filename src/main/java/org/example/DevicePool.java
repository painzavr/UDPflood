package org.example;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;

public class DevicePool {
    public static void main(String[] args) throws IOException, InterruptedException {
        String ipRange = getLocalIp();
        System.out.println(getLocalIp());
        System.out.println(getDefaultGateway());
        ArrayList<String> devices = getLocalDevicesList(ipRange);
        printDevices(devices);

    }

    public static ArrayList<String> getLocalDevicesList(String ipRange) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long durationSecond = 20000;
        ArrayList<String> devices = new ArrayList<>();
        SubnetUtils subnetUtils = new SubnetUtils(ipRange);
        SubnetInfo subnetInfo = subnetUtils.getInfo();
        for (String ip : subnetInfo.getAllAddresses()) {
            if (System.currentTimeMillis() - startTime > durationSecond) break;
            try {
                InetAddress address = InetAddress.getByName(ip);
                if (address.isReachable(100)) {
                    devices.add(ip);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return devices;
    }

    public static void printDevices(ArrayList<String> devices) {
        for (String ip : devices) {
            System.out.println("Device found at IP: " + ip);
        }
    }

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                        System.out.println("Network Interface: " + networkInterface.getName());
                        System.out.println("IPv4 Address: " + address.getHostAddress());
                        System.out.println("Subnet Mask: " + getSubnetMask(networkInterface));
                        System.out.println();
                        return address.getHostAddress() + getSubnetMaskCIDR(networkInterface);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getSubnetMask(NetworkInterface networkInterface) throws SocketException {
        short prefixLength = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
        int subnetMask = 0xffffffff << (32 - prefixLength);
        return (subnetMask >> 24 & 0xff) + "." + (subnetMask >> 16 & 0xff) + "." +
                (subnetMask >> 8 & 0xff) + "." + (subnetMask & 0xff);
    }

    private static String getSubnetMaskCIDR(NetworkInterface networkInterface) throws SocketException {
        short prefixLength = networkInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength();
        return "/" + prefixLength;
    }

    public static String getDefaultGateway() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("ipconfig");
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.US_ASCII))) {
            String line;
            StringBuilder gateway = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                gateway.append(line);
            }
            String[] parts = new String(gateway).split(":");
            String defaultGetway = parts[parts.length - 1];
            defaultGetway = defaultGetway.trim();
            return defaultGetway;
        }
    }
}