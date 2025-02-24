import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ShortcutListener {

    private static final String SHORTCUTS_FILE = "shortcuts.txt";
    private static boolean editMode = false; // Tracks if user pressed CTRL+E after opening shortcuts

    public static void main(String[] args) {
        new KeyboardHook().startListening();
    }

    public String getActiveWindowTitle() {
        char[] buffer = new char[1024];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        int textLength = User32.INSTANCE.GetWindowText(hwnd, buffer, 1024);

        if (textLength > 0) {
            String title = Native.toString(buffer).trim();
            System.out.println("Active Window: " + title);

            // Normalize known applications
            if (title.toLowerCase().contains("firefox")) {
                return "Mozilla Firefox";
            } else if (title.toLowerCase().contains("chrome")) {
                return "Google Chrome";
            } else if (title.toLowerCase().contains("notepad")) {
                return "Notepad";
            }

            // If no exact match, return only the first part of the title
            return title.split("—|\\-")[0].trim(); // Splits title using "—" or "-"
        }
        return "Unknown";
    }


    public void displayShortcuts(String appName) {
        Map<String, List<String>> shortcuts = loadShortcuts();
        List<String> appShortcuts = shortcuts.getOrDefault(appName, new ArrayList<>());

        if (appShortcuts.isEmpty()) {
            showPopup("No shortcuts available.", appName);
        } else {
            String formattedShortcuts = "<html><div style='padding:10px; border-radius:15px; border:2px solid #000;'>" +
                    String.join("<br>", appShortcuts) + "</html>";
            showPopup(formattedShortcuts, appName);
        }
    }

    public void openShortcutEditor() {
        SwingUtilities.invokeLater(() -> {
            JFrame editorFrame = new JFrame("Edit Shortcuts");
            editorFrame.setSize(500, 400);
            editorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            editorFrame.setLayout(new BorderLayout());

            JTextArea textArea = new JTextArea();
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));

            // Load existing shortcuts
            try (BufferedReader reader = new BufferedReader(new FileReader(SHORTCUTS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n");
                }
            } catch (IOException e) {
                textArea.setText("# Example:\nNotepad, Ctrl+S - Save File");
            }

            editorFrame.add(new JScrollPane(textArea), BorderLayout.CENTER);

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(SHORTCUTS_FILE))) {
                    writer.write(textArea.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                editorFrame.dispose();
            });

            JPanel bottomPanel = new JPanel();
            bottomPanel.add(saveButton);
            editorFrame.add(bottomPanel, BorderLayout.SOUTH);

            editorFrame.setVisible(true);
        });
    }

    private Map<String, List<String>> loadShortcuts() {
        Map<String, List<String>> shortcutsMap = new HashMap<>();
        File shortcutFile = new File(SHORTCUTS_FILE);

        if (!shortcutFile.exists()) {
            System.out.println("shortcuts.txt not found in " + shortcutFile.getAbsolutePath());
            return shortcutsMap;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(shortcutFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split(",", 2);
                    if (parts.length == 2) {
                        String app = parts[0].trim();
                        String shortcut = parts[1].trim();
                        shortcutsMap.computeIfAbsent(app, k -> new ArrayList<>()).add(shortcut);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shortcutsMap;
    }

    private void showPopup(String message, String appName) {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);

        // Create panel with modern styling
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 240, 240, 220)); // Light gray with transparency
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Rounded corners
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Add drop shadow effect
        dialog.getRootPane().setOpaque(false);
        dialog.getContentPane().setBackground(new Color(0, 0, 0, 0)); // Transparent background
        dialog.setBackground(new Color(0, 0, 0, 0)); // Transparent background

        // Top bar with title and close button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(appName + " Shortcuts", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setMargin(new Insets(2, 5, 2, 5));
        closeButton.addActionListener(e -> dialog.dispose());

        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(closeButton, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Create modern table layout for shortcuts
        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setBackground(new Color(255, 255, 255, 220));
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        String formattedMessage = message.replaceAll("<html>|</html>", "").trim();
        if (formattedMessage.startsWith("<div")) {
            formattedMessage = formattedMessage.substring(formattedMessage.indexOf('>') + 1).trim();
        }
        String[] shortcuts = formattedMessage.split("<br>");

        int row = 0;
        for (String shortcut : shortcuts) {
            if (!shortcut.trim().isEmpty()) {
                String[] parts = shortcut.split(" - ", 2);
                if (parts.length == 2) {
                    JPanel shortcutBox = new JPanel();
                    shortcutBox.setBackground(new Color(200, 200, 200));
                    shortcutBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
                    shortcutBox.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
                    shortcutBox.setPreferredSize(new Dimension(100, 30));
                    shortcutBox.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.BLACK, 1, true),
                            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

                    JLabel shortcutLabel = new JLabel(parts[0]);
                    shortcutLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    shortcutBox.add(shortcutLabel);

                    JLabel descriptionLabel = new JLabel(parts[1]);
                    descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                    gbc.gridx = 0;
                    gbc.gridy = row;
                    gbc.weightx = 0.3;
                    tablePanel.add(shortcutBox, gbc);

                    gbc.gridx = 1;
                    gbc.weightx = 0.7;
                    tablePanel.add(descriptionLabel, gbc);

                    row++;
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Info label for editing shortcuts
        JLabel editLabel = new JLabel("Press CTRL+E to edit shortcuts", SwingConstants.CENTER);
        editLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        editLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        panel.add(editLabel, BorderLayout.SOUTH);

        dialog.getContentPane().add(panel);
        dialog.setSize(450, 350);

        // Get screen size and position at bottom-right
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int padding = 20; // Margin from edges
        int x = screenSize.width - dialog.getWidth() - padding;
        int y = screenSize.height - dialog.getHeight() - padding;
        dialog.setLocation(x, y);

        dialog.setVisible(true);
        dialog.toFront();
        dialog.requestFocus();

        // Allow edit mode trigger
        editMode = true;
        new javax.swing.Timer(4000, e -> {
            if (!editMode) {
                dialog.dispose();
            }
        }).start();
    }






    public void handleEditRequest() {
        if (editMode) {
            openShortcutEditor();
            editMode = false;
        }
    }
}
