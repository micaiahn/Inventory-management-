import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel {

    private JTextField usernameField;
    private JTextField fullNameField;
    private JPasswordField passwordField;

    private JTable table;

    private UserDAO dao =
            new UserDAO();

    public UserPanel() {

        setLayout(new BorderLayout(10, 10));

        buildInterface();

        loadUsers();
    }

    private void buildInterface() {

        JPanel form =
                new JPanel(
                        new GridLayout(2, 4, 10, 10)
                );

        form.setBorder(
                BorderFactory.createTitledBorder(
                        "Create Cashier"
                )
        );

        usernameField =
                new JTextField();

        fullNameField =
                new JTextField();

        passwordField =
                new JPasswordField();

        JButton add =
                new JButton("CREATE CASHIER");

        JButton delete =
                new JButton("DELETE SELECTED");

        form.add(
                new JLabel("Username")
        );

        form.add(usernameField);

        form.add(
                new JLabel("Full Name")
        );

        form.add(fullNameField);

        form.add(
                new JLabel("Password")
        );

        form.add(passwordField);

        form.add(add);
        form.add(delete);

        add.addActionListener(
                e -> addUser()
        );

        delete.addActionListener(
                e -> deleteUser()
        );

        add(form, BorderLayout.NORTH);

        table = new JTable();

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );
    }

    private void loadUsers() {

        List<User> users =
                dao.getAllUsers();

        String[] columns = {
                "ID",
                "Username",
                "Full Name",
                "Role"
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        columns,
                        0
                );

        for (User u : users) {

            model.addRow(
                    new Object[]{
                            u.getUserId(),
                            u.getUsername(),
                            u.getFullName(),
                            u.getRole()
                    }
            );
        }

        table.setModel(model);
    }

    private void addUser() {

        String username =
                usernameField.getText().trim();

        String fullName =
                fullNameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        if (username.isEmpty() ||
                fullName.isEmpty() ||
                password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all fields."
            );

            return;
        }

        User user =
                new User(
                        0,
                        username,
                        password,
                        "Cashier",
                        fullName
                );

        if (dao.addUser(user)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cashier account created."
            );

            usernameField.setText("");
            fullNameField.setText("");
            passwordField.setText("");

            loadUsers();
        }
    }

    private void deleteUser() {

        int row =
                table.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a user first."
            );

            return;
        }

        int id =
                Integer.parseInt(
                        table.getValueAt(
                                row,
                                0
                        ).toString()
                );

        String role =
                table.getValueAt(
                        row,
                        3
                ).toString();

        if (role.equals("Admin")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Admin accounts cannot be deleted."
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete this cashier?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

        if (answer ==
                JOptionPane.YES_OPTION) {

            if (dao.deleteUser(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "User deleted."
                );

                loadUsers();
            }
        }
    }
}