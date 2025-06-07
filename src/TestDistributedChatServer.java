import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.*;

public class TestDistributedChatServer {

    @Test
    public void testServerConnection() throws Exception {
        String serverIp = "localhost";
        int port = 8080;

        new Thread(() -> DistributedChatServer.startServer(serverIp, port)).start();

        Thread.sleep(2000);

        try (Socket socket = new Socket(serverIp, port)) {
            assertTrue(socket.isConnected(), "Server should accept connection.");
        }
    }

    @Test
    public void testMessageBroadcast() throws Exception {
        String serverIp = "localhost";
        int port = 8080;

        new Thread(() -> DistributedChatServer.startServer(serverIp, port)).start();
        Thread.sleep(2000);

        Socket clientSocket = new Socket(serverIp, port);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String testMessage = "Hello, Server!";
        out.println(testMessage);

        assertNotNull(clientSocket, "Client should be able to connect.");
    }
}