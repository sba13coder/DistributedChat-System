import javax.swing.*;

public class DistributedChatMainGUI {
    private JFrame frame;

    public DistributedChatMainGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Distributed Chat Main");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new java.awt.FlowLayout());

        JButton openClientButton = new JButton("Open New Client");
        openClientButton.addActionListener(_ -> openClient());
        frame.add(openClientButton);

        frame.setVisible(true);
    }

    private void openClient() {
        SwingUtilities.invokeLater(() -> {
            try {
                new DistributedChatClientGUI();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Failed to open client: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DistributedChatMainGUI::new);
    }
}