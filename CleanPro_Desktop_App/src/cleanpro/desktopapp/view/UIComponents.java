package cleanpro.desktopapp.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class UIComponents {

    // =====================================================================
    // RoundedTextField
    // =====================================================================
    public static class RoundedTextField extends JTextField {
        private final String placeholder;
        private float focusAlpha = 0f;
        private Timer focusTimer;
        private boolean focusIn = false;

        public RoundedTextField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setFont(UITheme.FONT_FIELD);
            setForeground(UITheme.TEXT_DARK);
            setCaretColor(UITheme.TEAL_DARK);
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { animateFocus(true); }
                @Override public void focusLost(FocusEvent e) { animateFocus(false); }
            });
        }

        private void animateFocus(boolean in) {
            focusIn = in;
            if (focusTimer != null && focusTimer.isRunning()) focusTimer.stop();
            focusTimer = new Timer(15, null);
            focusTimer.addActionListener(ev -> {
                focusAlpha = focusIn ? Math.min(1f, focusAlpha + 0.08f) : Math.max(0f, focusAlpha - 0.12f);
                repaint();
                if ((focusIn && focusAlpha >= 1f) || (!focusIn && focusAlpha <= 0f))
                    ((Timer) ev.getSource()).stop();
            });
            focusTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (focusAlpha > 0.01f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, focusAlpha * 0.40f));
                g2.setColor(UITheme.ACCENT);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, getWidth() - 3, getHeight() - 3, 20, 20));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            g2.setColor(UITheme.FIELD_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
            g2.dispose();
            super.paintComponent(g);
            if (getText().isEmpty() && !hasFocus()) {
                Graphics2D pg = (Graphics2D) g.create();
                pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                pg.setColor(UITheme.PLACEHOLDER);
                pg.setFont(getFont());
                FontMetrics fm = pg.getFontMetrics();
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                pg.drawString(placeholder, 16, textY);
                pg.dispose();
            }
        }

        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, Math.max(d.height, 40));
        }
    }

    // =====================================================================
    // RoundedPasswordField
    // =====================================================================
    public static class RoundedPasswordField extends JPasswordField {
        private final String placeholder;
        private float focusAlpha = 0f;
        private Timer focusTimer;
        private boolean focusIn = false;

        public RoundedPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setFont(UITheme.FONT_FIELD);
            setForeground(UITheme.TEXT_DARK);
            setCaretColor(UITheme.TEAL_DARK);
            setEchoChar('●');
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { animateFocus(true); }
                @Override public void focusLost(FocusEvent e) { animateFocus(false); }
            });
        }

        private void animateFocus(boolean in) {
            focusIn = in;
            if (focusTimer != null && focusTimer.isRunning()) focusTimer.stop();
            focusTimer = new Timer(15, null);
            focusTimer.addActionListener(ev -> {
                focusAlpha = focusIn ? Math.min(1f, focusAlpha + 0.08f) : Math.max(0f, focusAlpha - 0.12f);
                repaint();
                if ((focusIn && focusAlpha >= 1f) || (!focusIn && focusAlpha <= 0f))
                    ((Timer) ev.getSource()).stop();
            });
            focusTimer.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (focusAlpha > 0.01f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, focusAlpha * 0.40f));
                g2.setColor(UITheme.ACCENT);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new RoundRectangle2D.Float(1.5f, 1.5f, getWidth() - 3, getHeight() - 3, 20, 20));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
            g2.setColor(UITheme.FIELD_BG);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
            g2.dispose();
            super.paintComponent(g);
            if (getPassword().length == 0 && !hasFocus()) {
                Graphics2D pg = (Graphics2D) g.create();
                pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                pg.setColor(UITheme.PLACEHOLDER);
                pg.setFont(getFont());
                FontMetrics fm = pg.getFontMetrics();
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                pg.drawString(placeholder, 16, textY);
                pg.dispose();
            }
        }

        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, Math.max(d.height, 40));
        }
    }

    // =====================================================================
    // GlassmorphismPanel
    // =====================================================================
    public static class GlassmorphismPanel extends JPanel {
        private final int radius;
        private final Color accentColor;
        private final int accentHeight;

        public GlassmorphismPanel(int radius, Color accentColor, int accentHeight) {
            this.radius = radius;
            this.accentColor = accentColor;
            this.accentHeight = accentHeight;
            setOpaque(false);
            setLayout(new BorderLayout());
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth() - 8;
            int h = getHeight() - 10;

            g2.setColor(new Color(0, 0, 0, 45));
            g2.fill(new RoundRectangle2D.Float(6, 9, w - 4, h - 4, radius, radius));

            g2.setColor(new Color(255, 255, 255, 120));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

            g2.setColor(new Color(230, 245, 245, 35));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

            GradientPaint topHighlight = new GradientPaint(
                0, 0, new Color(255, 255, 255, 120),
                0, h * 0.45f, new Color(255, 255, 255, 0));
            g2.setPaint(topHighlight);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(255, 255, 255, 220));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, radius, radius));

            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(new Color(255, 255, 255, 100));
            g2.draw(new RoundRectangle2D.Float(3.5f, 3.5f, w - 7, h - 7, radius - 4, radius - 4));

            GradientPaint bottomGlow = new GradientPaint(
                0, h - 15, new Color(255, 255, 255, 40),
                0, h, new Color(255, 255, 255, 0));
            g2.setPaint(bottomGlow);
            g2.fill(new RoundRectangle2D.Float(4, h - 15, w - 8, 15, radius - 4, radius - 4));

            if (accentColor != null && accentHeight > 0) {
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, accentHeight * 2, radius / 2, radius / 2));
                g2.fillRect(0, accentHeight / 2, w, accentHeight);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =====================================================================
    // RoundedPanel
    // =====================================================================
    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        public RoundedPanel(int radius, Color bg) {
            this.radius = radius; this.bg = bg; setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 25));
            g2.fill(new RoundRectangle2D.Float(2, 4, getWidth() - 4, getHeight() - 4, radius, radius));
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 6, radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =====================================================================
    // AnimatedButton
    // =====================================================================
    public static class AnimatedButton extends JButton {
        private Color currentColor;
        private final Color base;
        private final Color hover;
        private Timer transitionTimer;

        public AnimatedButton(String text, Color base, Color hover) {
            super(text);
            this.base = base; this.hover = hover; this.currentColor = base;
            setForeground(Color.WHITE);
            setFont(UITheme.FONT_BUTTON);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { if (isEnabled()) animateTo(hover); }
                @Override public void mouseExited(MouseEvent e) { if (isEnabled()) animateTo(base); }
            });
        }

        private void animateTo(Color target) {
            if (transitionTimer != null && transitionTimer.isRunning()) transitionTimer.stop();
            transitionTimer = new Timer(15, null);
            transitionTimer.addActionListener(e -> {
                currentColor = blend(currentColor, target, 0.25f);
                repaint();
                if (colorsClose(currentColor, target)) {
                    currentColor = target;
                    ((Timer) e.getSource()).stop();
                    repaint();
                }
            });
            transitionTimer.start();
        }

        private Color blend(Color a, Color b, float ratio) {
            return new Color(
                (int) (a.getRed() + (b.getRed() - a.getRed()) * ratio),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * ratio),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * ratio));
        }
        private boolean colorsClose(Color a, Color b) {
            return Math.abs(a.getRed() - b.getRed()) < 3 && Math.abs(a.getGreen() - b.getGreen()) < 3 && Math.abs(a.getBlue() - b.getBlue()) < 3;
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? currentColor : new Color(180, 190, 190));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =====================================================================
    // BubbleBackgroundPanel
    // =====================================================================
    public static class BubbleBackgroundPanel extends JPanel {
        private final List<Bubble> bubbles = new ArrayList<>();
        private final Timer animTimer;
        public BubbleBackgroundPanel() {
            for (int i = 0; i < 14; i++) bubbles.add(Bubble.random());
            animTimer = new Timer(30, e -> { for (Bubble b : bubbles) b.step(); repaint(); });
            animTimer.start();
        }
        public void stopAnimation() { if (animTimer != null) animTimer.stop(); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradient = new GradientPaint(0, 0, UITheme.TEAL_DARK, getWidth(), getHeight(), UITheme.TEAL_LIGHT);
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            for (Bubble b : bubbles) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, b.alpha));
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(b.x * getWidth(), b.y * getHeight(), b.size, b.size));
            }
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.dispose();
        }
    }

    private static class Bubble {
        float x, y, size, speed, alpha;
        static Bubble random() {
            Bubble b = new Bubble();
            b.x = (float) Math.random(); b.y = (float) Math.random();
            b.size = 8 + (float) Math.random() * 24;
            b.speed = 0.0006f + (float) Math.random() * 0.0012f;
            b.alpha = 0.15f + (float) Math.random() * 0.25f;
            return b;
        }
        void step() {
            y -= speed;
            if (y < -0.05f) { y = 1.05f; x = (float) Math.random(); }
        }
    }

    // =====================================================================
    // FadeGlassPane
    // =====================================================================
    public static class FadeGlassPane extends JPanel {
        private float alpha = 1f;
        public FadeGlassPane() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            if (alpha <= 0f) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
        public void startFadeIn() {
            Timer timer = new Timer(20, null);
            timer.addActionListener(e -> {
                alpha -= 0.04f;
                if (alpha <= 0f) { alpha = 0f; setVisible(false); ((Timer) e.getSource()).stop(); }
                repaint();
            });
            timer.start();
        }
    }

    // =====================================================================
    // InlineErrorLabel
    // =====================================================================
    public static class InlineErrorLabel extends JLabel {
        public InlineErrorLabel() {
            super(" ");
            setFont(UITheme.FONT_ERROR);
            setForeground(UITheme.DANGER);
        }
        public void showError(String msg) { setText(msg); }
        public void clear() { setText(" "); }
    }

    public static JComboBox<String> styledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(UITheme.FONT_FIELD);
        combo.setBackground(UITheme.FIELD_BG);
        combo.setForeground(UITheme.TEXT_DARK);
        combo.setBorder(new EmptyBorder(4, 10, 4, 10));
        return combo;
    }

    private UIComponents() {}
}