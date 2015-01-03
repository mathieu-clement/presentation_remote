package com.mathieuclement.presentation.javasrv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.IllegalFormatCodePointException;

public class UdpKeyboardServer {
    private final int port;

    private DatagramSocket serverSocket;
    private DatagramPacket receivedPacket;
    private byte[] buffer = new byte[2];

    public UdpKeyboardServer(int port) throws SocketException {
        this.port = port;
        this.serverSocket = new DatagramSocket(port);
        this.receivedPacket = new DatagramPacket(buffer, buffer.length);
    }

    public UdpKeyboardServer() throws SocketException {
        this(12000);
    }

    public synchronized ModifierAndKeyCode receiveOne() throws IOException {
        serverSocket.receive(receivedPacket);
        int keyCode = receivedPacket.getData()[0];
        int modifier = receivedPacket.getData()[1];
        System.out.printf("UdpKeyboardServer: Received modifier %d / key code %d",
                modifier, keyCode);
        try {
            System.out.printf(" '%c')", keyCode);
        } catch (IllegalFormatCodePointException e) {
            // ignore
        }
        System.out.println(" from " + receivedPacket.getAddress().toString());
        return new ModifierAndKeyCode(modifier, keyCode);
    }
}
