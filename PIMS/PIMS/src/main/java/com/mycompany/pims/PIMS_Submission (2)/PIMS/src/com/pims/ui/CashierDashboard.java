package com.pims.ui;

import com.pims.ui.panels.POSPanel;
import com.pims.ui.panels.StockCheckPanel;
import com.pims.util.Session;
import com.pims.util.UIStyle;

import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends JFrame {

    public CashierDashboard() {
        setTitle("HealthFirst Pharmacy - Cashier POS");
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());

        JPanel top = UIStyle.headerBar("Point of Sale");
        JLabel who = new JLabel("Cashier: " + Session.getFullName() + "   ");
        who.setForeground(Color.WHITE);
        who.setFont(UIStyle.FONT_LABEL);
        JButton logout = UIStyle.neutralButton("Logout");
        logout.addActionListener(e -> logout());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(who);
        right.add(logout);
        top.add(right, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.FONT_HEADING);
        tabs.addTab("  New Sale  ", new POSPanel());
        tabs.addTab("  Stock Check  ", new StockCheckPanel());

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
