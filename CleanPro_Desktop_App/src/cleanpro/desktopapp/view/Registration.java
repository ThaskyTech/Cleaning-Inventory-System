package cleanpro.desktopapp.view;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class Registration extends JFrame {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    private UIComponents.RoundedTextField fullNameField;
    private UIComponents.RoundedTextField usernameField;
    private UIComponents.RoundedTextField emailField;
    private UIComponents.RoundedPasswordField passwordField;
    private UIComponents.RoundedPasswordField confirmPasswordField;
    private JComboBox<String> roleCombo;
    private UIComponents.InlineErrorLabel errorLabel;
    private UIComponents.BubbleBackgroundPanel background;

    public Registration() {
        setTitle("REGISTER");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);

        background = new UIComponents.BubbleBackgroundPanel();
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // LEFT: branding (half the screen — pure bubble background)
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Load logo.png from the same package directory
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("logo.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoIcon = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandTitle = new JLabel("Clean Pro");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 48));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandTag = new JLabel("Join the team — register below");
        brandTag.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        brandTag.setForeground(new Color(220, 240, 238));
        brandTag.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logoIcon);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        leftPanel.add(brandTitle);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        leftPanel.add(brandTag);
        leftPanel.add(Box.createVerticalGlue());

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.weightx = 1.0;   // HALF
        gbcLeft.weighty = 1.0;
        gbcLeft.fill = GridBagConstraints.BOTH;
        background.add(leftPanel, gbcLeft);

        // RIGHT: glassmorphism card (half the screen)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        JPanel card = buildRegistrationCard();
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.BOTH;
        cardGbc.weightx = 1.0;
        cardGbc.weighty = 1.0;
        cardGbc.insets = new Insets(40, 60, 40, 60);
        rightPanel.add(card, cardGbc);

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 1;
        gbcRight.weightx = 1.0;  // HALF
        gbcRight.weighty = 1.0;
        gbcRight.fill = GridBagConstraints.BOTH;
        background.add(rightPanel, gbcRight);

        // Fade-in
        UIComponents.FadeGlassPane fadePane = new UIComponents.FadeGlassPane();
        setGlassPane(fadePane);
        fadePane.setVisible(true);
        setVisible(true);
        fadePane.startFadeIn();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                background.stopAnimation();
            }
        });
    }

    private JPanel buildRegistrationCard() {
        UIComponents.GlassmorphismPanel card =
                new UIComponents.GlassmorphismPanel(32, UITheme.ACCENT, 3);
        // Two columns: fields side by side
        card.setLayout(new MigLayout(
            "wrap 2, fillx, insets 40 50 30 50, gapy 10, gapx 18",
            "[grow][grow]"));

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE.deriveFont(30f));
        title.setForeground(UITheme.TEAL_DARK);
        card.add(title, "span 2, align center");

        JLabel subtitle = new JLabel("Register as a Clean Pro staff member", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_SUBTITLE.deriveFont(16f));
        subtitle.setForeground(UITheme.TEXT_MUTED);
        card.add(subtitle, "span 2, align center, gapbottom 10");

        // Row 1: Full Name | Username
        card.add(fieldGroup("Full Name", fullNameField = new UIComponents.RoundedTextField("Enter your full name")), "growx");
        card.add(fieldGroup("Username", usernameField = new UIComponents.RoundedTextField("Choose a username")), "growx");

        // Row 2: Email | Password
        card.add(fieldGroup("Email", emailField = new UIComponents.RoundedTextField("Enter your email")), "growx");
        card.add(fieldGroup("Password", passwordField = new UIComponents.RoundedPasswordField("Create a password")), "growx");

        // Row 3: Confirm Password | Role
        card.add(fieldGroup("Confirm Password", confirmPasswordField = new UIComponents.RoundedPasswordField("Re-enter your password")), "growx");
        roleCombo = UIComponents.styledComboBox(new String[]{"Manger", "Cleaner","Admin"});
        card.add(fieldGroup("Role", roleCombo), "growx");

        errorLabel = new UIComponents.InlineErrorLabel();
        card.add(errorLabel, "span 2, growx, height 22!, gaptop 8");

        UIComponents.AnimatedButton registerBtn =
                new UIComponents.AnimatedButton("REGISTER", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        registerBtn.addActionListener(e -> handleRegister());
        card.add(registerBtn, "span 2, growx, height 48!, gaptop 10, gapbottom 12");

        // LOGIN LINK
        JPanel loginRow = new JPanel(new MigLayout("insets 0, align center"));
        loginRow.setOpaque(false);
        JLabel haveAccount = new JLabel("Already have an account?");
        haveAccount.setFont(UITheme.FONT_LINK.deriveFont(15f));
        haveAccount.setForeground(UITheme.TEXT_MUTED);
        JLabel loginLink = new JLabel("Log in");
        loginLink.setFont(UITheme.FONT_LINK.deriveFont(Font.BOLD, 15f));
        loginLink.setForeground(UITheme.ACCENT);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { openLogin(); }
            @Override
            public void mouseEntered(MouseEvent e) { loginLink.setText("<html><u>Log in</u></html>"); }
            @Override
            public void mouseExited(MouseEvent e) { loginLink.setText("Log in"); }
        });
        loginRow.add(haveAccount);
        loginRow.add(loginLink);
        card.add(loginRow, "span 2, align center");

        return card;
    }

    /** Helper: label stacked tightly above a field, placed inside a transparent panel */
    private JPanel fieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel(new MigLayout("wrap, insets 0, gapy 3", "[grow]", "[][]"));
        group.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL.deriveFont(15f));
        label.setForeground(UITheme.TEXT_DARK);
        group.add(label);
        if (field instanceof UIComponents.RoundedTextField || field instanceof UIComponents.RoundedPasswordField) {
            group.add(field, "growx, height 46!");
        } else {
            group.add(field, "growx, height 40!");
        }
        return group;
    }

    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();
        char[] confirmPassword = confirmPasswordField.getPassword();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty()
                || password.length == 0 || confirmPassword.length == 0) {
            errorLabel.showError("Please fill in all fields.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            errorLabel.showError("Please enter a valid email address.");
            return;
        }
        if (password.length < 6) {
            errorLabel.showError("Password must be at least 6 characters.");
            return;
        }
        if (!new String(password).equals(new String(confirmPassword))) {
            errorLabel.showError("Passwords do not match.");
            return;
        }
        errorLabel.clear();
        JOptionPane.showMessageDialog(this,
                "Registration successful (placeholder).\nHook this up to the real backend.",
                "Registration", JOptionPane.INFORMATION_MESSAGE);
        openLogin();
    }

    private void openLogin() {
        dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Registration::new);
    }
}