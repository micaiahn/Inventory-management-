import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        setTitle("HealthFirst Pharmacy - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildInterface();
    }

    private void buildInterface() {

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 248, 250));

        JPanel header = new JPanel();
        header.setBackground(new Color(25, 92, 140));
        header.setPreferredSize(new Dimension(500, 110));
        header.setLayout(new GridLayout(2, 1));

        JLabel title =
                new JLabel("HEALTHFIRST", SwingConstants.CENTER);

        title.setForeground(Color.WHITE);
        title.setFont(
                new Font("Segoe UI", Font.BOLD, 28)
        );

        JLabel subtitle =
                new JLabel(
                        "Pharmacy Inventory Management System",
                        SwingConstants.CENTER
                );

        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(
                new Font("Segoe UI", Font.PLAIN, 14)
        );

        header.add(title);
        header.add(subtitle);

        JPanel form = new JPanel(
                new GridBagLayout()
        );

        form.setBackground(
                new Color(245, 248, 250)
        );

        GridBagConstraints g =
                new GridBagConstraints();

        g.insets =
                new Insets(10, 10, 10, 10);

        g.fill =
                GridBagConstraints.HORIZONTAL;

        JLabel userLabel =
                new JLabel("Username:");

        usernameField =
                new JTextField(20);

        JLabel passLabel =
                new JLabel("Password:");

        passwordField =
                new JPasswordField(20);

        JButton login =
                new JButton("LOGIN");

        login.setBackground(
                new Color(25, 92, 140)
        );

        login.setForeground(Color.WHITE);

        g.gridx = 0;
        g.gridy = 0;

        form.add(userLabel, g);

        g.gridx = 1;

        form.add(usernameField, g);

        g.gridx = 0;
        g.gridy = 1;

        form.add(passLabel, g);

        g.gridx = 1;

        form.add(passwordField, g);

        g.gridy = 2;

        form.add(login, g);

        login.addActionListener(
                e -> login()
        );

        passwordField.addActionListener(
                e -> login()
        );

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);

        add(main);
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        if (username.isEmpty() ||
                password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password."
            );

            return;
        }

        UserDAO dao =
                new UserDAO();

        User user =
                dao.login(username, password);

        if (user == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        dispose();

        if (user.getRole().equals("Admin")) {

            new AdminDashboard(user)
                    .setVisible(true);

        } else {

            new CashierDashboard(user)
                    .setVisible(true);
        }
    }
}