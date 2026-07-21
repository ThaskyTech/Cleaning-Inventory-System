package cleanpro.desktopapp.view;

import java.awt.Color;
import java.awt.Font;


public final class UITheme {

    // Core palette
    public static final Color TEAL_DARK    = new Color(0, 121, 128);
    public static final Color TEAL_LIGHT   = new Color(120, 209, 202);
    public static final Color ACCENT       = new Color(0, 168, 150);
    public static final Color ACCENT_HOVER = new Color(0, 145, 130);
    public static final Color DANGER       = new Color(198, 40, 40);
    public static final Color DANGER_HOVER = new Color(170, 30, 30);
    public static final Color FIELD_BG     = new Color(240, 245, 245);
    public static final Color TEXT_DARK    = new Color(30, 45, 45);
    public static final Color TEXT_MUTED   = new Color(120, 130, 130);
    public static final Color PLACEHOLDER  = new Color(150, 160, 160);
    public static final Color CARD_WHITE   = Color.WHITE;

    // Fonts
    public static final Font FONT_TITLE     = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE  = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_LABEL     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_FIELD     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LINK      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_ERROR     = new Font("Segoe UI", Font.PLAIN, 11);

    private UITheme() {
        // Prevent instantiation - this is a constants-only holder class
    }
}

