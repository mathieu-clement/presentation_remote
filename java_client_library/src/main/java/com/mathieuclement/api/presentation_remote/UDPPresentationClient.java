package com.mathieuclement.api.presentation_remote;

import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPPresentationClient {

    private final DatagramSocket socket;
    private final String serverHost;

    public UDPPresentationClient(String serverHost, int port) throws SocketException {
        socket = new DatagramSocket(port);
        this.serverHost = serverHost;
    }

    public void sendKey(KeyCode keyCode, Modifier modifier) {

    }


}
