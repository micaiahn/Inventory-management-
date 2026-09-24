package com.pims.ui.panels;

import com.pims.db.DBConnection;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserPanel extends JPanel {

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Username", "Full Name", "Role"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private JTextField txtUsername, txtFullName;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private int selectedId = -1;

    public UserPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(UIStyle.BG_LIGHT);

        add(UIStyle.heading("Manage Users (Cashiers / Admins)"), BorderLayout.NORTH);
        UIStyle.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        loadUsers();
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) fillForm();
        });
    }

    private JPanel buildForm() {
        JPanel wrap = new JPanel(new BorderLayout(8, 8));
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel grid = new JPanel(new GridLayout(1, 4, 10, 8));
        grid.setOpaque(false);
        txtUsername = field(grid, "Username");
        txtFullName = field(grid, "Full Name");

        JPanel passBox = new JPanel(new BorderLayout());
        passBox.setOpaque(false);
        JLabel pl = new JLabel("Password");
        pl.setFont(UIStyle.FONT_LABEL);
        txtPassword = new JPasswordField();
        txtPassword.setFont(UIStyle.FONT_FIELD);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER), new EmptyBorder(5, 8, 5, 8)));
        passBox.add(pl, BorderLayout.NORTH);
        passBox.add(txtPassword, BorderLayout.CENTER);
        grid.add(passBox);

        JPanel roleBox = new JPanel(new BorderLayout());
        roleBox.setOpaque(false);
        JLabel rl = new JLabel("Role");
        rl.setFont(UIStyle.FONT_LABEL);
        cmbRole = new JComboBox<>(new String[]{"Cashier", "Admin"});
        cmbRole.setFont(UIStyle.FONT_FIELD);
        roleBox.add(rl, BorderLayout.NORTH);
        roleBox.add(cmbRole, BorderLayout.CENTER);
        grid.add(roleBox);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        JButton add = UIStyle.accentButton("Add New User");
        JButton upd = UIStyle.primaryButton("Update Selected");
        JButton del = UIStyle.dangerButton("Delete Selected");
        JButton clr = UIStyle.neutralButton("Clear Form");
        add.addActionListener(e -> addUser());
        upd.addActionListener(e -> updateUser());
        del.addActionListener(e -> deleteUser());
        clr.addActionListener(e -> clearForm());
        buttons.add(add); buttons.add(upd); buttons.add(del); buttons.add(clr);

        JLabel note = new JLabel("Note: leave Password blank when updating to keep the existing password.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(Color.GRAY);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(buttons, BorderLayout.NORTH);
        south.add(note, BorderLayout.SOUTH);

        wrap.add(grid, BorderLayout.CENTER);
        wrap.add(south, BorderLayout.SOUTH);
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

    private void loadUsers() {
        model.setRowCount(0);
        String sql = "SELECT user_id, username, full_name, role FROM users ORDER BY username";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("full_name"), rs.getString("role")});
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        selectedId = (int) model.getValueAt(row, 0);
        txtUsername.setText(String.valueOf(model.getValueAt(row, 1)));
        txtFullName.setText(String.valueOf(model.getValueAt(row, 2)));
        cmbRole.setSelectedItem(String.valueOf(model.getValueAt(row, 3)));
        txtPassword.setText("");
    }

    private void addUser() {
        if (txtUsername.getText().trim().isEmpty() || txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Username and Password are required for a new user.");
            return;
        }
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, txtUsername.getText().trim());
            ps.setString(2, new String(txtPassword.getPassword()));
            ps.setString(3, (String) cmbRole.getSelectedItem());
            ps.setString(4, txtFullName.getText().trim());
            ps.executeUpdate();
            clearForm();
            loadUsers();
        } catch (SQLException ex) { showError(ex); }
    }

    private void updateUser() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Select a user to update."); return; }
        boolean changePassword = txtPassword.getPassword().length > 0;
        String sql = changePassword
                ? "UPDATE users SET username=?, full_name=?, role=?, password=? WHERE user_id=?"
                : "UPDATE users SET username=?, full_name=?, role=? WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, txtUsername.getText().trim());
            ps.setString(2, txtFullName.getText().trim());
            ps.setString(3, (String) cmbRole.getSelectedItem());
            if (changePassword) {
                ps.setString(4, new String(txtPassword.getPassword()));
                ps.setInt(5, selectedId);
            } else {
                ps.setInt(4, selectedId);
            }
            ps.executeUpdate();
            clearForm();
            loadUsers();
        } catch (SQLException ex) { showError(ex); }
    }

    private void deleteUser() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Select a user to delete."); return; }
        int c = JOptionPane.showConfirmDialog(this, "Delete this user account?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, selectedId);
            ps.executeUpdate();
            clearForm();
            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete: user may have existing sales records linked.\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedId = -1;
        txtUsername.setText(""); txtFullName.setText(""); txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        table.clearSelection();
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
