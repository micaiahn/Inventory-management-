import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class MedicinePanel extends JPanel {

    private JTextField idField;
    private JTextField nameField;
    private JTextField companyField;
    private JComboBox<String> typeBox;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField reorderField;
    private JTextField expiryField;
    private JTextField supplierField;
    private JTextField searchField;

    private JTable table;

    private MedicineDAO dao =
            new MedicineDAO();

    public MedicinePanel() {

        setLayout(
                new BorderLayout(10, 10)
        );

        buildInterface();

        loadMedicines();
    }

    private void buildInterface() {

        JPanel form =
                new JPanel(
                        new GridLayout(5, 4, 8, 8)
                );

        form.setBorder(
                BorderFactory.createTitledBorder(
                        "Medicine Details"
                )
        );

        idField = new JTextField();
        idField.setEditable(false);

        nameField = new JTextField();
        companyField = new JTextField();

        typeBox =
                new JComboBox<>(
                        new String[]{
                                "Tablet",
                                "Capsule",
                                "Syrup",
                                "Injection",
                                "Cream"
                        }
                );

        priceField = new JTextField();
        quantityField = new JTextField();
        reorderField = new JTextField();
        expiryField = new JTextField();
        supplierField = new JTextField();

        form.add(new JLabel("ID"));
        form.add(idField);

        form.add(new JLabel("Name"));
        form.add(nameField);

        form.add(new JLabel("Company"));
        form.add(companyField);

        form.add(new JLabel("Type"));
        form.add(typeBox);

        form.add(new JLabel("Price"));
        form.add(priceField);

        form.add(new JLabel("Quantity"));
        form.add(quantityField);

        form.add(new JLabel("Reorder Level"));
        form.add(reorderField);

        form.add(new JLabel("Expiry YYYY-MM-DD"));
        form.add(expiryField);

        form.add(new JLabel("Supplier ID"));
        form.add(supplierField);

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
                e -> addMedicine()
        );

        update.addActionListener(
                e -> updateMedicine()
        );

        delete.addActionListener(
                e -> deleteMedicine()
        );

        clear.addActionListener(
                e -> clearFields()
        );

        JPanel top =
                new JPanel(
                        new BorderLayout()
                );

        top.add(
                form,
                BorderLayout.CENTER
        );

        JPanel search =
                new JPanel();

        searchField =
                new JTextField(20);

        JButton searchButton =
                new JButton("SEARCH");

        JButton allButton =
                new JButton("SHOW ALL");

        search.add(
                new JLabel("Search:")
        );

        search.add(searchField);
        search.add(searchButton);
        search.add(allButton);

        top.add(
                search,
                BorderLayout.SOUTH
        );

        add(
                top,
                BorderLayout.NORTH
        );

        table =
                new JTable();

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getSelectionModel()
                .addListSelectionListener(
                        e -> selectMedicine()
                );

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        searchButton.addActionListener(
                e -> searchMedicines()
        );

        allButton.addActionListener(
                e -> loadMedicines()
        );
    }

    private void loadMedicines() {

        List<Medicine> list =
                dao.getAllMedicines();

        fillTable(list);
    }

    private void searchMedicines() {

        String text =
                searchField.getText().trim();

        fillTable(
                dao.searchMedicine(text)
        );
    }

    private void fillTable(
            List<Medicine> list) {

        String[] columns = {
                "ID",
                "Name",
                "Company",
                "Type",
                "Price",
                "Stock",
                "Reorder",
                "Expiry",
                "Supplier"
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        columns,
                        0
                );

        for (Medicine m : list) {

            model.addRow(
                    new Object[]{
                            m.getMedicineId(),
                            m.getName(),
                            m.getCompany(),
                            m.getMedicineType(),
                            String.format(
                                    "R %.2f",
                                    m.getPrice()
                            ),
                            m.getQuantityInStock(),
                            m.getReorderLevel(),
                            m.getExpiryDate(),
                            m.getSupplierId()
                    }
            );
        }

        table.setModel(model);
    }

    private void selectMedicine() {

        int row =
                table.getSelectedRow();

        if (row < 0) {
            return;
        }

        idField.setText(
                table.getValueAt(row, 0).toString()
        );

        nameField.setText(
                table.getValueAt(row, 1).toString()
        );

        companyField.setText(
                table.getValueAt(row, 2).toString()
        );

        typeBox.setSelectedItem(
                table.getValueAt(row, 3).toString()
        );

        priceField.setText(
                table.getValueAt(row, 4)
                        .toString()
                        .replace("R ", "")
        );

        quantityField.setText(
                table.getValueAt(row, 5).toString()
        );

        reorderField.setText(
                table.getValueAt(row, 6).toString()
        );

        expiryField.setText(
                table.getValueAt(row, 7).toString()
        );

        supplierField.setText(
                table.getValueAt(row, 8).toString()
        );
    }

    private Medicine getMedicineFromFields()
            throws Exception {

        int id =
                idField.getText().isEmpty()
                        ? 0
                        : Integer.parseInt(
                        idField.getText()
                );

        String name =
                nameField.getText().trim();

        String company =
                companyField.getText().trim();

        double price =
                Double.parseDouble(
                        priceField.getText()
                );

        int quantity =
                Integer.parseInt(
                        quantityField.getText()
                );

        int reorder =
                Integer.parseInt(
                        reorderField.getText()
                );

        Date expiry =
                Date.valueOf(
                        expiryField.getText()
                );

        int supplier =
                Integer.parseInt(
                        supplierField.getText()
                );

        if (name.isEmpty() ||
                company.isEmpty()) {

            throw new Exception(
                    "Name and company are required."
            );
        }

        if (price < 0 ||
                quantity < 0 ||
                reorder < 0) {

            throw new Exception(
                    "Numbers cannot be negative."
            );
        }

        return new Medicine(
                id,
                name,
                company,
                typeBox.getSelectedItem().toString(),
                price,
                quantity,
                reorder,
                expiry,
                supplier
        );
    }

    private void addMedicine() {

        try {

            Medicine m =
                    getMedicineFromFields();

            if (dao.addMedicine(m)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Medicine added successfully."
                );

                loadMedicines();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Medicine could not be added."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid data: "
                            + e.getMessage()
            );
        }
    }

    private void updateMedicine() {

        try {

            Medicine m =
                    getMedicineFromFields();

            if (m.getMedicineId() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Select a medicine first."
                );

                return;
            }

            if (dao.updateMedicine(m)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Medicine updated."
                );

                loadMedicines();
                clearFields();

            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid data: "
                            + e.getMessage()
            );
        }
    }

    private void deleteMedicine() {

        if (idField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a medicine first."
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete this medicine?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

        if (answer ==
                JOptionPane.YES_OPTION) {

            int id =
                    Integer.parseInt(
                            idField.getText()
                    );

            if (dao.deleteMedicine(id)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Medicine deleted."
                );

                loadMedicines();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Cannot delete this medicine."
                );
            }
        }
    }

    private void clearFields() {

        idField.setText("");
        nameField.setText("");
        companyField.setText("");
        priceField.setText("");
        quantityField.setText("");
        reorderField.setText("");
        expiryField.setText("");
        supplierField.setText("");

        typeBox.setSelectedIndex(0);

        table.clearSelection();
    }
}