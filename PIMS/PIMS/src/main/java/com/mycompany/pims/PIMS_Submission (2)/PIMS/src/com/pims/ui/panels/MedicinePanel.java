package com.pims.ui.panels;

import com.pims.db.DBConnection;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MedicinePanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Company", "Type", "Price", "Qty", "Reorder Lvl", "Expiry", "Supplier"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField txtSearch = new JTextField();
    private final Map<String, Integer> supplierNameToId = new HashMap<>();

    private JTextField txtName, txtCompany, txtType, txtPrice, txtQty, txtReorder, txtExpiry;
    private JComboBox<String> cmbSupplier;
    private int selectedId = -1;

    public MedicinePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyle.BG_LIGHT);

        add(buildSearchBar(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildFormPanel(), BorderLayout.SOUTH);

        loadSuppliersIntoCombo();
        loadMedicines(null);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) fillFormFromSelection();
        });
    }

    private JPanel buildSearchBar() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        JLabel lbl = UIStyle.heading("Manage Medicines");
        txtSearch.setFont(UIStyle.FONT_FIELD);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER), new EmptyBorder(6, 10, 6, 10)));
        JButton btnSearch = UIStyle.neutralButton("Search");
        btnSearch.addActionListener(e -> loadMedicines(txtSearch.getText().trim()));
        JPanel searchBox = new JPanel(new BorderLayout(6, 0));
        searchBox.setOpaque(false);
        searchBox.add(txtSearch, BorderLayout.CENTER);
        searchBox.add(btnSearch, BorderLayout.EAST);
        searchBox.setPreferredSize(new Dimension(320, 38));
        p.add(lbl, BorderLayout.WEST);
        p.add(searchBox, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildTable() {
        UIStyle.styleTable(table);
        return new JScrollPane(table);
    }

    private JPanel buildFormPanel() {
        JPanel wrap = new JPanel(new BorderLayout(8, 8));
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel grid = new JPanel(new GridLayout(2, 4, 10, 8));
        grid.setOpaque(false);

        txtName = labeledField(grid, "Medicine Name");
        txtCompany = labeledField(grid, "Company");
        txtType = labeledField(grid, "Type (Tablet/Syrup/...)");
        txtPrice = labeledField(grid, "Price");
        txtQty = labeledField(grid, "Quantity in Stock");
        txtReorder = labeledField(grid, "Reorder Level");
        txtExpiry = labeledField(grid, "Expiry Date (YYYY-MM-DD)");

        JPanel supplierBox = new JPanel(new BorderLayout());
        supplierBox.setOpaque(false);
        JLabel l = new JLabel("Supplier");
        l.setFont(UIStyle.FONT_LABEL);
        cmbSupplier = new JComboBox<>();
        cmbSupplier.setFont(UIStyle.FONT_FIELD);
        supplierBox.add(l, BorderLayout.NORTH);
        supplierBox.add(cmbSupplier, BorderLayout.CENTER);
        grid.add(supplierBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        JButton btnAdd = UIStyle.accentButton("Add New");
        JButton btnUpdate = UIStyle.primaryButton("Update Selected");
        JButton btnDelete = UIStyle.dangerButton("Delete Selected");
        JButton btnClear = UIStyle.neutralButton("Clear Form");

        btnAdd.addActionListener(e -> addMedicine());
        btnUpdate.addActionListener(e -> updateMedicine());
        btnDelete.addActionListener(e -> deleteMedicine());
        btnClear.addActionListener(e -> clearForm());

        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);

        wrap.add(grid, BorderLayout.CENTER);
        wrap.add(buttons, BorderLayout.SOUTH);
        return wrap;
    }

    private JTextField labeledField(JPanel parent, String label) {
        JPanel box = new JPanel(new BorderLayout());
        box.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UIStyle.FONT_LABEL);
        JTextField f = new JTextField();
        f.setFont(UIStyle.FONT_FIELD);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER), new EmptyBorder(5, 8, 5, 8)));
        box.add(l, BorderLayout.NORTH);
        box.add(f, BorderLayout.CENTER);
        parent.add(box);
        return f;
    }

    private void loadSuppliersIntoCombo() {
        cmbSupplier.removeAllItems();
        supplierNameToId.clear();
        String sql = "SELECT supplier_id, name FROM suppliers ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                supplierNameToId.put(name, rs.getInt("supplier_id"));
                cmbSupplier.addItem(name);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadMedicines(String filter) {
        model.setRowCount(0);
        String sql = "SELECT m.medicine_id, m.name, m.company, m.medicine_type, m.price, " +
                "m.quantity_in_stock, m.reorder_level, m.expiry_date, s.name AS supplier_name " +
                "FROM medicines m LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id " +
                (filter != null && !filter.isEmpty() ? "WHERE m.name LIKE ? OR m.company LIKE ? " : "") +
                "ORDER BY m.name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filter != null && !filter.isEmpty()) {
                ps.setString(1, "%" + filter + "%");
                ps.setString(2, "%" + filter + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("medicine_id"), rs.getString("name"), rs.getString("company"),
                            rs.getString("medicine_type"), rs.getBigDecimal("price"),
                            rs.getInt("quantity_in_stock"), rs.getInt("reorder_level"),
                            rs.getDate("expiry_date"), rs.getString("supplier_name")
                    });
                }
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        selectedId = (int) model.getValueAt(row, 0);
        txtName.setText(String.valueOf(model.getValueAt(row, 1)));
        txtCompany.setText(String.valueOf(model.getValueAt(row, 2)));
        txtType.setText(String.valueOf(model.getValueAt(row, 3)));
        txtPrice.setText(String.valueOf(model.getValueAt(row, 4)));
        txtQty.setText(String.valueOf(model.getValueAt(row, 5)));
        txtReorder.setText(String.valueOf(model.getValueAt(row, 6)));
        txtExpiry.setText(String.valueOf(model.getValueAt(row, 7)));
        Object supplier = model.getValueAt(row, 8);
        if (supplier != null) cmbSupplier.setSelectedItem(supplier.toString());
    }

    private void addMedicine() {
        if (!validateForm()) return;
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, " +
                "reorder_level, expiry_date, supplier_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bindForm(ps);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Medicine added successfully.");
            clearForm();
            loadMedicines(null);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateMedicine() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine row to update first.");
            return;
        }
        if (!validateForm()) return;
        String sql = "UPDATE medicines SET name=?, company=?, medicine_type=?, price=?, quantity_in_stock=?, " +
                "reorder_level=?, expiry_date=?, supplier_id=? WHERE medicine_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bindForm(ps);
            ps.setInt(9, selectedId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Medicine updated successfully.");
            clearForm();
            loadMedicines(null);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteMedicine() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a medicine row to delete first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected medicine? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM medicines WHERE medicine_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, selectedId);
            ps.executeUpdate();
            clearForm();
            loadMedicines(null);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void bindForm(PreparedStatement ps) throws SQLException {
        ps.setString(1, txtName.getText().trim());
        ps.setString(2, txtCompany.getText().trim());
        ps.setString(3, txtType.getText().trim());
        ps.setBigDecimal(4, new BigDecimal(txtPrice.getText().trim()));
        ps.setInt(5, Integer.parseInt(txtQty.getText().trim()));
        ps.setInt(6, Integer.parseInt(txtReorder.getText().trim()));
        ps.setDate(7, Date.valueOf(txtExpiry.getText().trim()));
        String supplierName = (String) cmbSupplier.getSelectedItem();
        Integer supId = supplierName != null ? supplierNameToId.get(supplierName) : null;
        if (supId != null) ps.setInt(8, supId); else ps.setNull(8, Types.INTEGER);
    }

    private boolean validateForm() {
        try {
            if (txtName.getText().trim().isEmpty()) throw new IllegalArgumentException("Name is required.");
            new BigDecimal(txtPrice.getText().trim());
            Integer.parseInt(txtQty.getText().trim());
            Integer.parseInt(txtReorder.getText().trim());
            Date.valueOf(txtExpiry.getText().trim());
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Please check your inputs.\nPrice must be numeric, Qty/Reorder must be integers, " +
                            "Expiry must be YYYY-MM-DD.\n\n" + ex.getMessage(),
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtName.setText(""); txtCompany.setText(""); txtType.setText("");
        txtPrice.setText(""); txtQty.setText(""); txtReorder.setText(""); txtExpiry.setText("");
        table.clearSelection();
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
