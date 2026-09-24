import javax.swing.*;
import java.awt.*;

public class CashierDashboard extends JFrame {

    private User user;

    public CashierDashboard(User user) {

        this.user = user;

        setTitle(
                "HealthFirst - Cashier"
        );

        setSize(1100, 700);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        buildInterface();
    }

    private void buildInterface() {

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(
                new Color(25, 92, 140)
        );

        JLabel title =
                new JLabel(
                        "  HEALTHFIRST | POINT OF SALE"
                );

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        JLabel cashier =
                new JLabel(
                        "Cashier: "
                                + user.getFullName()
                                + "   "
                );

        cashier.setForeground(Color.WHITE);

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                cashier,
                BorderLayout.EAST
        );

        add(
                header,
                BorderLayout.NORTH
        );

        add(
                new POSPanel(user),
                BorderLayout.CENTER
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

        add(
                bottom,
                BorderLayout.SOUTH
        );
    }
}