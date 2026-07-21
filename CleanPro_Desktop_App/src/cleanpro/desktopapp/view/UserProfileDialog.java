package cleanpro.desktopapp.view;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class UserProfileDialog {

    private final JFrame parent;
    private final String username;
    private JDialog dialog;
    private JTextField nameField, emailField, phoneField;
    private JPasswordField currentPassField, newPassField, confirmPassField;

    public UserProfileDialog(JFrame parent, String username) {
        this.parent = parent;
        this.username = username;
    }

    public void show() {
        dialog = new JDialog(parent, "User Profile", true);
        dialog.setSize(480, 580);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel content = new JPanel(new MigLayout("wrap, insets 28, gapy 14, align center", "[grow]"));
        content.setBackground(Color.WHITE);

        // Avatar
        JPanel avatarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.TEAL_DARK);
                g2.fill(new Ellipse2D.Float(0, 0, 80, 80));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                String initials = username.substring(0, Math.min(2, username.length())).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (80 - fm.stringWidth(initials)) / 2;
                int y = (80 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        avatarPanel.setOpaque(false);
        content.add(avatarPanel, "align center, wrap");

        JLabel nameLabel = new JLabel(username, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(UITheme.TEXT_DARK);
        content.add(nameLabel, "align center, wrap");

        JLabel roleLabel = new JLabel("Administrator", SwingConstants.CENTER);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(UITheme.TEXT_MUTED);
        content.add(roleLabel, "align center, gapbottom 20, wrap");

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(220, 230, 230));
        content.add(sep, "growx, gapbottom 16, wrap");

        // Profile Info Section
        JLabel sectionLabel = new JLabel("Profile Information");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionLabel.setForeground(UITheme.TEAL_DARK);
        content.add(sectionLabel, "gapbottom 8, wrap");

        nameField = new UIComponents.RoundedTextField("Full name");
        nameField.setText(username);
        emailField = new UIComponents.RoundedTextField("Email address");
        emailField.setText(username.toLowerCase().replace(" ", ".") + "@cleanpro.com");
        phoneField = new UIComponents.RoundedTextField("Phone number");
        phoneField.setText("555-0000");

        content.add(newFormLabel("Full Name")); content.add(nameField, "growx, height 40!, wrap");
        content.add(newFormLabel("Email")); content.add(emailField, "growx, height 40!, wrap");
        content.add(newFormLabel("Phone")); content.add(phoneField, "growx, height 40!, gapbottom 16, wrap");

        // Password Section
        JLabel passLabel = new JLabel("Change Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        passLabel.setForeground(UITheme.TEAL_DARK);
        content.add(passLabel, "gapbottom 8, wrap");

        currentPassField = new UIComponents.RoundedPasswordField("Current password");
        newPassField = new UIComponents.RoundedPasswordField("New password");
        confirmPassField = new UIComponents.RoundedPasswordField("Confirm new password");

        content.add(newFormLabel("Current")); content.add(currentPassField, "growx, height 40!, wrap");
        content.add(newFormLabel("New")); content.add(newPassField, "growx, height 40!, wrap");
        content.add(newFormLabel("Confirm")); content.add(confirmPassField, "growx, height 40!, wrap");

        dialog.add(content, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new MigLayout("insets 16 28 28 28, align right"));
        btnPanel.setBackground(Color.WHITE);

        UIComponents.AnimatedButton closeBtn = new UIComponents.AnimatedButton(
            "Close", new Color(180, 190, 190), new Color(160, 170, 170));
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.addActionListener(e -> dialog.dispose());

        UIComponents.AnimatedButton updateBtn = new UIComponents.AnimatedButton(
            "Update Profile", UITheme.ACCENT, UITheme.ACCENT_HOVER);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        updateBtn.addActionListener(e -> {
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            if (!newPass.isEmpty() && !newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "New passwords do not match.");
                return;
            }
            JOptionPane.showMessageDialog(dialog, "Profile updated successfully!");
            dialog.dispose();
        });

        btnPanel.add(closeBtn, "width 100!, height 40!, gapx 8");
        btnPanel.add(updateBtn, "width 150!, height 40!");
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JLabel newFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(UITheme.TEXT_DARK);
        return l;
    }
}