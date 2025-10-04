import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class RegistrationPage extends JPanel {
    private GymTaskManager gymTaskManager;
    private Database database;
    private Color primaryColor = new Color(0, 120, 215); // Blue
    private Color secondaryColor = new Color(240, 240, 240); // Light gray
    private Color backgroundColor = new Color(245, 248, 250); // Very light blue-gray background
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

    public RegistrationPage(Database database) {
        this.database = database;
        setBackground(backgroundColor); // Set the background color
        createRegistrationPage();
    }

    private void createRegistrationPage() {
        // Set layout with some padding
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); // More padding
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create a container panel with background color and shadow
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new DropShadowBorder(),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Create form title
        JLabel titleLabel = new JLabel("Gym Membership Registration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(primaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Reset gridbag constraints
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Create form components
        JLabel nameLabel = createStyledLabel("Name:");
        JTextField nameField = createStyledTextField();

        JLabel surnameLabel = createStyledLabel("Surname:");
        JTextField surnameField = createStyledTextField();

        JLabel idNumberLabel = createStyledLabel("ID Number (13 digits):");
        JTextField idNumberField = createStyledTextField();
        idNumberField.setToolTipText("Enter 13-digit South African ID number");

        JLabel ageLabel = createStyledLabel("Age:");
        JTextField ageField = createStyledTextField();
        ageField.setEditable(false);
        ageField.setBackground(secondaryColor);

        JLabel cellNumberLabel = createStyledLabel("Cell Number:");
        JTextField cellNumberField = createStyledTextField();

        JLabel addressLabel = createStyledLabel("Address:");
        JTextField addressField = createStyledTextField();

        JLabel occupationLabel = createStyledLabel("Occupation:");
        String[] occupations = {"Select occupation", "Student", "Employed", "Unemployed"};
        JComboBox<String> occupationCombo = new JComboBox<>(occupations);
        styleComboBox(occupationCombo);

        // Add listener to calculate age when ID number changes
        idNumberField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateAge(); }
            public void removeUpdate(DocumentEvent e) { updateAge(); }
            public void insertUpdate(DocumentEvent e) { updateAge(); }

            private void updateAge() {
                String idNumber = idNumberField.getText().trim();
                if (idNumber.length() == 13) {
                    try {
                        int age = calculateAgeFromID(idNumber);
                        ageField.setText(String.valueOf(age));
                    } catch (Exception ex) {
                        ageField.setText("");
                    }
                } else {
                    ageField.setText("");
                }
            }
        });

        // Create buttons
        JButton registerButton = createStyledButton("Register", primaryColor);
        JButton doneButton = createStyledButton("Done", new Color(76, 175, 80)); // Green

        // Add action listeners
        registerButton.addActionListener(e -> registerUser(
                nameField, surnameField, idNumberField, ageField,
                cellNumberField, addressField, occupationCombo
        ));

        doneButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(RegistrationPage.this, "Registration successful!");
            Window window = SwingUtilities.getWindowAncestor(RegistrationPage.this);
            if (window != null) {
                window.dispose();
            }
        });

        // Add components to form panel
        int row = 1; // Start after title
        addFormRow(formPanel, gbc, row++, nameLabel, nameField);
        addFormRow(formPanel, gbc, row++, surnameLabel, surnameField);
        addFormRow(formPanel, gbc, row++, idNumberLabel, idNumberField);
        addFormRow(formPanel, gbc, row++, ageLabel, ageField);
        addFormRow(formPanel, gbc, row++, cellNumberLabel, cellNumberField);
        addFormRow(formPanel, gbc, row++, addressLabel, addressField);

        // Occupation row
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(occupationLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(occupationCombo, gbc);
        row++;

        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(registerButton);
        buttonPanel.add(doneButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Add form panel to main panel with some padding
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(30, 30, 30, 30);
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.NONE;
        add(formPanel, mainGbc);
    }

    // Helper method to add a form row
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(fieldFont);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(primaryColor, 2),
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

        return field;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(fieldFont);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    public void setGymTaskManager(GymTaskManager gymTaskManager) {
    }

    // Custom shadow border for the form panel
    private static class DropShadowBorder extends AbstractBorder {
        private static final int SHADOW_SIZE = 5;
        private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw shadow
            for (int i = 0; i < SHADOW_SIZE; i++) {
                g2.setColor(new Color(SHADOW_COLOR.getRed(), SHADOW_COLOR.getGreen(),
                        SHADOW_COLOR.getBlue(), SHADOW_COLOR.getAlpha() / SHADOW_SIZE * (i + 1)));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 5, 5);
            }

            // Draw white background
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

    private void registerUser(JTextField nameField, JTextField surnameField, JTextField idNumberField,
                              JTextField ageField, JTextField cellNumberField, JTextField addressField,
                              JComboBox<String> occupationCombo) {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String idNumber = idNumberField.getText().trim();
        String ageText = ageField.getText().trim();
        String cellNumber = cellNumberField.getText().trim();
        String address = addressField.getText().trim();
        String occupation = (String) occupationCombo.getSelectedItem();

        try {
            // Validate required fields
            if (name.isEmpty() || surname.isEmpty() || idNumber.isEmpty() ||
                    cellNumber.isEmpty() || address.isEmpty()) {
                throw new IllegalArgumentException("Please fill in all required fields");
            }

            // Validate ID number
            if (idNumber.length() != 13) {
                throw new IllegalArgumentException("ID Number must be exactly 13 digits");
            }

            // Validate occupation selection
            if ("Select occupation".equals(occupation)) {
                throw new IllegalArgumentException("Please select an occupation");
            }

            // Check age (should be auto-calculated and valid)
            if (ageText.isEmpty()) {
                throw new IllegalArgumentException("Invalid ID number - could not calculate age");
            }
            int age = Integer.parseInt(ageText);
            if (age < 18) {
                throw new IllegalArgumentException("You must be 18 years or older to register");
            }

            // Check database connection
            if (database == null) {
                throw new IllegalStateException("Database connection not available");
            }

            // Check for existing user
            if (database.userExists(name, surname)) {
                throw new IllegalArgumentException("User already exists in the system");
            }

            // Save registration
            database.saveRegisteredUser(name, surname, idNumber, age, cellNumber, address, occupation);

            // Show success and clear form
            JOptionPane.showMessageDialog(this,
                    "Registration successful for: " + name + " " + surname,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm(nameField, surnameField, idNumberField, ageField,
                    cellNumberField, addressField, occupationCombo);

        } catch (NumberFormatException e) {
            showError("Please enter valid information in all fields");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    private int calculateAgeFromID(String idNumber) throws Exception {
        String yearStr = idNumber.substring(0, 2);
        String monthStr = idNumber.substring(2, 4);
        String dayStr = idNumber.substring(4, 6);

        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);

        // Determine century (00-21 assumed to be 2000-2021, 22-99 assumed to be 1922-1999)
        int currentYear = LocalDate.now().getYear();
        int fullYear = (year <= 21) ? 2000 + year : 1900 + year;

        // Calculate age
        int age = currentYear - fullYear;

        // Adjust if birthday hasn't occurred yet this year
        LocalDate today = LocalDate.now();
        LocalDate birthDate = LocalDate.of(fullYear, month, day);
        if (today.getMonthValue() < birthDate.getMonthValue() ||
                (today.getMonthValue() == birthDate.getMonthValue() &&
                        today.getDayOfMonth() < birthDate.getDayOfMonth())) {
            age--;
        }

        return age;
    }

    private void clearForm(JTextField nameField, JTextField surnameField, JTextField idNumberField,
                           JTextField ageField, JTextField cellNumberField, JTextField addressField,
                           JComboBox<String> occupationCombo) {
        nameField.setText("");
        surnameField.setText("");
        idNumberField.setText("");
        ageField.setText("");
        cellNumberField.setText("");
        addressField.setText("");
        occupationCombo.setSelectedIndex(0);
        nameField.requestFocusInWindow();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}


