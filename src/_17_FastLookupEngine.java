
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class _17_FastLookupEngine {
    private static final String URL = "jdbc:mysql://localhost:3306/fintech_db";
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
    private static final String PASS = System.getenv("DB_PASS");

    public static void main(String[] args) {
        if (PASS == null) {
            System.err.println(" DB_PASS not found in Environment Variables!");
            return;
        }

        System.out.println("==========================================");
        System.out.println("  FAST LOOKUP & REPLICA CACHE (DAY 17) ");
        System.out.println("==========================================");

        // 1. HashMap for O(1) Instant Lookup (Key = user_tag, Value = Full Name)
        Map<String, String> fastLookupMap = new HashMap<>();

        // 2. TreeMap for Sorted Reporting (Automatically sorts by UserTag)
        Map<String, String> sortedUserMap = new TreeMap<>();

        // 3. Simulating 'Replication': Loading DB data into Memory
        loadProfilesFromDB(fastLookupMap, sortedUserMap);

        // 4. Test the Fast Lookup
        String searchTag = "teja";
        System.out.println("\n Performing Instant Memory Lookup for: " + searchTag);

        if (fastLookupMap.containsKey(searchTag)) {
            System.out.println(" Result found: " + fastLookupMap.get(searchTag));
        } else {
            System.out.println(" User not in cache.");
        }

        // 5. Display Sorted User List (TreeMap magic)
        System.out.println("\n Alphabetical User Directory (Sorted by TreeMap):");
        sortedUserMap.forEach((tag, name) -> System.out.println(" - [" + tag + "]: " + name));
    }

    private static void loadProfilesFromDB(Map<String, String> hashmap, Map<String, String> treemap) {
        String query = "SELECT user_tag, full_name FROM user_profiles";

        System.out.println(" Replication Sync: Syncing Memory Cache with MySQL...");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String tag = rs.getString("user_tag");
                String name = rs.getString("full_name");

                // Put data into both maps
                hashmap.put(tag, name);
                treemap.put(tag, name);
            }
            System.out.println(" Cache Sync Complete. " + hashmap.size() + " profiles loaded.");

        } catch (SQLException e) {
            System.err.println(" Connection failed: " + e.getMessage());
        }
    }
}