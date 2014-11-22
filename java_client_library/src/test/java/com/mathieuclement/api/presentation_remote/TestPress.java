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
        String text = "Steve is a very common first name.";
        client.sendString(text);
        client.sendKey(KeyCode.ENTER);

        int maxTime = text.length(); // seconds. One second per letter.

        int nbSeconds = 0;
        while (!runnable.hasFinished() && nbSeconds < maxTime) {
            Thread.sleep(1000); // Give it enough time to type the words
            nbSeconds++;
        }
        if (nbSeconds > maxTime) {
            thread.interrupt();
        }

        assertTrue(runnable.hasFinished());
        assertEquals(text, runnable.getOutput());
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
