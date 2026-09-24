import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private User user;

    public AdminDashboard(User user) {

        this.user = user;

        setTitle(
                "HealthFirst - Administrator Dashboard"
        );

        setSize(1100, 700);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        buildInterface();
    }

    private void buildInterface() {

        JPanel header = new JPanel(
                new BorderLayout()
        );

        header.setBackground(
                new Color(25, 92, 140)
        );

        JLabel title =
                new JLabel(
                        "  HEALTHFIRST | ADMIN DASHBOARD"
                );

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        JLabel welcome =
                new JLabel(
                        "Administrator: "
                                + user.getFullName()
                                + "   "
                );

        welcome.setForeground(Color.WHITE);

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                welcome,
                BorderLayout.EAST
        );

        JTabbedPane tabs =
                new JTabbedPane();

        tabs.addTab(
                "Dashboard",
                createDashboard()
        );

        tabs.addTab(
                "Medicines",
                new MedicinePanel()
        );

        tabs.addTab(
                "Suppliers",
                new SupplierPanel()
        );

        tabs.addTab(
                "Users",
                new UserPanel()
        );

        tabs.addTab(
                "Reports",
                new ReportsPanel()
        );

        JButton logout =
                new JButton("LOGOUT");

        logout.addActionListener(e -> {

            dispose();

            new LoginFrame()
                    .setVisible(true);
        });

        JPanel bottom =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        bottom.add(logout);

        add(header, BorderLayout.NORTH);

        add(tabs, BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createDashboard() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                2, 2, 20, 20
                        )
                );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        40, 40, 40, 40
                )
        );

        panel.add(
                createCard(
                        "MEDICINE MANAGEMENT",
                        "Add, update and delete medicines"
                )
        );

        panel.add(
                createCard(
                        "SUPPLIERS",
                        "Manage pharmacy suppliers"
                )
        );

        panel.add(
                createCard(
                        "USERS",
                        "Manage cashier accounts"
                )
        );

        panel.add(
                createCard(
                        "REPORTS",
                        "Sales, stock and expiry reports"
                )
        );

        return panel;
    }

    private JPanel createCard(
            String title,
            String description) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                Color.LIGHT_GRAY
                        ),
                        BorderFactory.createEmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        JLabel t =
                new JLabel(title);

        t.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        JLabel d =
                new JLabel(description);

        card.add(
                t,
                BorderLayout.NORTH
        );

        card.add(
                d,
                BorderLayout.CENTER
        );

        return card;
    }
}