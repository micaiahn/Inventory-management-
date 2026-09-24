import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {

    private User user;

    private MedicineDAO medicineDAO =
            new MedicineDAO();

    private SaleDAO saleDAO =
            new SaleDAO();

    private JTextField searchField;

    private JTable medicineTable;
    private JTable cartTable;

    private JLabel totalLabel;

    private List<SaleItem> cart =
            new ArrayList<>();

    private double total = 0;

    public POSPanel(User user) {

        this.user = user;

        setLayout(
                new BorderLayout(10, 10)
        );

        buildInterface();

        loadMedicines("");
    }

    private void buildInterface() {

        JPanel searchPanel =
                new JPanel();

        searchField =
                new JTextField(25);

        JButton search =
                new JButton("SEARCH");

        JButton showAll =
                new JButton("SHOW ALL");

        searchPanel.add(
                new JLabel("Medicine:")
        );

        searchPanel.add(searchField);
        searchPanel.add(search);
        searchPanel.add(showAll);

        add(
                searchPanel,
                BorderLayout.NORTH
        );

        medicineTable =
                new JTable();

        medicineTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane medicineScroll =
                new JScrollPane(
                        medicineTable
                );

        medicineScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Available Medicines"
                )
        );

        cartTable =
                new JTable();

        JScrollPane cartScroll =
                new JScrollPane(
                        cartTable
                );

        cartScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Current Cart"
                )
        );

        JSplitPane split =
                new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT,
                        medicineScroll,
                        cartScroll
                );

        split.setDividerLocation(260);

        add(
                split,
                BorderLayout.CENTER
        );

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        JButton addCart =
                new JButton("ADD TO CART");

        JButton remove =
                new JButton("REMOVE ITEM");

        JButton clear =
                new JButton("CLEAR CART");

        JButton checkout =
                new JButton("CHECKOUT");

        totalLabel =
                new JLabel("TOTAL: R 0.00");

        totalLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        bottom.add(addCart);
        bottom.add(remove);
        bottom.add(clear);
        bottom.add(totalLabel);
        bottom.add(checkout);

        add(
                bottom,
                BorderLayout.SOUTH
        );

        search.addActionListener(
                e -> loadMedicines(
                        searchField.getText()
                )
        );

        showAll.addActionListener(
                e -> loadMedicines("")
        );

        addCart.addActionListener(
                e -> addToCart()
        );

        remove.addActionListener(
                e -> removeFromCart()
        );

        clear.addActionListener(
                e -> clearCart()
        );

        checkout.addActionListener(
                e -> checkout()
        );
    }

    private void loadMedicines(
            String search) {

        List<Medicine> list;

        if (search.trim().isEmpty()) {
            list =
                    medicineDAO.getAllMedicines();
        } else {
            list =
                    medicineDAO.searchMedicine(
                            search
                    );
        }

        String[] columns = {
                "ID",
                "Medicine",
                "Company",
                "Type",
                "Price",
                "Stock",
                "Expiry"
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
                            m.getExpiryDate()
                    }
            );
        }

        medicineTable.setModel(model);
    }

    private void addToCart() {

        int row =
                medicineTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a medicine."
            );

            return;
        }

        int medicineId =
                Integer.parseInt(
                        medicineTable
                                .getValueAt(row, 0)
                                .toString()
                );

        Medicine medicine =
                medicineDAO.getMedicineById(
                        medicineId
                );

        if (medicine == null) {
            return;
        }

        String input =
                JOptionPane.showInputDialog(
                        this,
                        "Enter quantity:"
                );

        if (input == null) {
            return;
        }

        try {

            int quantity =
                    Integer.parseInt(input);

            if (quantity <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quantity must be greater than zero."
                );

                return;
            }

            if (quantity >
                    medicine.getQuantityInStock()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Not enough stock."
                );

                return;
            }

            for (SaleItem item : cart) {

                if (item.getMedicineId()
                        == medicineId) {

                    int newQuantity =
                            item.getQuantitySold()
                                    + quantity;

                    if (newQuantity >
                            medicine.getQuantityInStock()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Not enough stock."
                        );

                        return;
                    }

                    item.setQuantitySold(
                            newQuantity
                    );

                    updateCart();

                    return;
                }
            }

            cart.add(
                    new SaleItem(
                            0,
                            0,
                            medicineId,
                            quantity,
                            medicine.getPrice()
                    )
            );

            updateCart();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid number."
            );
        }
    }

    private void updateCart() {

        String[] columns = {
                "Medicine ID",
                "Medicine",
                "Quantity",
                "Price",
                "Total"
        };

        DefaultTableModel model =
                new DefaultTableModel(
                        columns,
                        0
                );

        total = 0;

        for (SaleItem item : cart) {

            Medicine m =
                    medicineDAO.getMedicineById(
                            item.getMedicineId()
                    );

            double lineTotal =
                    item.getQuantitySold()
                            * item.getPriceAtSale();

            total += lineTotal;

            model.addRow(
                    new Object[]{
                            m.getMedicineId(),
                            m.getName(),
                            item.getQuantitySold(),
                            String.format(
                                    "R %.2f",
                                    item.getPriceAtSale()
                            ),
                            String.format(
                                    "R %.2f",
                                    lineTotal
                            )
                    }
            );
        }

        cartTable.setModel(model);

        totalLabel.setText(
                String.format(
                        "TOTAL: R %.2f",
                        total
                )
        );
    }

    private void removeFromCart() {

        int row =
                cartTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a cart item."
            );

            return;
        }

        cart.remove(row);

        updateCart();
    }

    private void clearCart() {

        cart.clear();

        updateCart();
    }

    private void checkout() {

        if (cart.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cart is empty."
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        String.format(
                                "Complete sale for R %.2f?",
                                total
                        ),
                        "Checkout",
                        JOptionPane.YES_NO_OPTION
                );

        if (answer !=
                JOptionPane.YES_OPTION) {
            return;
        }

        int saleId =
                saleDAO.createSale(
                        user.getUserId(),
                        total,
                        cart
                );

        if (saleId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sale failed. Check stock/database."
            );

            return;
        }

        new BillFrame(
                saleId,
                user,
                cart,
                total
        ).setVisible(true);

        cart.clear();

        updateCart();

        loadMedicines(
                searchField.getText()
        );
    }
}