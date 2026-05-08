import java.util.ArrayList;
import java.util.List;
public class _10_TransactionHistory<T> {
    // Using the List Collection Interface backed by an ArrayList
    private final List<T> logs = new ArrayList<>();

    // Adds a generic transaction record to our dynamic array
    public void addLog(T entry) {
        logs.add(entry);
    }
    // Returns the complete dynamic collection
    public List<T> getLogs() {
        return logs;
    }
    // Prints all items stored in the history container
    public void displayLogs() {
        System.out.println("\n==========================================");
        System.out.println(" DYNAMIC TRANSACTION HISTORY (JAVA COLLECTION)");
        System.out.println("==========================================");
        if (logs.isEmpty()) {
            System.out.println("No transaction logs found.");
        } else {
            for (T log : logs) {
                System.out.println(" ⚡ " + log);
            }
        }
        System.out.println("==========================================\n");
    }
}