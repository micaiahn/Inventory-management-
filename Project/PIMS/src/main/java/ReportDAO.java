import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<String[]> salesReport() {

        List<String[]> data = new ArrayList<>();

        String sql =
                "SELECT s.sale_id, s.sale_date, " +
                        "u.full_name, s.total_amount " +
                        "FROM sales s " +
                        "JOIN users u ON s.user_id=u.user_id " +
                        "ORDER BY s.sale_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                data.add(new String[]{
                        String.valueOf(rs.getInt("sale_id")),
                        rs.getTimestamp("sale_date").toString(),
                        rs.getString("full_name"),
                        String.format("R %.2f",
                                rs.getDouble("total_amount"))
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public List<String[]> itemWiseReport() {

        List<String[]> data = new ArrayList<>();

        String sql =
                "SELECT m.name, " +
                        "SUM(si.quantity_sold) AS qty, " +
                        "SUM(si.quantity_sold * si.price_at_sale) AS revenue " +
                        "FROM sale_items si " +
                        "JOIN medicines m ON si.medicine_id=m.medicine_id " +
                        "GROUP BY m.medicine_id,m.name " +
                        "ORDER BY qty DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                data.add(new String[]{
                        rs.getString("name"),
                        String.valueOf(rs.getInt("qty")),
                        String.format("R %.2f",
                                rs.getDouble("revenue"))
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public List<String[]> lowStockReport() {

        List<String[]> data = new ArrayList<>();

        String sql =
                "SELECT name, quantity_in_stock, reorder_level " +
                        "FROM medicines " +
                        "WHERE quantity_in_stock <= reorder_level " +
                        "ORDER BY quantity_in_stock";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                data.add(new String[]{
                        rs.getString("name"),
                        String.valueOf(
                                rs.getInt("quantity_in_stock")),
                        String.valueOf(
                                rs.getInt("reorder_level"))
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    public List<String[]> expiryReport() {

        List<String[]> data = new ArrayList<>();

        String sql =
                "SELECT name, expiry_date, " +
                        "DATEDIFF(expiry_date,CURDATE()) AS days_remaining " +
                        "FROM medicines " +
                        "WHERE expiry_date BETWEEN CURDATE() " +
                        "AND DATE_ADD(CURDATE(),INTERVAL 1 MONTH) " +
                        "ORDER BY expiry_date";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                data.add(new String[]{
                        rs.getString("name"),
                        rs.getDate("expiry_date").toString(),
                        String.valueOf(
                                rs.getInt("days_remaining"))
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }
}