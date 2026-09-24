import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierPanel extends JPanel {

    private JTextField idField;
    private JTextField nameField;
    private JTextField contactField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;

    private JTable table;

    private SupplierDAO dao =
            new SupplierDAO();

    public SupplierPanel() {

        setLayout(new BorderLayout(10, 10));

        buildInterface();

        loadSuppliers();
    }

    private void buildInterface() {

        JPanel form =
                new JPanel(
                        new GridLayout(3, 4, 8, 8)
                );

        form.setBorder(
                BorderFactory.createTitledBorder(
                        "Supplier Details"
                )
        );

        idField = new JTextField();
        idField.setEditable(false);

        nameField = new JTextField();
        contactField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();

        form.add(new JLabel("ID"));
        form.add(idField);

        form.add(new JLabel("Name"));
        form.add(nameField);

        form.add(new JLabel("Contact Person"));
        form.add(contactField);

        form.add(new JLabel("Phone"));
        form.add(phoneField);

        form.add(new JLabel("Email"));
        form.add(emailField);

        form.add(new JLabel("Address"));
        form.add(addressField);

        JButton add =
                new JButton("ADD");

        JButton update =
                new JButton("UPDATE");

        JButton delete =
                new JButton("DELETE");

        JButton clear =
                new JButton("CLEAR");

        form.add(add);
        form.add(update);
        form.add(delete);
        form.add(clear);

        add.addActionListener(
                e -> addSupplier()
        );

        update.addActionListener(
                e -> updateSupplier()
        );

        delete.addActionListener(
                e -> deleteSupplier()
        );

        clear.addActionListener(
                e -> clearFields()
        );

        add(form, BorderLayout.NORTH);

        table = new JTable();

        table.getSelectionModel()
                .addListSelectionListener(
                        e -> selectSupplier()
                );

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );
    }

    private void loadSuppliers() {

        List<Supplier> list =
                dao.getAllSuppliers();

        String[] columns = {
                "ID",
                "Name",
                "Contact",
                "Phone",
                "Email",
                "Address"
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        columns,
                        0
                );

        for (Supplier s : list) {

            model.addRow(
                    new Object[]{
                            s.getSupplierId(),
                            s.getName(),
                            s.getContactPerson(),
                            s.getPhone(),
                            s.getEmail(),
                            s.getAddress()
                    }
            );
        }

        table.setModel(model);
    }

    private void selectSupplier() {

        int row =
                table.getSelectedRow();

        if (row < 0) return;

        idField.setText(
                table.getValueAt(row, 0).toString()
        );

        nameField.setText(
                table.getValueAt(row, 1).toString()
        );

        contactField.setText(
                table.getValueAt(row, 2).toString()
        );

        phoneField.setText(
                table.getValueAt(row, 3).toString()
        );

        emailField.setText(
                table.getValueAt(row, 4).toString()
        );

        addressField.setText(
                table.getValueAt(row, 5).toString()
        );
    }

    private Supplier getSupplier() {

        int id =
                idField.getText().isEmpty()
                        ? 0
                        : Integer.parseInt(
                        idField.getText()
                );

        return new Supplier(
                id,
                nameField.getText(),
                contactField.getText(),
                phoneField.getText(),
                emailField.getText(),
                addressField.getText()
        );
    }

    private void addSupplier() {

        Supplier s = getSupplier();

        if (s.getName().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier name is required."
            );

            return;
        }

        if (dao.addSupplier(s)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier added."
            );

            loadSuppliers();
            clearFields();
        }
    }

    private void updateSupplier() {

        if (idField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a supplier first."
            );

            return;
        }

        if (dao.updateSupplier(
                getSupplier())) {

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier updated."
            );

            loadSuppliers();
            clearFields();
        }
    }

    private void deleteSupplier() {

        if (idField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a supplier first."
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete this supplier?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

        if (answer ==
                JOptionPane.YES_OPTION) {

            int id =
                    Integer.parseInt(
                            idField.getText()
                    );

            if (dao.deleteSupplier(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Supplier deleted."
                );

                loadSuppliers();
                clearFields();
            }
        }
    }

    private void clearFields() {

        idField.setText("");
        nameField.setText("");
        contactField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");

        table.clearSelection();
    }
}