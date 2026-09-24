import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public boolean addMedicine(Medicine m) {

        String sql = "INSERT INTO medicines " +
                "(name,company,medicine_type,price,quantity_in_stock," +
                "reorder_level,expiry_date,supplier_id) " +
                "VALUES(?,?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getCompany());
            ps.setString(3, m.getMedicineType());
            ps.setDouble(4, m.getPrice());
            ps.setInt(5, m.getQuantityInStock());
            ps.setInt(6, m.getReorderLevel());
            ps.setDate(7, m.getExpiryDate());
            ps.setInt(8, m.getSupplierId());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMedicine(Medicine m) {

        String sql = "UPDATE medicines SET name=?,company=?,medicine_type=?," +
                "price=?,quantity_in_stock=?,reorder_level=?,expiry_date=?," +
                "supplier_id=? WHERE medicine_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, m.getName());
            ps.setString(2, m.getCompany());
            ps.setString(3, m.getMedicineType());
            ps.setDouble(4, m.getPrice());
            ps.setInt(5, m.getQuantityInStock());
            ps.setInt(6, m.getReorderLevel());
            ps.setDate(7, m.getExpiryDate());
            ps.setInt(8, m.getSupplierId());
            ps.setInt(9, m.getMedicineId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMedicine(int id) {

        String sql = "DELETE FROM medicines WHERE medicine_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    public List<Medicine> getAllMedicines() {

        List<Medicine> list = new ArrayList<>();

        String sql = "SELECT * FROM medicines ORDER BY medicine_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                list.add(new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("company"),
                        rs.getString("medicine_type"),
                        rs.getDouble("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("reorder_level"),
                        rs.getDate("expiry_date"),
                        rs.getInt("supplier_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Medicine> searchMedicine(String keyword) {

        List<Medicine> list = new ArrayList<>();

        String sql = "SELECT * FROM medicines " +
                "WHERE name LIKE ? OR company LIKE ? " +
                "ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                list.add(new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("company"),
                        rs.getString("medicine_type"),
                        rs.getDouble("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("reorder_level"),
                        rs.getDate("expiry_date"),
                        rs.getInt("supplier_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Medicine getMedicineById(int id) {

        String sql = "SELECT * FROM medicines WHERE medicine_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("company"),
                        rs.getString("medicine_type"),
                        rs.getDouble("price"),
                        rs.getInt("quantity_in_stock"),
                        rs.getInt("reorder_level"),
                        rs.getDate("expiry_date"),
                        rs.getInt("supplier_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}