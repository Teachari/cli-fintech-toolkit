public interface _8_PaymentGateway {
    // Contract: Every gateway must validate and transfer[cite: 1]
    boolean validateUser(String name, double amount);
    void processTransfer(String sender, String receiver, double amount);
}