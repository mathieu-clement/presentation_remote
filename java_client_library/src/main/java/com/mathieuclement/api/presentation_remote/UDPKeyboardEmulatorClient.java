package com.mathieuclement.api.presentation_remote;

import java.io.IOException;
import java.net.*;

public class UDPKeyboardEmulatorClient {

    private static final int BYTES_FOR_KEYCODE_AND_MODIFIER = 2;

    private final DatagramSocket socket;
    private final int serverPort;
    private final InetAddress serverInetAddress;

    /**
     * Create a client over the UDP protocol for the Presentation Server.
     *
     * @param serverHost Server host or  {@code null} for loopback.
     * @param serverPort Server port
     * @throws SocketException      as thrown by {@link java.net.InetAddress#getByName(String)}
     * @throws UnknownHostException if IP address cannot be found for serverHost
     */
    public UDPKeyboardEmulatorClient(String serverHost, int serverPort) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        this.serverInetAddress = InetAddress.getByName(serverHost);
        this.serverPort = serverPort;
    }

    /**
     * Create a client over the UDP protocol for the Presentation Server,
     * on port 12000 by default.
     *
     * @param serverHost Server host or  {@code null} for loopback.
     * @throws SocketException      as thrown by {@link java.net.InetAddress#getByName(String)}
     * @throws UnknownHostException if IP address cannot be found for serverHost
     */
    public UDPKeyboardEmulatorClient(String serverHost) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        this.serverInetAddress = InetAddress.getByName(serverHost);
        this.serverPort = 12000;
    }


    /**
     * Send key to server.
     *
     * @param keyCode  Key code
     * @param modifier Modifier. Use {@link com.mathieuclement.api.presentation_remote.Modifier#NONE}
     *                 if unused.
     * @throws IOException as thrown by {@link java.net.DatagramSocket#send(java.net.DatagramPacket)}
     */
    public void sendKey(KeyCode keyCode, Modifier modifier) throws IOException {
        byte[] data = new byte[BYTES_FOR_KEYCODE_AND_MODIFIER];
        // set modifier and keycode as UDP payload
        data[0] = (byte) (keyCode.getVkCode() & 0xff);
        data[1] = (byte) (modifier.getVkCode() & 0xff);
        DatagramPacket packet = new DatagramPacket(data, data.length,
                serverInetAddress, serverPort);
        socket.send(packet);
    }

    /**
     * Send key to server, without modifier.
     *
     * @param keyCode Key code
     * @throws IOException as thrown by {@link java.net.DatagramSocket#send(java.net.DatagramPacket)}
     */
    public void sendKey(KeyCode keyCode) throws IOException {
        sendKey(keyCode, Modifier.NONE);
    }

    /**
     * Send characters of string, one after the other.
     *
     * @param str String to send
     */
    public void sendString(String str) throws IOException {
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            try {
                sendKey(KeyCode.createFromChar(aChar),
                        Character.isUpperCase(aChar) ? Modifier.SHIFT : Modifier.NONE);
            } catch (IllegalArgumentException iae) {
                throw new IOException("Could not send character '" + aChar + "'.", iae);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UDPKeyboardEmulatorClient client = new UDPKeyboardEmulatorClient("localhost", 12000);
            client.sendKey(KeyCode.LEFT_WINDOWS);
            client.sendString("Bryan est un petit fou.");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
