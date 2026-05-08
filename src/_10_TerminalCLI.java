import java.util.Scanner;
public class _10_TerminalCLI {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        _10_FintechManager manager = new _10_FintechManager();
        System.out.println("==========================================");
        System.out.println("        DAY 10: FINTECH TOOLKIT CLI       ");
        System.out.println("==========================================");

        System.out.print("Enter username to check logs (e.g., teja, suri): ");
        String username = sc.next().trim().toLowerCase(); // Normalize input text

        System.out.println("\nSyncing with database...");
        manager.fetchHistory(username);

        sc.close();
    }
}