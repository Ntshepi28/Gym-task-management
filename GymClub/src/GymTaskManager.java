import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.filechooser.*;

public class GymTaskManager extends JFrame {
    private Database database;
    private JPanel coverPanel;
    private JPanel mainPanel;
    private JButton nextButton;
    private Registration currentUser;
    private ArrayList<Registration> registeredUsers;
    private JLabel taskNameToDeleteLabel;
    private JTextField taskNameToDeleteField;

    public GymTaskManager() {
        database = new Database();
        registeredUsers = new ArrayList<>();
        createCoverPage();
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createCoverPage() {
        coverPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("C:\\Users\\dell\\Downloads\\GymClub\\GymClub\\src\\workout.jpg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        coverPanel.setLayout(new BorderLayout());
        coverPanel.setBackground(Color.DARK_GRAY);

        JLabel welcomeLabel = new JLabel("Welcome to Gym Fitness Club", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);

        JLabel motivationLabel = new JLabel("<html><div style='text-align: center;'>The body achieves what the mind believes.<br>- Napoleon Hill</div></html>", JLabel.CENTER);
        motivationLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        motivationLabel.setForeground(Color.WHITE);

        nextButton = new JButton("Next");
        styleButton(nextButton, new Color(200, 0, 200), Color.WHITE); 
        nextButton.addActionListener(e -> showMainPage());

        coverPanel.add(welcomeLabel, BorderLayout.NORTH);
        coverPanel.add(motivationLabel, BorderLayout.CENTER);
        coverPanel.add(nextButton, BorderLayout.SOUTH);

        add(coverPanel);
    }

    private void showMainPage() {
        remove(coverPanel);
        mainPanel = createMainPagePanel();
        add(mainPanel);
        revalidate();
        repaint();
    }

    private JPanel createMainPagePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#6e9ca5")); 
        panel.setLayout(new GridLayout(11, 2, 10, 10)); // Added spacing between components
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<String> taskNameComboBox = new JComboBox<>();
        taskNameComboBox.addItem("Push-ups");
        taskNameComboBox.addItem("Bicycle crunches");
        taskNameComboBox.addItem("Squats");
        taskNameComboBox.addItem("Mountain climb");

        JTextArea descriptionField = new JTextArea();
        descriptionField.setLineWrap(true);

        JComboBox<String> categoryField = new JComboBox<>();
        categoryField.addItem("Strength Training");
        categoryField.addItem("Bodyweight Exercise");
        categoryField.addItem("Abdominal Exercise");
        categoryField.addItem("Lower body Exercise");

        // Initialize delete components (hidden by default)
        taskNameToDeleteLabel = new JLabel("Task Name to Delete:");
        taskNameToDeleteField = new JTextField();
        taskNameToDeleteLabel.setVisible(false);
        taskNameToDeleteField.setVisible(false);

        // Primary action buttons 
        JButton addButton = new JButton("Add Task");
        styleButton(addButton, Color.decode("#6e9ca5")); 

        JButton saveButton = new JButton("Save Tasks");
        styleButton(saveButton, Color.decode("#3a9bad"));

       JButton loadButton = new JButton("Load Tasks");
       styleButton(loadButton, Color.decode("#8d6394"));

       JButton registrationButton = new JButton("Registration");
       styleButton(registrationButton, Color.decode("#a83a7b"));

       // Secondary action buttons 
       JButton deleteButton = new JButton("Delete Task");
       styleButton(deleteButton, Color.decode("#9b95c1")); 

       JButton viewButton = new JButton("View Tasks");
       styleButton(viewButton, Color.decode("#a83a7b"));

       JButton exportCSVButton = new JButton("Export CSV");
       styleButton(exportCSVButton, Color.decode("#83d1ce"));

       JButton markCompleteButton = new JButton("Mark Complete");
        styleButton(markCompleteButton, Color.decode("#3a9bad"));

       JButton loginButton = new JButton("Login");
       styleButton(loginButton, Color.decode("#244e56"));

        // Add components to panel
        panel.add(new JLabel("Task Name:"));
        panel.add(taskNameComboBox);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionField));
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(taskNameToDeleteLabel);
        panel.add(taskNameToDeleteField);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(viewButton);
        panel.add(saveButton);
        panel.add(loadButton);
        panel.add(exportCSVButton);
        panel.add(markCompleteButton);
        panel.add(registrationButton);
        panel.add(loginButton);

        // Add action listeners
        addButton.addActionListener(e -> addTask(taskNameComboBox, descriptionField, categoryField));

        deleteButton.addActionListener(e -> {
            if (!taskNameToDeleteField.isVisible()) {
                // First click - show the delete field
                taskNameToDeleteLabel.setVisible(true);
                taskNameToDeleteField.setVisible(true);
                taskNameToDeleteField.setText("");
                panel.revalidate();
                panel.repaint();
            } else {
                // Second click - perform deletion
                String taskNameToDelete = taskNameToDeleteField.getText().trim();
                if (!taskNameToDelete.isEmpty()) {
                    boolean taskDeleted = database.deleteTaskByName(taskNameToDelete);
                    if (taskDeleted) {
                        JOptionPane.showMessageDialog(this, "Task deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Task not found.");
                    }
                }
                // Hide the delete field after operation
                taskNameToDeleteLabel.setVisible(false);
                taskNameToDeleteField.setVisible(false);
                panel.revalidate();
                panel.repaint();
            }
        });

