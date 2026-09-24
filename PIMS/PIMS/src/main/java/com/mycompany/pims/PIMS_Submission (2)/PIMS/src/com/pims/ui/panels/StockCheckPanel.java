package com.pims.ui.panels;

import com.pims.db.DBConnection;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Read-only stock lookup for cashiers. Cashiers can search price/availability
 * but cannot add, edit, or delete medicines.
 */
public class StockCheckPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Medicine", "Company", "Type", "Price", "In Stock", "Expiry"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = new JTextField();

    public StockCheckPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyle.BG_LIGHT);

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.add(UIStyle.heading("Stock Check (view only)"), BorderLayout.WEST);

        JPanel searchBox = new JPanel(new BorderLayout(6, 0));
        searchBox.setOpaque(false);
        txtSearch.setFont(UIStyle.FONT_FIELD);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER), new EmptyBorder(6, 10, 6, 10)));
        JButton btnSearch = UIStyle.primaryButton("Search");
        btnSearch.addActionListener(e -> search());
        txtSearch.addActionListener(e -> search());
        searchBox.setPreferredSize(new Dimension(320, 38));
        searchBox.add(txtSearch, BorderLayout.CENTER);
        searchBox.add(btnSearch, BorderLayout.EAST);
        top.add(searchBox, BorderLayout.EAST);

        UIStyle.styleTable(table);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        search();
    }

    private void search() {
        model.setRowCount(0);
        String term = txtSearch.getText().trim();
        String sql = "SELECT name, company, medicine_type, price, quantity_in_stock, expiry_date FROM medicines " +
                (term.isEmpty() ? "" : "WHERE name LIKE ? OR company LIKE ? ") + "ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (!term.isEmpty()) {
                ps.setString(1, "%" + term + "%");
                ps.setString(2, "%" + term + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getBigDecimal(4), rs.getInt(5), rs.getDate(6)});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
