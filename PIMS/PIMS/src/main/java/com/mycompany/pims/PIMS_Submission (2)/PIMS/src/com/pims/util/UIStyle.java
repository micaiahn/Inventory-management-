package com.pims.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Central place for fonts / colors / helper factory methods so every screen
 * in the application looks consistent ("neatly designed interfaces").
 */
public class UIStyle {

    public static final Color PRIMARY      = new Color(0x1E, 0x5F, 0x74);   // deep teal
    public static final Color PRIMARY_DARK = new Color(0x14, 0x40, 0x50);
    public static final Color ACCENT       = new Color(0x2E, 0xA0, 0x6D);   // green (success / sell)
    public static final Color DANGER       = new Color(0xC0, 0x39, 0x2B);   // red (delete)
    public static final Color WARNING      = new Color(0xE6, 0x7E, 0x22);   // amber (low stock / expiry)
    public static final Color BG_LIGHT     = new Color(0xF4, 0xF6, 0xF7);
    public static final Color WHITE        = Color.WHITE;
    public static final Color TEXT_DARK    = new Color(0x2C, 0x2C, 0x2C);
    public static final Color BORDER       = new Color(0xD9, 0xDE, 0xE1);

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_FIELD   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TABLE_H = new Font("Segoe UI", Font.BOLD, 13);

    public static void applyGlobalLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        UIManager.put("control", BG_LIGHT);
        UIManager.put("Button.font", FONT_BUTTON);
        UIManager.put("Label.font", FONT_LABEL);
        UIManager.put("TextField.font", FONT_FIELD);
    }

    public static JButton primaryButton(String text) {
        JButton b = styledButton(text, PRIMARY);
        return b;
    }

    public static JButton accentButton(String text) {
        return styledButton(text, ACCENT);
    }

    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER);
    }

    public static JButton neutralButton(String text) {
        JButton b = styledButton(text, new Color(0x6C, 0x75, 0x7D));
        return b;
    }

    private static JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(bg);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(28);
        table.setShowGrid(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(0xD6, 0xEA, 0xF0));
        table.setSelectionForeground(TEXT_DARK);
        table.setIntercellSpacing(new Dimension(8, 6));
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_H);
        header.setBackground(PRIMARY);
        header.setForeground(WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEADING);
        l.setForeground(PRIMARY_DARK);
        return l;
    }

    public static JPanel headerBar(String title) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PRIMARY);
        bar.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(WHITE);
        bar.add(lbl, BorderLayout.WEST);
        return bar;
    }
}
