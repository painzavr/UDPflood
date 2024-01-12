package org.example;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class LoadSynchronization {
    public static int getThreadsValue() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        int clockSpeed = (int) (processor.getMaxFreq()/1_000_000);
        int processorCount = processor.getLogicalProcessorCount();
        int threadsNumber = (int) Math.floor(clockSpeed * processorCount / 156.0);
        if(threadsNumber>150){
            System.out.println("Number of optimal threads is - " + threadsNumber + " , but saving better program efficiency, the number of threads is limited to 150");
            threadsNumber=150;
        }
        System.out.println();
        return threadsNumber;
    }
}