        viewButton.addActionListener(e -> viewTasks());
        saveButton.addActionListener(e -> saveTasksToFile());
        loadButton.addActionListener(e -> loadTasksFromFile());
        exportCSVButton.addActionListener(e -> exportTasksToCSV());
        markCompleteButton.addActionListener(e -> markTaskComplete(taskNameComboBox));
        registrationButton.addActionListener(e -> {
            if (database.getTasks().isEmpty()) {
                JOptionPane.showMessageDialog(this, "You must add a task before registering.");
            } else {
                showRegistrationPage();
            }
        });
        loginButton.addActionListener(e -> showLoginPage());

        return panel;
    }

    private void addTask(JComboBox<String> taskNameComboBox, JTextArea descriptionField, JComboBox<String> categoryField) {
        String taskName = (String) taskNameComboBox.getSelectedItem();
        String description = descriptionField.getText();
        String category = (String) categoryField.getSelectedItem();

        Task task = new Task(0, taskName, description, category, false);
        database.addTask(task);

        JOptionPane.showMessageDialog(this, "Task added successfully!");
    }

    private void viewTasks() {
        StringBuilder taskList = new StringBuilder("Tasks:\n");
        for (Task task : database.getTasks()) {
            taskList.append(task).append("\n");
        }
        JOptionPane.showMessageDialog(this, taskList.toString());
    }

    private void saveTasksToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Tasks");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (.txt)", "txt"));
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
                for (Task task : database.getTasks()) {
                    writer.write(task.toString());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Tasks saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving tasks.");
            }
        }
    }

    private void loadTasksFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Tasks");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (.txt)", "txt"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] taskData = line.split(",");
                    if (taskData.length == 4) {
                        String taskName = taskData[0];
                        String description = taskData[1];
                        String category = taskData[2];
                        boolean isCompleted = Boolean.parseBoolean(taskData[3]);
                        Task task = new Task(0, taskName, description, category, isCompleted);
                        database.addTask(task);
                    }
                }
                JOptionPane.showMessageDialog(this, "Tasks loaded successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading tasks.");
            }
        }
    }

    private void exportTasksToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Tasks to CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (.csv)", "csv"));
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
                writer.write("Task Name,Description,Category,Completed\n");
                for (Task task : database.getTasks()) {
                    writer.write(task.getTaskName() + "," + task.getDescription() + "," + task.getCategory() + "," + task.isCompleted() + "\n");
                }
                JOptionPane.showMessageDialog(this, "Tasks exported to CSV successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting tasks.");
            }
        }
    }

    private void markTaskComplete(JComboBox<String> taskNameComboBox) {
        String taskName = (String) taskNameComboBox.getSelectedItem();
        Task task = database.getTaskByName(taskName);
        if (task != null) {
            task.setCompleted(true);
            JOptionPane.showMessageDialog(this, "Task marked as complete!");
        } else {
            JOptionPane.showMessageDialog(this, "Task not found.");
        }
    }

    private void showRegistrationPage() {
        RegistrationPage registrationPage = new RegistrationPage(database);
        registrationPage.setGymTaskManager(this);
        JFrame registrationFrame = new JFrame("Registration");
        registrationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registrationFrame.setSize(400, 400);
        registrationFrame.add(registrationPage);
        registrationFrame.setVisible(true);
    }

    private void showLoginPage() {
        JDialog loginDialog = new JDialog();
        loginDialog.setTitle("Gym Member Login");
        loginDialog.setModal(true);
        loginDialog.setLayout(new BorderLayout());
        loginDialog.getContentPane().setBackground(new Color(245, 248, 250));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new DropShadowBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Member Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 120, 215));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField nameField = new JTextField(20);
        styleTextField(nameField);

        JLabel cellLabel = new JLabel("Cell Number:");
        cellLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField cellField = new JTextField(20);
        styleTextField(cellField);

        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(nameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(cellLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(cellField, gbc);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(0, 120, 215));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        loginDialog.add(mainPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String cellNumber = cellField.getText().trim();

            if (name.isEmpty() || cellNumber.isEmpty()) {
                JOptionPane.showMessageDialog(loginDialog,
                        "Please enter both your Name and Cell Number.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Registration user = findUserByNameAndCell(name, cellNumber);

            if (user == null) {
                JOptionPane.showMessageDialog(loginDialog,
                        "Invalid Name or Cell Number. Please check your credentials.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                currentUser = user;
                JOptionPane.showMessageDialog(loginDialog,
                        "Welcome, " + user.getName() + "!\nYour tasks: " + user.getTasks(),
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                loginDialog.dispose();
            }
        });

        loginDialog.pack();
        loginDialog.setLocationRelativeTo(this);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.setVisible(true);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                        BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

    private Registration findUserByNameAndCell(String name, String cellNumber) {
        for (Registration user : registeredUsers) {
            if (user.getName().equalsIgnoreCase(name.trim()) &&
                    user.getCellNumber().equals(cellNumber.replaceAll("[^0-9]", ""))) {
                return user;
            }
        }
        return null;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(background.darker(), 2),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(background.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
    }

    private static class DropShadowBorder extends AbstractBorder {
        private static final int SHADOW_SIZE = 5;
        private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < SHADOW_SIZE; i++) {
                g2.setColor(new Color(SHADOW_COLOR.getRed(), SHADOW_COLOR.getGreen(),
                        SHADOW_COLOR.getBlue(), SHADOW_COLOR.getAlpha() / SHADOW_SIZE * (i + 1)));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 5, 5);
            }

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x + SHADOW_SIZE, y + SHADOW_SIZE,
                    width - SHADOW_SIZE * 2, height - SHADOW_SIZE * 2, 5, 5);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE, SHADOW_SIZE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GymTaskManager();
            } catch (Exception ex) {
                Logger.getLogger(GymTaskManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void registerUser(Registration registration) {
        registeredUsers.add(registration);
        System.out.println("User registered: " + registration.getName() + " " + registration.getSurname());
    }
}