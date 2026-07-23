package cleanpro.desktopapp.view;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login extends JFrame {

    private UIComponents.RoundedTextField usernameField;
    private UIComponents.RoundedPasswordField passwordField;
    private UIComponents.InlineErrorLabel errorLabel;
    private UIComponents.BubbleBackgroundPanel background;
    private final cleanpro.desktopapp.controller.LoginController loginController =
        new cleanpro.desktopapp.controller.LoginController();

    public Login() {
        setTitle("LOGIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);

        background = new UIComponents.BubbleBackgroundPanel();
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // LEFT: branding (55% of screen — pure bubble background)
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

        JLabel brandTag = new JLabel("Welcome back — sign in to continue");
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
        gbcLeft.weightx = 0.55;   // 55% — bubble background
        gbcLeft.weighty = 1.0;
        gbcLeft.fill = GridBagConstraints.BOTH;
        background.add(leftPanel, gbcLeft);

        // RIGHT: glassmorphism card (45% of screen)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        JPanel card = buildLoginCard();
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.fill = GridBagConstraints.BOTH;
        cardGbc.weightx = 1.0;
        cardGbc.weighty = 1.0;
        cardGbc.insets = new Insets(40, 50, 40, 50);
        rightPanel.add(card, cardGbc);

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 1;
        gbcRight.weightx = 0.45;  // 45% — glass panel
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

    private JPanel buildLoginCard() {
        UIComponents.GlassmorphismPanel card =
                new UIComponents.GlassmorphismPanel(32, UITheme.ACCENT, 3);
        // SINGLE COLUMN — password below username
        card.setLayout(new MigLayout(
            "wrap, fillx, insets 40 40 30 40, gapy 8",
            "[grow]"));

        JLabel title = new JLabel("Welcome Back", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE.deriveFont(28f));
        title.setForeground(UITheme.TEAL_DARK);
        card.add(title, "align center");

        JLabel subtitle = new JLabel("Sign in to your CleanCo account", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_SUBTITLE.deriveFont(15f));
        subtitle.setForeground(UITheme.TEXT_MUTED);
        card.add(subtitle, "align center, gapbottom 16");

        // Username field
        card.add(fieldLabel("Username"), "gapbottom 1");
        usernameField = new UIComponents.RoundedTextField("Enter your username");
        card.add(usernameField, "growx, height 46!");

        // Password field — BELOW username (single column)
        card.add(fieldLabel("Password"), "gapbottom 1, gaptop 10");
        passwordField = new UIComponents.RoundedPasswordField("Enter your password");
        card.add(passwordField, "growx, height 46!");

        // Remember me / forgot password row
        JPanel optionsRow = new JPanel(new MigLayout("insets 0, fillx", "[left][right]"));
        optionsRow.setOpaque(false);
        JCheckBox rememberBox = new JCheckBox("Remember me");
        rememberBox.setFont(UITheme.FONT_LINK.deriveFont(14f));
        rememberBox.setForeground(UITheme.TEXT_MUTED);
        rememberBox.setOpaque(false);
        rememberBox.setFocusPainted(false);
        JLabel forgotLink = new JLabel("Forgot password?");
        forgotLink.setFont(UITheme.FONT_LINK.deriveFont(Font.BOLD, 14f));
        forgotLink.setForeground(UITheme.ACCENT);
        forgotLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(Login.this,
                    "Password reset placeholder.\nHook this up to your backend.",
                    "Forgot Password", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        optionsRow.add(rememberBox);
        optionsRow.add(forgotLink, "align right");
        card.add(optionsRow, "growx, gaptop 8, gapbottom 5");

        errorLabel = new UIComponents.InlineErrorLabel();
        card.add(errorLabel, "growx, height 22!, gaptop 6");

        UIComponents.AnimatedButton loginBtn =
                new UIComponents.AnimatedButton("SIGN IN", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn, "growx, height 48!, gaptop 10, gapbottom 14");

        // REGISTER LINK
        JPanel registerRow = new JPanel(new MigLayout("insets 0, align center"));
        registerRow.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(UITheme.FONT_LINK.deriveFont(14f));
        noAccount.setForeground(UITheme.TEXT_MUTED);
        JLabel registerLink = new JLabel("Register");
        registerLink.setFont(UITheme.FONT_LINK.deriveFont(Font.BOLD, 14f));
        registerLink.setForeground(UITheme.ACCENT);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { openRegistration(); }
            @Override
            public void mouseEntered(MouseEvent e) { registerLink.setText("<html><u>Register</u></html>"); }
            @Override
            public void mouseExited(MouseEvent e) { registerLink.setText("Register"); }
        });
        registerRow.add(noAccount);
        registerRow.add(new JLabel(" "));
        registerRow.add(registerLink);
        card.add(registerRow, "align center");

        return card;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_LABEL.deriveFont(14f));
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.showError("Please enter both username and password.");
            return;
        }

        try {
            cleanpro.desktopapp.model.User user = loginController.login(username, password);
            errorLabel.clear();
            dispose();
            SwingUtilities.invokeLater(() -> new Dashboard(user.getFirstName()).setVisible(true));
        } catch (cleanpro.desktopapp.service.exceptions.ValidationException ex) {
            errorLabel.showError(ex.getMessage());
        }
    }

    private void openRegistration() {
        dispose();
        SwingUtilities.invokeLater(() -> new Registration().setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}