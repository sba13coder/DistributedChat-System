import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.*;

public class TestDistributedChatClientGUI {

    @Test
    public void testClientConnection() throws Exception {
        String serverIp = "localhost";
        int port = 8080;

        new Thread(() -> DistributedChatServer.startServer(serverIp, port)).start();
        Thread.sleep(2000);

        Socket socket = new Socket(serverIp, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String testMessage = "Hello from client!";
        out.println(testMessage);

        assertNotNull(socket, "Socket should be connected.");
    }
}