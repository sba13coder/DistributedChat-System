import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class DistributedChatServer {
    private static final Coordinator coordinator = new Coordinator();
    private static final Map<String, PrintWriter> clientWriters = new HashMap<>();
    private static final Logger logger = Logger.getLogger(DistributedChatServer.class.getName());

    public static void startServer(String ip, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip))) {
            System.out.println("Server started on " + ip + ":" + port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private String clientId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                clientId = in.readLine();

                synchronized (coordinator) {
                    for (Member member : coordinator.getMembers()) {
                        if (member.getId().equals(clientId)) {
                            out.println("ID_IN_USE");
                            out.flush();
                            socket.close();
                            return;
                        }
                    }
                }

                String ip = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();
                Member newMember = new Member(clientId, ip, port);
                coordinator.addMember(newMember);

                synchronized (clientWriters) {
                    clientWriters.put(clientId, out);
                }

                out.println("Coordinator: " + coordinator.getCoordinator());
                out.flush();
                System.out.println(clientId + " joined. Coordinator info sent.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break;
                    } else if (message.equalsIgnoreCase("/members")) {
                        out.println(coordinator.getMemberDetails());
                        continue;
                    }
                    handleMessage(clientId, message);
                }
            } catch (IOException e) {
                logger.severe("Error handling client: " + e.getMessage());
                logger.log(Level.SEVERE, "Exception details:", e);
            } finally {
                boolean wasMember = coordinator.removeMember(clientId);

                synchronized (clientWriters) {
                    clientWriters.remove(clientId);
                }

                if (wasMember) {
                    broadcastMessage(clientId + " has left the chat.");
                }
            }
        }
    }

    private static void handleMessage(String sender, String message) {
        if (message.startsWith("@")) {
            String[] parts = message.split(" ", 2);
            if (parts.length > 1) {
                String targetUser = parts[0].substring(1); // Extract username after "@"
                String privateMessage = "[Private] " + sender + ": " + parts[1];
                sendPrivateMessage(targetUser, privateMessage);
            }
        } else {
            broadcastMessage(sender + ": " + message);
        }
    }

    private static void sendPrivateMessage(String recipient, String message) {
        synchronized (clientWriters) {
            PrintWriter writer = clientWriters.get(recipient);
            if (writer != null) {
                writer.println(message);
                writer.flush();
            } else {
                System.out.println("User " + recipient + " not found for private message.");
            }
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(message);
                writer.flush();
            }
        }
    }

    public static void broadcastNewCoordinator() {
        if (coordinator.getCoordinator() != null) {
            String message = "New Coordinator: " + coordinator.getCoordinator();
            System.out.println("Broadcasting: " + message);
            broadcastMessage(message);
        }
    }
}