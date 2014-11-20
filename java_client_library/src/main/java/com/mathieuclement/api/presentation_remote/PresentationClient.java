package com.mathieuclement.api.presentation_remote;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Control a remote PowerPoint instance with keyboard shortcuts
 * send over the network using a UDP connection.
 */
public class PresentationClient extends UDPKeyboardEmulatorClient {
    public PresentationClient(String serverHost, int serverPort) throws SocketException, UnknownHostException {
        super(serverHost, serverPort);
    }

    public PresentationClient(String serverHost) throws SocketException, UnknownHostException {
        super(serverHost);
    }

    // ----------------------------------------
    // Commands as found on:
    // https://support.office.com/en-us/article/Use-keyboard-shortcuts-to-deliver-your-presentation-1524ffce-bd2a-45f4-9a7f-f18b992b93a0
    // ----------------------------------------

    public void nextSlideOrAnimation() throws IOException {
        sendKey(KeyCode.RIGHT);
    }

    public void previousSlideOrAnimation() throws IOException {
        sendKey(KeyCode.LEFT);
    }

    public void goToSlide(int slideNumber) throws IOException {
        sendString(Integer.toString(slideNumber));
        sendKey(KeyCode.ENTER);
    }

    public void goToFirstSlide() throws IOException {
        sendKey(KeyCode.HOME);
    }

    public void toggleScreenBlack() throws IOException {
        sendKey(KeyCode.PERIOD);
    }

    public void toggleScreenWhite() throws Exception {
        sendKey(KeyCode.COMMA);
    }

    public void endPresentation() throws Exception {
        sendKey(KeyCode.ESCAPE);
    }

    public void eraseOnScreenAnnotations() throws Exception {
        sendKey(KeyCode.createFromLetter('E'));
    }
}
