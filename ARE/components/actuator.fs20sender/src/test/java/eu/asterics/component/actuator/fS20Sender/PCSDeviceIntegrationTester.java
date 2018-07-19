package eu.asterics.component.actuator.fS20Sender;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class PCSDeviceIntegrationTester {

    private static PCSDevice device;

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        setup();
        for(int i=0; i<100; i++) {
            device.send(1111, 1111, 28);
        }
        System.out.println("sleeping...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("continue...");
        for(int i=0; i<100; i++) {
            device.send(1111, 1111, 28);
        }
        device.close();
    }

    private static void setup() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        device = new PCSDevice();
        device.open();
    }
}
