import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class _12_InterestCalculator {
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = "root";
    private static final String PASS = "Teja@8004";

    public Optional<LocalDate> getFirstTransactionDate(String userTag) {
        String query = "SELECT MIN(created_at) as first_tx FROM transaction_logs WHERE user_tag = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, userTag);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp timestamp = rs.getTimestamp("first_tx");
                    if (timestamp != null) {
                        // Convert Timestamp to modern java.time.LocalDate safely
                        return Optional.of(timestamp.toLocalDateTime().toLocalDate());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(" Database read error: " + e.getMessage());
        }
        return Optional.empty(); // Safely returns empty instead of null
    }

    public double callInterestProcedure(String userTag, double dailyRate) {
        String procedureCall = "{CALL CalculateInterest(?, ?, ?)}";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             CallableStatement cs = conn.prepareCall(procedureCall)) {

            cs.setString(1, userTag);
            cs.setDouble(2, dailyRate);

            // Register OUT parameter (3rd parameter is the decimal interest earned)
            cs.registerOutParameter(3, java.sql.Types.DECIMAL);

            cs.execute();

            return cs.getDouble(3);

        } catch (SQLException e) {
            System.err.println(" Error executing Stored Procedure: " + e.getMessage());
        }
        return 0.0;
    }
}