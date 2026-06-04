
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    private static final String FILENAME = "leaderboard.txt";

    // Save a player's score to file
    public void saveScore(String playerName, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME, true))) {
            writer.write(playerName + ":" + score);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("[Warning] Could not save score: " + e.getMessage());
        }
    }

    // Read and display top 5 scores
    public void displayLeaderboard() {
        List<String[]> entries = new ArrayList<>();

        File file = new File(FILENAME);
        if (!file.exists()) {
            System.out.println("  No scores recorded yet.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    entries.add(parts);
                }
            }
        } catch (IOException e) {
            System.out.println("[Warning] Could not read leaderboard: " + e.getMessage());
            return;
        }

        // Sort by score descending
        entries.sort((a, b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));

        System.out.println("\n  ╔══════════════════════════════╗");
        System.out.println("  ║         TOP SCORES           ║");
        System.out.println("  ╠══════════════════════════════╣");

        int limit = Math.min(5, entries.size());
        for (int i = 0; i < limit; i++) {
            System.out.printf("  ║  %d. %-15s %7s pts  ║%n",
                i + 1, entries.get(i)[0], entries.get(i)[1]);
        }
        System.out.println("  ╚══════════════════════════════╝");
    }
}
