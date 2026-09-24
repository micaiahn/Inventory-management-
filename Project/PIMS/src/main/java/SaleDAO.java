import java.sql.*;
import java.util.List;

public class SaleDAO {

    public int createSale(int userId,
                          double total,
                          List<SaleItem> items) {

        Connection con = null;

        try {

            con = DBConnection.getConnection();

            con.setAutoCommit(false);

            String saleSQL =
                    "INSERT INTO sales(total_amount,user_id) VALUES(?,?)";

            PreparedStatement salePS =
                    con.prepareStatement(
                            saleSQL,
                            Statement.RETURN_GENERATED_KEYS
                    );

            salePS.setDouble(1, total);
            salePS.setInt(2, userId);

            salePS.executeUpdate();

            ResultSet keys =
                    salePS.getGeneratedKeys();

            int saleId;

            if (keys.next()) {
                saleId = keys.getInt(1);
            } else {
                throw new SQLException("Sale ID could not be generated.");
            }

            String itemSQL =
                    "INSERT INTO sale_items " +
                            "(sale_id,medicine_id,quantity_sold,price_at_sale) " +
                            "VALUES(?,?,?,?)";

            String stockSQL =
                    "UPDATE medicines " +
                            "SET quantity_in_stock = quantity_in_stock - ? " +
                            "WHERE medicine_id=? AND quantity_in_stock >= ?";

            PreparedStatement itemPS =
                    con.prepareStatement(itemSQL);

            PreparedStatement stockPS =
                    con.prepareStatement(stockSQL);

            for (SaleItem item : items) {

                itemPS.setInt(1, saleId);
                itemPS.setInt(2, item.getMedicineId());
                itemPS.setInt(3, item.getQuantitySold());
                itemPS.setDouble(4, item.getPriceAtSale());

                itemPS.executeUpdate();

                stockPS.setInt(1, item.getQuantitySold());
                stockPS.setInt(2, item.getMedicineId());
                stockPS.setInt(3, item.getQuantitySold());

                int updated =
                        stockPS.executeUpdate();

                if (updated == 0) {
                    throw new SQLException(
                            "Insufficient stock."
                    );
                }
            }

            con.commit();

            return saleId;

        } catch (SQLException e) {

            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            e.printStackTrace();

            return -1;

        } finally {

            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}