import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class DistributedChatClientGUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public DistributedChatClientGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Distributed Chat Client");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(_ -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        messageField.addActionListener(_ -> sendMessage());

        frame.add(inputPanel, BorderLayout.SOUTH);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(_ -> quitChat());
        frame.add(quitButton, BorderLayout.NORTH);

        frame.setVisible(true);
        connectToServer();
    }

    private void connectToServer() {
        String serverIp = JOptionPane.showInputDialog(frame, "Enter server IP:");
        if (serverIp == null || serverIp.trim().isEmpty()) {
            return;
        }

        String portStr = JOptionPane.showInputDialog(frame, "Enter server port:");
        if (portStr == null || portStr.trim().isEmpty()) {
            return;
        }

        int serverPort;
        try {
            serverPort = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid port number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!tryConnecting(serverIp, serverPort)) {
            JOptionPane.showMessageDialog(frame, "No server found. Starting one...", "Info", JOptionPane.INFORMATION_MESSAGE);

            new Thread(() -> DistributedChatServer.startServer(serverIp, serverPort)).start();

            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

            tryConnecting(serverIp, serverPort);
        }
    }

    private boolean tryConnecting(String serverIp, int serverPort) {
        try {
            socket = new Socket(serverIp, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String id = JOptionPane.showInputDialog(frame, "Enter your ID:");
            if (id == null || id.trim().isEmpty()) {
                return false;
            }

            out.println(id);
            String response = in.readLine();

            if ("ID_IN_USE".equals(response)) {
                JOptionPane.showMessageDialog(frame, "This ID is already in use, please enter a different ID.", "ID Error", JOptionPane.ERROR_MESSAGE);
                closeConnection();
                return false;
            }

            chatArea.append("Connected to server.\n");
            new Thread(this::listenForMessages).start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                chatArea.append(message + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Disconnected from server.", "Connection Lost", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(message);
            messageField.setText("");

            if (message.startsWith("@")) {
                chatArea.append("[To " + message.split(" ")[0] + "] " + message.split(" ", 2)[1] + "\n");
            }
        }
    }

    private void quitChat() {
        if (out != null) {
            out.println("/quit");
        }
        closeConnection();
        SwingUtilities.invokeLater(() -> frame.dispose());
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error closing connection: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DistributedChatClientGUI::new);
    }
}