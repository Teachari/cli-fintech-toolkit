import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/fintech_db";
        String user = "root";
        String password = "Teja@8004";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Scanner sc = new Scanner(System.in);
            System.out.println("--- FinTech CLI Calculator ---");

            System.out.print("Enter first number: ");
            double n1 = sc.nextDouble();
            System.out.print("Enter second number: ");
            double n2 = sc.nextDouble();
            System.out.print("Enter operation (+, -, *, /): ");
            String op = sc.next();

            double res = 0;
            if (op.equals("+")) res = n1 + n2;
            else if (op.equals("-")) res = n1 - n2;
            else if (op.equals("*")) res = n1 * n2;
            else if (op.equals("/")) res = n1 / n2;

            System.out.println("Result: " + res);

            // Afternoon Goal: INSERT record into History Table
            String sql = "INSERT INTO calc_history (operation, num1, num2, result) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, op);
            pstmt.setDouble(2, n1);
            pstmt.setDouble(3, n2);
            pstmt.setDouble(4, res);
            pstmt.executeUpdate();

            // Afternoon Goal: SELECT and display History
            System.out.println("\n--- Recent History ---");
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM calc_history ORDER BY id DESC LIMIT 5");
            while (rs.next()) {
                System.out.println(rs.getDouble("num1") + " " + rs.getString("operation") + " " +
                        rs.getDouble("num2") + " = " + rs.getDouble("result"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
