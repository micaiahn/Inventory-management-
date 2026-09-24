import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BillFrame extends JFrame {

    private int saleId;
    private User user;
    private List<SaleItem> items;
    private double total;

    private JTextArea billArea;

    public BillFrame(
            int saleId,
            User user,
            List<SaleItem> items,
            double total) {

        this.saleId = saleId;
        this.user = user;
        this.items = items;
        this.total = total;

        setTitle("HealthFirst - Customer Bill");

        setSize(500, 600);

        setLocationRelativeTo(null);

        buildInterface();
    }

    private void buildInterface() {

        billArea =
                new JTextArea();

        billArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        13
                )
        );

        billArea.setEditable(false);

        generateBill();

        JButton save =
                new JButton("SAVE BILL");

        JButton print =
                new JButton("PRINT");

        JButton close =
                new JButton("CLOSE");

        save.addActionListener(
                e -> saveBill()
        );

        print.addActionListener(
                e -> printBill()
        );

        close.addActionListener(
                e -> dispose()
        );

        JPanel buttons =
                new JPanel();

        buttons.add(save);
        buttons.add(print);
        buttons.add(close);

        add(
                new JScrollPane(billArea),
                BorderLayout.CENTER
        );

        add(
                buttons,
                BorderLayout.SOUTH
        );
    }

    private void generateBill() {

        StringBuilder b =
                new StringBuilder();

        b.append(
                "========================================\n"
        );

        b.append(
                "             HEALTHFIRST\n"
        );

        b.append(
                "        PHARMACY & HEALTHCARE\n"
        );

        b.append(
                "========================================\n"
        );

        b.append(
                "Sale Number: "
                        + saleId
                        + "\n"
        );

        b.append(
                "Cashier: "
                        + user.getFullName()
                        + "\n"
        );

        b.append(
                "Date: "
                        + new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss"
                ).format(new Date())
                        + "\n"
        );

        b.append(
                "----------------------------------------\n"
        );

        for (SaleItem item : items) {

            Medicine m =
                    new MedicineDAO()
                            .getMedicineById(
                                    item.getMedicineId()
                            );

            double lineTotal =
                    item.getQuantitySold()
                            * item.getPriceAtSale();

            b.append(
                    String.format(
                            "%-18s %2d  R%8.2f\n",
                            m.getName(),
                            item.getQuantitySold(),
                            lineTotal
                    )
            );
        }

        b.append(
                "----------------------------------------\n"
        );

        b.append(
                String.format(
                        "TOTAL:                 R %.2f\n",
                        total
                )
        );

        b.append(
                "========================================\n"
        );

        b.append(
                "       Thank you for shopping!\n"
        );

        b.append(
                "========================================\n"
        );

        billArea.setText(
                b.toString()
        );
    }

    private void saveBill() {

        JFileChooser chooser =
                new JFileChooser();

        chooser.setSelectedFile(
                new File(
                        "HealthFirst_Bill_"
                                + saleId
                                + ".txt"
                )
        );

        if (chooser.showSaveDialog(this)
                == JFileChooser.APPROVE_OPTION) {

            try {

                FileWriter writer =
                        new FileWriter(
                                chooser.getSelectedFile()
                        );

                writer.write(
                        billArea.getText()
                );

                writer.close();

                JOptionPane.showMessageDialog(
                        this,
                        "Bill saved successfully."
                );

            } catch (IOException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not save bill."
                );
            }
        }
    }

    private void printBill() {

        try {

            billArea.print();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Printing failed."
            );
        }
    }
}