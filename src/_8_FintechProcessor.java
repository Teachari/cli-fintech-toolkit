import java.sql.*;

public class _8_FintechProcessor implements _8_PaymentGateway {
    private String url = "jdbc:mysql://localhost:3306/fintech_db";
    private String user = "root";
    private String pass = "Teja@8004";

    @Override
    public boolean validateUser(String name, double amt) {
        // Sync check: Does the user exist in digital_wallets?
        String sql = "SELECT cash_balance FROM digital_wallets WHERE user_tag = ? AND cash_balance >= ?";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, amt);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) { return false; }
    }

    @Override
    public void processTransfer(String sender, String receiver, double amt) {
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            conn.setAutoCommit(false); // ACID Transaction Start

            Statement st = conn.createStatement();
            // 1. Deduct from Sender
            st.executeUpdate("UPDATE digital_wallets SET cash_balance = cash_balance - " + amt + " WHERE user_tag = '" + sender + "'");
            // 2. Add to Receiver
            st.executeUpdate("UPDATE digital_wallets SET cash_balance = cash_balance + " + amt + " WHERE user_tag = '" + receiver + "'");

            conn.commit(); // Sync Complete

            // PRINT SYNC RESULTS TO TERMINAL
            System.out.println("\n✅ TRANSACTION SYNCED SUCCESSFULLY");
            System.out.println("-----------------------------------");
            showStatus(conn, sender, receiver);

        } catch (SQLException e) { System.out.println("❌ Sync Error: " + e.getMessage()); }
    }

    private void showStatus(Connection conn, String s, String r) throws SQLException {
        String sql = "SELECT user_tag, cash_balance FROM digital_wallets WHERE user_tag IN (?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, s);
        ps.setString(2, r);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            System.out.println("Account: " + rs.getString("user_tag") + " | Final Balance: $" + rs.getDouble("cash_balance"));
        }
    }
}