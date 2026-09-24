package com.pims.ui;

import com.pims.db.DBConnection;
import com.pims.util.Session;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JLabel lblError = new JLabel(" ");

    public LoginFrame() {
        setTitle("HealthFirst Pharmacy - Login");
        setSize(430, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIStyle.WHITE);

        // Top banner
        JPanel banner = new JPanel();
        banner.setBackground(UIStyle.PRIMARY);
        banner.setPreferredSize(new Dimension(430, 140));
        banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("+ HealthFirst Pharmacy");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Pharmacy Inventory Management System");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(220, 235, 240));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        banner.add(Box.createVerticalGlue());
        banner.add(logo);
        banner.add(Box.createRigidArea(new Dimension(0, 6)));
        banner.add(sub);
        banner.add(Box.createVerticalGlue());

        // Form
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(30, 40, 30, 40));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIStyle.FONT_LABEL);
        txtUsername.setFont(UIStyle.FONT_FIELD);
        txtUsername.setMaximumSize(new Dimension(2000, 38));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER), new EmptyBorder(6, 10, 6, 10)));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIStyle.FONT_LABEL);
        txtPassword.setFont(UIStyle.FONT_FIELD);
        txtPassword.setMaximumSize(new Dimension(2000, 38));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyle.BORDER), new EmptyBorder(6, 10, 6, 10)));

        lblError.setForeground(UIStyle.DANGER);
        lblError.setFont(UIStyle.FONT_LABEL);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnLogin = UIStyle.primaryButton("LOG IN");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(2000, 42));
        btnLogin.addActionListener(e -> attemptLogin());

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) attemptLogin();
            }
        });

        form.add(lblUser);
        form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(txtUsername);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(lblPass);
        form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(txtPassword);
        form.add(Box.createRigidArea(new Dimension(0, 14)));
        form.add(lblError);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(btnLogin);

        JLabel hint = new JLabel("<html><center>Default: admin / admin123 (Admin)<br>cashier1 / cash123 (Cashier)</center></html>");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(Box.createRigidArea(new Dimension(0, 16)));
        form.add(hint);

        root.add(banner, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        String sql = "SELECT user_id, username, password, role, full_name FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    // NOTE: For simplicity, passwords are stored/compared in plain text here.
                    // In a production system, use hashing (e.g. BCrypt) instead.
                    if (dbPassword.equals(password)) {
                        Session.set(rs.getInt("user_id"), rs.getString("username"),
                                rs.getString("full_name"), rs.getString("role"));
                        openDashboard(rs.getString("role"));
                    } else {
                        lblError.setText("Incorrect password. Please try again.");
                    }
                } else {
                    lblError.setText("No such user found.");
                }
            }
        } catch (SQLException ex) {
            lblError.setText("Database error - check connection.");
            JOptionPane.showMessageDialog(this,
                    "Could not connect to database:\n" + ex.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(String role) {
        dispose();
        if ("Admin".equalsIgnoreCase(role)) {
            new AdminDashboard().setVisible(true);
        } else {
            new CashierDashboard().setVisible(true);
        }
    }
}
