package com.pims.ui.panels;

import com.pims.db.DBConnection;
import com.pims.model.CartItem;
import com.pims.ui.BillWindow;
import com.pims.util.Session;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {

    private final DefaultTableModel searchModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Company", "Price", "In Stock"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel cartModel = new DefaultTableModel(
            new Object[]{"Medicine", "Unit Price", "Qty", "Line Total"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable searchTable = new JTable(searchModel);
    private final JTable cartTable = new JTable(cartModel);
    private final JTextField txtSearch = new JTextField();
    private final JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JLabel lblGrandTotal = new JLabel("Total: 0.00");

    private final List<CartItem> cart = new ArrayList<>();

    public POSPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyle.BG_LIGHT);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildSearchSide(), buildCartSide());
        split.setResizeWeight(0.55);
        split.setDividerSize(6);
        add(split, BorderLayout.CENTER);

        loadCatalog(null);
    }

    // ---------------- LEFT: catalog / search ----------------
    private JPanel buildSearchSide() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(6, 0));
        top.setOpaque(false);
        top.add(UIStyle.heading("Find Medicine"), BorderLayout.WEST);

        JPanel searchBox = new JPanel(new BorderLayout(6, 0));
        searchBox.setOpaque(false);
        txtSearch.setFont(UIStyle.FONT_FIELD);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER), new EmptyBorder(6, 10, 6, 10)));
        txtSearch.addActionListener(e -> loadCatalog(txtSearch.getText().trim()));
        JButton btnSearch = UIStyle.neutralButton("Search");
        btnSearch.addActionListener(e -> loadCatalog(txtSearch.getText().trim()));
        searchBox.setPreferredSize(new Dimension(260, 36));
        searchBox.add(txtSearch, BorderLayout.CENTER);
        searchBox.add(btnSearch, BorderLayout.EAST);
        top.add(searchBox, BorderLayout.EAST);

        UIStyle.styleTable(searchTable);

        JPanel addBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        addBar.setOpaque(false);
        JLabel qtyLbl = new JLabel("Qty:");
        qtyLbl.setFont(UIStyle.FONT_LABEL);
        spnQty.setFont(UIStyle.FONT_FIELD);
        JButton btnAddToCart = UIStyle.accentButton("Add to Cart");
        btnAddToCart.addActionListener(e -> addSelectedToCart());
        addBar.add(qtyLbl);
        addBar.add(spnQty);
        addBar.add(btnAddToCart);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        p.add(addBar, BorderLayout.SOUTH);
        return p;
    }

    // ---------------- RIGHT: cart / checkout ----------------
    private JPanel buildCartSide() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);
        p.add(UIStyle.heading("Current Cart"), BorderLayout.NORTH);

        UIStyle.styleTable(cartTable);
        p.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);
        lblGrandTotal.setFont(UIStyle.FONT_TITLE);
        lblGrandTotal.setForeground(UIStyle.PRIMARY_DARK);
        bottom.add(lblGrandTotal, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buttons.setOpaque(false);
        JButton btnRemove = UIStyle.neutralButton("Remove Selected");
        JButton btnClear = UIStyle.dangerButton("Clear Cart");
        JButton btnCheckout = UIStyle.primaryButton("CHECKOUT");
        btnRemove.addActionListener(e -> removeSelectedFromCart());
        btnClear.addActionListener(e -> clearCart());
        btnCheckout.addActionListener(e -> checkout());
        buttons.add(btnRemove);
        buttons.add(btnClear);
        buttons.add(btnCheckout);
        bottom.add(buttons, BorderLayout.SOUTH);

        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    private void loadCatalog(String filter) {
        searchModel.setRowCount(0);
        String sql = "SELECT medicine_id, name, company, price, quantity_in_stock FROM medicines " +
                (filter != null && !filter.isEmpty() ? "WHERE name LIKE ? OR company LIKE ? " : "") +
                "ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filter != null && !filter.isEmpty()) {
                ps.setString(1, "%" + filter + "%");
                ps.setString(2, "%" + filter + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    searchModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getBigDecimal(4), rs.getInt(5)});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void addSelectedToCart() {
        int row = searchTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine from the list first.");
            return;
        }
        int id = (int) searchModel.getValueAt(row, 0);
        String name = (String) searchModel.getValueAt(row, 1);
        BigDecimal price = (BigDecimal) searchModel.getValueAt(row, 3);
        int stock = (int) searchModel.getValueAt(row, 4);
        int qty = (int) spnQty.getValue();

        if (stock <= 0) {
            JOptionPane.showMessageDialog(this, name + " is out of stock.", "Out of Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // If already in cart, just increase quantity (respecting stock).
        for (CartItem item : cart) {
            if (item.getMedicineId() == id) {
                int newQty = item.getQuantity() + qty;
                if (newQty > stock) {
                    JOptionPane.showMessageDialog(this, "Only " + stock + " units of " + name + " available.");
                    return;
                }
                item.setQuantity(newQty);
                refreshCartTable();
                return;
            }
        }

        if (qty > stock) {
            JOptionPane.showMessageDialog(this, "Only " + stock + " units of " + name + " available.");
            return;
        }

        cart.add(new CartItem(id, name, price, qty, stock));
        refreshCartTable();
    }

    private void removeSelectedFromCart() {
        int row = cartTable.getSelectedRow();
        if (row == -1) return;
        cart.remove(row);
        refreshCartTable();
    }

    private void clearCart() {
        cart.clear();
        refreshCartTable();
    }

    private void refreshCartTable() {
        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart) {
            cartModel.addRow(new Object[]{item.getName(), item.getUnitPrice(), item.getQuantity(), item.getLineTotal()});
            total = total.add(item.getLineTotal());
        }
        lblGrandTotal.setText("Total: " + total.setScale(2, java.math.RoundingMode.HALF_UP));
    }

    private void checkout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add items before checking out.");
            return;
        }

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            BigDecimal grandTotal = BigDecimal.ZERO;
            for (CartItem item : cart) grandTotal = grandTotal.add(item.getLineTotal());

            // 1. Insert sale header
            int saleId;
            String saleSql = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
            try (PreparedStatement ps = con.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setBigDecimal(1, grandTotal);
                ps.setInt(2, Session.getUserId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    saleId = keys.getInt(1);
                }
            }

            // 2. Insert sale items and decrement stock (with a stock-availability re-check)
            String itemSql = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?,?,?,?)";
            String stockCheckSql = "SELECT quantity_in_stock FROM medicines WHERE medicine_id=? FOR UPDATE";
            String stockUpdateSql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id=?";

            for (CartItem item : cart) {
                try (PreparedStatement check = con.prepareStatement(stockCheckSql)) {
                    check.setInt(1, item.getMedicineId());
                    try (ResultSet rs = check.executeQuery()) {
                        rs.next();
                        int currentStock = rs.getInt(1);
                        if (currentStock < item.getQuantity()) {
                            throw new SQLException("Insufficient stock for " + item.getName() +
                                    " (only " + currentStock + " left).");
                        }
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                    ps.setInt(1, saleId);
                    ps.setInt(2, item.getMedicineId());
                    ps.setInt(3, item.getQuantity());
                    ps.setBigDecimal(4, item.getUnitPrice());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(stockUpdateSql)) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getMedicineId());
                    ps.executeUpdate();
                }
            }

            con.commit();

            new BillWindow(saleId, new ArrayList<>(cart), grandTotal).setVisible(true);

            clearCart();
            loadCatalog(txtSearch.getText().trim());

        } catch (SQLException ex) {
            try { if (con != null) con.rollback(); } catch (SQLException ignored) { }
            JOptionPane.showMessageDialog(this, "Checkout failed:\n" + ex.getMessage(),
                    "Transaction Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }
}
