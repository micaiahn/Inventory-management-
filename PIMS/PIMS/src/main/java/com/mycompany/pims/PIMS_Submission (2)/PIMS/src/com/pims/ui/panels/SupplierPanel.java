package com.pims.ui.panels;

import com.pims.db.DBConnection;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SupplierPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Name", "Contact Person", "Phone", "Email", "Address"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private JTextField txtName, txtContact, txtPhone, txtEmail;
    private JTextArea txtAddress;
    private int selectedId = -1;

    public SupplierPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyle.BG_LIGHT);

        add(UIStyle.heading("Manage Suppliers"), BorderLayout.NORTH);
        UIStyle.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        loadSuppliers();
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) fillForm();
        });
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout(8, 8));
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel grid = new JPanel(new GridLayout(1, 5, 10, 8));
        grid.setOpaque(false);
        txtName = field(grid, "Supplier Name");
        txtContact = field(grid, "Contact Person");
        txtPhone = field(grid, "Phone");
        txtEmail = field(grid, "Email");

        JPanel addrBox = new JPanel(new BorderLayout());
        addrBox.setOpaque(false);
        JLabel l = new JLabel("Address");
        l.setFont(UIStyle.FONT_LABEL);
        txtAddress = new JTextArea(2, 10);
        txtAddress.setFont(UIStyle.FONT_FIELD);
        txtAddress.setLineWrap(true);
        addrBox.add(l, BorderLayout.NORTH);
        addrBox.add(new JScrollPane(txtAddress), BorderLayout.CENTER);
        grid.add(addrBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        JButton add = UIStyle.accentButton("Add New");
        JButton upd = UIStyle.primaryButton("Update Selected");
        JButton del = UIStyle.dangerButton("Delete Selected");
        JButton clr = UIStyle.neutralButton("Clear Form");
        add.addActionListener(e -> addSupplier());
        upd.addActionListener(e -> updateSupplier());
        del.addActionListener(e -> deleteSupplier());
        clr.addActionListener(e -> clearForm());
        buttons.add(add); buttons.add(upd); buttons.add(del); buttons.add(clr);

        wrap.add(grid, BorderLayout.CENTER);
        wrap.add(buttons, BorderLayout.SOUTH);
        return wrap;
    }

    private JTextField field(JPanel parent, String label) {
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

    private void loadSuppliers() {
        model.setRowCount(0);
        String sql = "SELECT * FROM suppliers ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("supplier_id"), rs.getString("name"),
                        rs.getString("contact_person"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("address")});
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        selectedId = (int) model.getValueAt(row, 0);
        txtName.setText(String.valueOf(model.getValueAt(row, 1)));
        txtContact.setText(String.valueOf(model.getValueAt(row, 2)));
        txtPhone.setText(String.valueOf(model.getValueAt(row, 3)));
        txtEmail.setText(String.valueOf(model.getValueAt(row, 4)));
        txtAddress.setText(String.valueOf(model.getValueAt(row, 5)));
    }

    private void addSupplier() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Supplier name is required.");
            return;
        }
        String sql = "INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps);
            ps.executeUpdate();
            clearForm();
            loadSuppliers();
        } catch (SQLException ex) { showError(ex); }
    }

    private void updateSupplier() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Select a supplier to update."); return; }
        String sql = "UPDATE suppliers SET name=?, contact_person=?, phone=?, email=?, address=? WHERE supplier_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps);
            ps.setInt(6, selectedId);
            ps.executeUpdate();
            clearForm();
            loadSuppliers();
        } catch (SQLException ex) { showError(ex); }
    }

    private void deleteSupplier() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Select a supplier to delete."); return; }
        int c = JOptionPane.showConfirmDialog(this, "Delete this supplier?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, selectedId);
            ps.executeUpdate();
            clearForm();
            loadSuppliers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete: supplier may be linked to existing medicines.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bind(PreparedStatement ps) throws SQLException {
        ps.setString(1, txtName.getText().trim());
        ps.setString(2, txtContact.getText().trim());
        ps.setString(3, txtPhone.getText().trim());
        ps.setString(4, txtEmail.getText().trim());
        ps.setString(5, txtAddress.getText().trim());
    }

    private void clearForm() {
        selectedId = -1;
        txtName.setText(""); txtContact.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtAddress.setText("");
        table.clearSelection();
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
