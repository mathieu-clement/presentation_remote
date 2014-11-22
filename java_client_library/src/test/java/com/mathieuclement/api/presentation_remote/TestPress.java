package com.mathieuclement.api.presentation_remote;

import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by mathieu on 22.11.2014.
 */
public class TestPress {
    @Test
    public void testTypeSteve() throws Exception {
        // 1. Show an input dialog
        // 2. Send "Steve" then Enter over the network.
        // 3. If dialog closes and the input was indeed "Steve" (with the capital S), test passed.

        MyRunnable runnable = new MyRunnable();
        Thread thread = new Thread(runnable);
        thread.start();

        Thread.sleep(1000); // Wait till dialog is displayed

        UDPKeyboardEmulatorClient client = new UDPKeyboardEmulatorClient("localhost");
        client.sendString("Steve");
        client.sendKey(KeyCode.ENTER);

        Thread.sleep(1000);
        thread.interrupt();

        assertTrue(runnable.hasFinished());
        assertEquals("Steve", runnable.getOutput());
    }

    static class MyRunnable implements Runnable {
        private String output;
        private boolean hasFinished = false;

        @Override
        public void run() {
            JFrame frame = new JFrame();
            frame.setVisible(true);
            frame.setAlwaysOnTop(true);
            output = JOptionPane.showInputDialog(frame, "TestPress JUnit. This will close automatically.");
            frame.dispose();
            hasFinished = true;
        }

        public String getOutput() {
            return output;
        }

        public boolean hasFinished() {
            return hasFinished;
        }
    }
}
