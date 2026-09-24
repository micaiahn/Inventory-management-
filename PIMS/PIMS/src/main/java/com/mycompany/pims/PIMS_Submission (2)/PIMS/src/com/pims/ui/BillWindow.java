package com.pims.ui;

import com.pims.model.CartItem;
import com.pims.util.Session;
import com.pims.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Printable/saveable bill shown to the cashier immediately after a
 * successful checkout. One window per completed sale.
 */
public class BillWindow extends JFrame {

    private final int saleId;
    private final List<CartItem> items;
    private final BigDecimal grandTotal;
    private final JTable table;

    public BillWindow(int saleId, List<CartItem> items, BigDecimal grandTotal) {
        this.saleId = saleId;
        this.items = items;
        this.grandTotal = grandTotal;

        setTitle("HealthFirst Pharmacy - Bill / Receipt");
        setSize(480, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(UIStyle.WHITE);
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        root.add(buildHeader(), BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Medicine", "Qty", "Unit Price", "Line Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (CartItem item : items) {
            model.addRow(new Object[]{item.getName(), item.getQuantity(),
                    item.getUnitPrice(), item.getLineTotal()});
        }
        table = new JTable(model);
        UIStyle.styleTable(table);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel name = new JLabel("HealthFirst Pharmacy");
        name.setFont(UIStyle.FONT_TITLE);
        name.setForeground(UIStyle.PRIMARY_DARK);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Official Sales Receipt");
        sub.setFont(UIStyle.FONT_LABEL);
        sub.setForeground(Color.GRAY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel meta = new JLabel("<html><center>Sale #" + saleId + " &nbsp;|&nbsp; "
                + new SimpleDateFormat("dd MMM yyyy, HH:mm").format(new Date())
                + "<br>Served by: " + Session.getFullName() + "</center></html>");
        meta.setFont(UIStyle.FONT_LABEL);
        meta.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.setBorder(new EmptyBorder(8, 0, 8, 0));

        p.add(name);
        p.add(sub);
        p.add(meta);
        p.add(new JSeparator());
        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setOpaque(false);

        JLabel lblTotal = new JLabel("Grand Total: " + grandTotal.setScale(2, java.math.RoundingMode.HALF_UP));
        lblTotal.setFont(UIStyle.FONT_TITLE);
        lblTotal.setForeground(UIStyle.PRIMARY_DARK);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(lblTotal, BorderLayout.NORTH);

        JLabel thanks = new JLabel("Thank you for choosing HealthFirst Pharmacy!");
        thanks.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        thanks.setForeground(Color.GRAY);
        thanks.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(thanks, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.setOpaque(false);
        JButton btnPrint = UIStyle.primaryButton("Print");
        JButton btnSave = UIStyle.accentButton("Save as Text");
        JButton btnClose = UIStyle.neutralButton("Close");
        btnPrint.addActionListener(e -> printBill());
        btnSave.addActionListener(e -> saveBillToFile());
        btnClose.addActionListener(e -> dispose());
        buttons.add(btnPrint);
        buttons.add(btnSave);
        buttons.add(btnClose);
        p.add(buttons, BorderLayout.SOUTH);

        return p;
    }

    /** Sends the bill table to the default system printer (or "Print to PDF"). */
    private void printBill() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable((Printable) (graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
                printableContent(g2);
                return Printable.PAGE_EXISTS;
            });
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Could not print: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printableContent(Graphics2D g2) {
        int y = 15;
        g2.setFont(new Font("Serif", Font.BOLD, 16));
        g2.drawString("HealthFirst Pharmacy - Receipt", 0, y);
        y += 20;
        g2.setFont(new Font("Serif", Font.PLAIN, 11));
        g2.drawString("Sale #" + saleId + "   Cashier: " + Session.getFullName(), 0, y);
        y += 20;
        for (CartItem item : items) {
            g2.drawString(item.getName() + "  x" + item.getQuantity() + "   @ " + item.getUnitPrice()
                    + "   = " + item.getLineTotal(), 0, y);
            y += 16;
        }
        y += 10;
        g2.setFont(new Font("Serif", Font.BOLD, 13));
        g2.drawString("Grand Total: " + grandTotal.setScale(2, java.math.RoundingMode.HALF_UP), 0, y);
    }

    /** Saves a plain-text copy of the receipt (fallback for machines without a printer set up). */
    private void saveBillToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("bill_sale_" + saleId + ".txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
            fw.write("HealthFirst Pharmacy - Official Sales Receipt\n");
            fw.write("================================================\n");
            fw.write("Sale #" + saleId + "\n");
            fw.write("Date: " + new SimpleDateFormat("dd MMM yyyy, HH:mm").format(new Date()) + "\n");
            fw.write("Cashier: " + Session.getFullName() + "\n");
            fw.write("------------------------------------------------\n");
            for (CartItem item : items) {
                fw.write(String.format("%-25s x%-4d @ %-8s = %s%n",
                        item.getName(), item.getQuantity(), item.getUnitPrice(), item.getLineTotal()));
            }
            fw.write("------------------------------------------------\n");
            fw.write("Grand Total: " + grandTotal.setScale(2, java.math.RoundingMode.HALF_UP) + "\n");
            fw.write("Thank you for choosing HealthFirst Pharmacy!\n");
            JOptionPane.showMessageDialog(this, "Bill saved to " + chooser.getSelectedFile().getName());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save file: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
