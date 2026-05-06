import java.util.Scanner;

public class _8_TerminalPaymentCLI {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        _8_PaymentGateway gateway = new _8_FintechProcessor();

        System.out.println("=== 💸 DAY 8 PAYMENT GATEWAY CLI ===");

        System.out.print("Enter Sender Name: ");
        String sender = sc.next();

        System.out.print("Enter Receiver Name: ");
        String receiver = sc.next();

        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();

        // Processing via Interface logic[cite: 1]
        if (gateway.validateUser(sender, amount)) {
            gateway.processTransfer(sender, receiver, amount);
            System.out.println("💰 Transaction ID: TXN-" + System.currentTimeMillis());
        } else {
            System.out.println("❌ Transaction Denied: Insufficient Funds or User Not Found.");
        }
    }
}