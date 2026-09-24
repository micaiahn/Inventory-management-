package com.pims.ui;

import com.pims.ui.panels.MedicinePanel;
import com.pims.ui.panels.ReportsPanel;
import com.pims.ui.panels.SupplierPanel;
import com.pims.ui.panels.UserPanel;
import com.pims.util.Session;
import com.pims.util.UIStyle;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("HealthFirst Pharmacy - Administrator");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());

        // --- Top bar ---
        JPanel top = UIStyle.headerBar("Admin Dashboard");
        JLabel who = new JLabel("Logged in as: " + Session.getFullName() + "  (Admin)   ");
        who.setForeground(Color.WHITE);
        who.setFont(UIStyle.FONT_LABEL);
        JButton logout = UIStyle.neutralButton("Logout");
        logout.addActionListener(e -> logout());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(who);
        right.add(logout);
        top.add(right, BorderLayout.EAST);

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.FONT_HEADING);
        tabs.addTab("  Manage Medicines  ", new MedicinePanel());
        tabs.addTab("  Manage Suppliers  ", new SupplierPanel());
        tabs.addTab("  Manage Users  ", new UserPanel());
        tabs.addTab("  Reports  ", new ReportsPanel());

        root.add(top, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void logout() {
        Session.clear();
        dispose();
        new LoginFrame().setVisible(true);
    }
}
