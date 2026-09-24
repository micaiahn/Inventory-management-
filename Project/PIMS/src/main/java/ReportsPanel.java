import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {

    private JTable table;

    private JLabel title;

    private ReportDAO dao =
            new ReportDAO();

    public ReportsPanel() {

        setLayout(
                new BorderLayout(10, 10)
        );

        buildInterface();
    }

    private void buildInterface() {

        JPanel top =
                new JPanel();

        JButton sales =
                new JButton("SALES REPORT");

        JButton itemWise =
                new JButton("ITEM-WISE REPORT");

        JButton lowStock =
                new JButton("LOW STOCK");

        JButton expiry =
                new JButton("EXPIRY REPORT");

        top.add(sales);
        top.add(itemWise);
        top.add(lowStock);
        top.add(expiry);

        title =
                new JLabel(
                        "Select a report",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        table =
                new JTable();

        add(top, BorderLayout.NORTH);

        add(title, BorderLayout.CENTER);

        add(
                new JScrollPane(table),
                BorderLayout.SOUTH
        );

        sales.addActionListener(
                e -> salesReport()
        );

        itemWise.addActionListener(
                e -> itemWiseReport()
        );

        lowStock.addActionListener(
                e -> lowStockReport()
        );

        expiry.addActionListener(
                e -> expiryReport()
        );
    }

    private void salesReport() {

        title.setText(
                "SALES REPORT"
        );

        List<String[]> data =
                dao.salesReport();

        String[] columns = {
                "Sale ID",
                "Date",
                "Cashier",
                "Total"
        };

        display(data, columns);
    }

    private void itemWiseReport() {

        title.setText(
                "ITEM-WISE SALES REPORT"
        );

        List<String[]> data =
                dao.itemWiseReport();

        String[] columns = {
                "Medicine",
                "Quantity Sold",
                "Revenue"
        };

        display(data, columns);
    }

    private void lowStockReport() {

        title.setText(
                "LOW STOCK REPORT"
        );

        List<String[]> data =
                dao.lowStockReport();

        String[] columns = {
                "Medicine",
                "Current Stock",
                "Reorder Level"
        };

        display(data, columns);
    }

    private void expiryReport() {

        title.setText(
                "EXPIRY REPORT - NEXT ONE MONTH"
        );

        List<String[]> data =
                dao.expiryReport();

        String[] columns = {
                "Medicine",
                "Expiry Date",
                "Days Remaining"
        };

        display(data, columns);
    }

    private void display(
            List<String[]> data,
            String[] columns) {

        DefaultTableModel model =
                new DefaultTableModel(
                        columns,
                        0
                );

        for (String[] row : data) {

            model.addRow(row);
        }

        table.setModel(model);

        add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        revalidate();
        repaint();
    }
}