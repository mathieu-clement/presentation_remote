package com.mathieuclement.presentation.javasrv;

import java.awt.*;
import java.io.IOException;

public class ServerApp {
    private static final int DELAY = 100; // ms

    public static void main(String[] args) throws IOException, AWTException {
        UdpKeyboardServer server;
        if(args.length > 0) {
            server = new UdpKeyboardServer(Integer.parseInt(args[0]));
        } else {
            server = new UdpKeyboardServer();
        }

        Robot robot = new Robot();

        System.out.println("Listening for connections...");

        while(true) {
            ModifierAndKeyCode mk = server.receiveOne();
            int modifier = mk.getModifier();
            int keyCode = mk.getKeyCode();

            if(modifier != 0) {
                robot.keyPress(modifier);
                robot.delay(DELAY);
            }

            robot.keyPress(keyCode);
            robot.delay(DELAY);

            robot.keyRelease(keyCode);

            if(modifier != 0) {
                robot.delay(DELAY);
                robot.keyRelease(modifier);
            }
        }
    }
}
