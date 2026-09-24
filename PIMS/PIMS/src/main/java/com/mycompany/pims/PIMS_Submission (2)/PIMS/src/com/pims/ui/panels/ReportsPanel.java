package com.pims.ui.panels;

import com.pims.db.DBConnection;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JPanel {

    private final JTabbedPane innerTabs = new JTabbedPane();

    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyle.BG_LIGHT);
        add(UIStyle.heading("Business Reports"), BorderLayout.NORTH);

        innerTabs.addTab("Sales Report", buildSalesReport());
        innerTabs.addTab("Item-Wise Sales", buildItemWiseReport());
        innerTabs.addTab("Low Stock Report", buildLowStockReport());
        innerTabs.addTab("Expiry Report (Next 30 Days)", buildExpiryReport());

        add(innerTabs, BorderLayout.CENTER);
    }

    // ---------- 1. Sales Report (per transaction) ----------
    private JPanel buildSalesReport() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Sale ID", "Date/Time", "Cashier", "Total Amount"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);

        JLabel lblTotal = new JLabel(" ");
        lblTotal.setFont(UIStyle.FONT_HEADING);
        lblTotal.setForeground(UIStyle.PRIMARY_DARK);

        Runnable refresh = () -> {
            model.setRowCount(0);
            String sql = "SELECT s.sale_id, s.sale_date, u.full_name, s.total_amount " +
                    "FROM sales s JOIN users u ON s.user_id = u.user_id ORDER BY s.sale_date DESC";
            double grand = 0;
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getInt(1), rs.getTimestamp(2), rs.getString(3), rs.getBigDecimal(4)});
                    grand += rs.getBigDecimal(4).doubleValue();
                }
                lblTotal.setText(String.format("Grand Total Revenue: %.2f  (%d transactions)", grand, model.getRowCount()));
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        };

        return wrapReport(table, refresh, lblTotal);
    }

    // ---------- 2. Item-Wise Sales Report ----------
    private JPanel buildItemWiseReport() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Medicine", "Total Units Sold", "Total Revenue"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);

        Runnable refresh = () -> {
            model.setRowCount(0);
            String sql = "SELECT m.name, SUM(si.quantity_sold) AS units, SUM(si.quantity_sold * si.price_at_sale) AS revenue " +
                    "FROM sale_items si JOIN medicines m ON si.medicine_id = m.medicine_id " +
                    "GROUP BY m.medicine_id, m.name ORDER BY units DESC";
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getBigDecimal(3)});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        };

        return wrapReport(table, refresh, null);
    }

    // ---------- 3. Low Stock Report ----------
    private JPanel buildLowStockReport() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Medicine", "Company", "Qty in Stock", "Reorder Level", "Supplier"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);

        Runnable refresh = () -> {
            model.setRowCount(0);
            String sql = "SELECT m.name, m.company, m.quantity_in_stock, m.reorder_level, s.name AS supplier " +
                    "FROM medicines m LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                    "WHERE m.quantity_in_stock <= m.reorder_level ORDER BY m.quantity_in_stock ASC";
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getString(5)});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        };

        return wrapReport(table, refresh, null);
    }

    // ---------- 4. Expiry Report (next 30 days) ----------
    private JPanel buildExpiryReport() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Medicine", "Company", "Expiry Date", "Days Left", "Qty in Stock"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIStyle.styleTable(table);

        Runnable refresh = () -> {
            model.setRowCount(0);
            String sql = "SELECT name, company, expiry_date, quantity_in_stock, DATEDIFF(expiry_date, CURDATE()) AS days_left " +
                    "FROM medicines WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY) " +
                    "ORDER BY expiry_date ASC";
            try (Connection con = DBConnection.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getDate(3),
                            rs.getInt("days_left"), rs.getInt(4)});
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        };

        return wrapReport(table, refresh, null);
    }

    private JPanel wrapReport(JTable table, Runnable refresh, JLabel footerLabel) {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(new EmptyBorder(10, 0, 0, 0));
        p.setOpaque(false);

        JButton btnRefresh = UIStyle.primaryButton("Refresh Report");
        btnRefresh.addActionListener(e -> refresh.run());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(btnRefresh);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        if (footerLabel != null) p.add(footerLabel, BorderLayout.SOUTH);

        refresh.run();
        return p;
    }
}
