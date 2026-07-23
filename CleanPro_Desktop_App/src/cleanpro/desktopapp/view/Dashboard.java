package cleanpro.desktopapp.view;

import cleanpro.desktopapp.service.DashboardService;
import cleanpro.desktopapp.model.DashboardSummary;
import cleanpro.desktopapp.model.RecentIssuance;
import cleanpro.desktopapp.view.CleanersPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Dashboard extends JFrame {

    private final String username;
    private final DashboardService dashboardService = new DashboardService();
    private JPanel contentPanel;
    private JLabel currentViewLabel;
    private JPanel sidebarPanel;
    private CardLayout cardLayout;

    private static final String MENU_DASHBOARD = "Dashboard";
    private static final String MENU_SUPPLIERS = "Suppliers";
    private static final String MENU_MATERIALS = "Materials";
    private static final String MENU_CLEANERS = "Cleaners";
    private static final String MENU_ISSUANCES = "Stock Issuances";
    private static final String MENU_ADJUSTMENTS = "Adjustments";
    private static final String MENU_REPORTS = "Reports";

    private static final String[] MENU_ITEMS = {
        MENU_DASHBOARD, MENU_SUPPLIERS, MENU_MATERIALS,
        MENU_CLEANERS, MENU_ISSUANCES, MENU_ADJUSTMENTS, MENU_REPORTS
    };

    private JPanel[] menuItemPanels;
    private int activeMenuIndex = 0;

    public Dashboard(String username) {
        this.username = username;
        setTitle("Clean Pro - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setMinimumSize(new Dimension(1200, 800));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sidebarPanel = buildSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        contentPanel = buildContentArea();
        add(contentPanel, BorderLayout.CENTER);

        UIComponents.FadeGlassPane fadePane = new UIComponents.FadeGlassPane();
        setGlassPane(fadePane);
        fadePane.setVisible(true);
        setVisible(true);
        fadePane.startFadeIn();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new MigLayout(
            "wrap, fillx, insets 0, gapy 0",
            "[grow, fill]",
            "[]0[]0[]0[]0[]0[]0[]push[]0[]"
        ));
        sidebar.setPreferredSize(new Dimension(260, 900));
        sidebar.setBackground(UITheme.TEAL_DARK);

        // Logo & Brand
        JPanel brandPanel = new JPanel(new MigLayout("insets 24 20 16 20, align center", "[center]"));
        brandPanel.setOpaque(false);

        // Logo image - uses logo.png from package
        JLabel logoIcon = loadLogo(56, 56);

        JLabel brandName = new JLabel("Clean Pro", SwingConstants.CENTER);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 24));
        brandName.setForeground(Color.WHITE);

        JLabel brandTag = new JLabel("Management System", SwingConstants.CENTER);
        brandTag.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandTag.setForeground(new Color(180, 220, 218));

        brandPanel.add(logoIcon, "wrap, align center");
        brandPanel.add(brandName, "wrap, align center");
        brandPanel.add(brandTag, "align center");
        sidebar.add(brandPanel, "growx");

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setBackground(new Color(255, 255, 255, 20));
        sidebar.add(sep, "growx, gapx 20, gapbottom 8");

        // Menu Items
        menuItemPanels = new JPanel[MENU_ITEMS.length];
        for (int i = 0; i < MENU_ITEMS.length; i++) {
            final int index = i;
            JPanel item = createMenuItem(MENU_ITEMS[i], i == 0);
            item.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { setActiveMenu(index); }
                @Override public void mouseEntered(MouseEvent e) {
                    if (index != activeMenuIndex) item.setBackground(new Color(0, 100, 105));
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (index != activeMenuIndex) item.setBackground(UITheme.TEAL_DARK);
                }
            });
            menuItemPanels[i] = item;
            sidebar.add(item, "growx, gapx 12, height 48!");
        }

        sidebar.add(new JLabel(), "push");

        // User Profile Button (replaces static user info)
        JPanel userPanel = new JPanel(new MigLayout("insets 8 16 4 16, fillx"));
        userPanel.setOpaque(false);

        UIComponents.AnimatedButton profileBtn = new UIComponents.AnimatedButton(
            "  " + username + "  ", new Color(0, 100, 105), new Color(0, 130, 135));
        profileBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        profileBtn.addActionListener(e -> new UserProfileDialog(Dashboard.this, username).show());
        userPanel.add(profileBtn, "growx, height 42!");
        sidebar.add(userPanel, "growx, gapx 8, gapbottom 4");

        // Logout Button
        JPanel logoutPanel = new JPanel(new MigLayout("insets 4 16 20 16, fillx"));
        logoutPanel.setOpaque(false);

        UIComponents.AnimatedButton logoutBtn =
            new UIComponents.AnimatedButton("LOGOUT", UITheme.DANGER, UITheme.DANGER_HOVER);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.addActionListener(e -> handleLogout());
        logoutPanel.add(logoutBtn, "growx, height 42!");
        sidebar.add(logoutPanel, "growx");

        return sidebar;
    }

    private JLabel loadLogo(int w, int h) {
        JLabel logoLabel = new JLabel("CP", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setPreferredSize(new Dimension(w, h));
        logoLabel.setOpaque(true);
        logoLabel.setBackground(UITheme.ACCENT);
        logoLabel.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,60), 2));

        // Try to load logo.png - uncomment when you have the file
      
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("logo.png"));
            Image scaledImage = originalIcon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
            logoLabel.setPreferredSize(new Dimension(w, h));
        } catch (Exception e) {
            // fallback to text logo above
        }
       
        return logoLabel;
    }

    private JPanel createMenuItem(String text, boolean active) {
        JPanel panel = new JPanel(new MigLayout("insets 0 24 0 20, fill", "[grow]", "[center]"));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBackground(active ? new Color(0, 145, 152) : UITheme.TEAL_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder());

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textLabel.setForeground(Color.WHITE);

        if (active) {
            panel.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.ACCENT));
        }

        panel.add(textLabel, "aligny center");
        return panel;
    }

    private void setActiveMenu(int index) {
        if (index == activeMenuIndex) return;

        menuItemPanels[activeMenuIndex].setBackground(UITheme.TEAL_DARK);
        menuItemPanels[activeMenuIndex].setBorder(BorderFactory.createEmptyBorder());

        activeMenuIndex = index;
        menuItemPanels[activeMenuIndex].setBackground(new Color(0, 145, 152));
        menuItemPanels[activeMenuIndex].setBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.ACCENT));

        switchContentView(MENU_ITEMS[index]);
    }

    private JPanel buildContentArea() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UITheme.FIELD_BG);

        JPanel topBar = new JPanel(new MigLayout("insets 16 28 16 28, fillx", "[left]push[right]"));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 230, 230)));

        currentViewLabel = new JLabel(MENU_DASHBOARD);
        currentViewLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        currentViewLabel.setForeground(UITheme.TEXT_DARK);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(UITheme.TEXT_MUTED);

        topBar.add(currentViewLabel);
        topBar.add(dateLabel);
        container.add(topBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        JPanel viewsPanel = new JPanel(cardLayout);
        viewsPanel.setBackground(UITheme.FIELD_BG);

        viewsPanel.add(buildDashboardView(), MENU_DASHBOARD);
        viewsPanel.add(new SuppliersPanel(), MENU_SUPPLIERS);
        viewsPanel.add(new MaterialsPanel(), MENU_MATERIALS);
        viewsPanel.add(new CleanersPanel(), MENU_CLEANERS);
        viewsPanel.add(new StockIssuancesPanel(), MENU_ISSUANCES);
        viewsPanel.add(new StockAdjustmentsPanel(), MENU_ADJUSTMENTS);
        viewsPanel.add(new ReportsPanel(), MENU_REPORTS);

        container.add(viewsPanel, BorderLayout.CENTER);
        return container;
    }

    private void switchContentView(String viewName) {
        currentViewLabel.setText(viewName);
        cardLayout.show((Container) contentPanel.getComponent(1), viewName);
    }

    private JPanel buildDashboardView() {
        JPanel panel = new JPanel(new MigLayout(
            "wrap 3, fillx, insets 28, gapy 16, gapx 16",
            "[grow][grow][grow]",
            "[][][grow]"
        ));
        panel.setBackground(UITheme.FIELD_BG);

        JPanel banner = new JPanel(new MigLayout("insets 24 28, fill", "[grow]", "[][]")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.TEAL_DARK, getWidth(), getHeight(), UITheme.ACCENT);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome back, " + username + "!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(Color.WHITE);

        JLabel welcomeSub = new JLabel("Here is what is happening with your cleaning business today.");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeSub.setForeground(new Color(220, 240, 238));

        banner.add(welcomeTitle, "wrap");
        banner.add(welcomeSub);
        panel.add(banner, "span 3, growx, height 120!, wrap");

        DashboardSummary summary = dashboardService.getDashboardSummary();

        panel.add(buildStatCard("Total Materials", String.valueOf(summary.getTotalMaterials()), UITheme.ACCENT), "grow");
        panel.add(buildStatCard("Active Cleaners", String.valueOf(summary.getTotalCleaners()), UITheme.TEAL_DARK), "grow");
        // TODO: no supplier count in DashboardSummary yet — wire up if/when needed (e.g. add
        // getTotalSuppliers() to DashboardService, backed by SupplierDAO.getAll().size()).
        panel.add(buildStatCard("Suppliers", "-", new Color(0, 150, 136)), "grow, wrap");

        // TODO: "Pending Issuances" would need a status filter (StockIssuance.Status.PENDING)
        // once the Stock Issuance screen actually creates issuances with that status.
        panel.add(buildStatCard("Pending Issuances", "-", new Color(255, 152, 0)), "grow");
        // TODO: Stock Adjustments has no backing table/service yet at all (see StockAdjustmentsPanel) —
        // nothing to wire this to until that feature is actually built out.
        panel.add(buildStatCard("Stock Adjustments", "-", new Color(76, 175, 80)), "grow");
        panel.add(buildStatCard("Low Stock Items", String.valueOf(summary.getLowStockCount()), UITheme.DANGER), "grow, wrap");

        JPanel activityPanel = new UIComponents.GlassmorphismPanel(20, UITheme.ACCENT, 0);
        activityPanel.setLayout(new MigLayout("wrap, insets 20, fillx", "[grow]"));
        activityPanel.setOpaque(false);

        JLabel activityTitle = new JLabel("Recent Activity");
        activityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activityTitle.setForeground(UITheme.TEXT_DARK);
        activityPanel.add(activityTitle, "gapbottom 12");

        List<String> activities = new ArrayList<>();
        for (RecentIssuance issuance : summary.getRecentIssuances()) {
            activities.add("Issuance " + issuance.getIssuanceNumber() + " to "
                    + issuance.getCleanerFullName() + " - " + issuance.getStatus());
        }
        if (activities.isEmpty()) {
            activities.add("No stock issuances recorded yet.");
        }
        for (String act : activities) {
            JLabel actLabel = new JLabel("- " + act);
            actLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            actLabel.setForeground(UITheme.TEXT_DARK);
            actLabel.setBorder(new EmptyBorder(8, 0, 8, 0));
            activityPanel.add(actLabel, "growx");
        }

        panel.add(activityPanel, "span 2, grow");

        JPanel quickPanel = new UIComponents.GlassmorphismPanel(20, UITheme.ACCENT, 0);
        quickPanel.setLayout(new MigLayout("wrap, insets 20, fillx", "[grow]"));
        quickPanel.setOpaque(false);

        JLabel quickTitle = new JLabel("Quick Actions");
        quickTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        quickTitle.setForeground(UITheme.TEXT_DARK);
        quickPanel.add(quickTitle, "gapbottom 12");

        String[] actions = {"New Material", "New Supplier", "Stock Issuance", "View Reports"};
        for (String action : actions) {
            UIComponents.AnimatedButton btn = new UIComponents.AnimatedButton(
                action, UITheme.ACCENT, UITheme.ACCENT_HOVER);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            quickPanel.add(btn, "growx, height 40!, gapbottom 8");
        }

        panel.add(quickPanel, "grow");

        return panel;
    }

    private JPanel buildStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new MigLayout("insets 20, fill", "[grow]", "[][]")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 4, 4, 4));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(UITheme.TEXT_DARK);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(UITheme.TEXT_MUTED);

        card.add(valueLabel, "wrap");
        card.add(titleLabel);
        return card;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new Login().setVisible(true));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("Admin").setVisible(true));
    }
}