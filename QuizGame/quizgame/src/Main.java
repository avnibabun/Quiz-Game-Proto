import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         OOP QUIZ GAME                ║");
        System.out.println("║   Object Oriented Programming        ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n  [1] Start Game");
            System.out.println("  [2] View Leaderboard");
            System.out.println("  [3] Exit");
            System.out.print("  Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("\n  Enter your name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) name = "Player";
                    Player player = new Player(name);
                    GameEngine engine = new GameEngine(player);
                    engine.startGame();
                    break;

                case "2":
                    Leaderboard lb = new Leaderboard();
                    lb.displayLeaderboard();
                    break;

                case "3":
                    System.out.println("\n  Goodbye! Thanks for playing.");
                    running = false;
                    break;

                default:
                    System.out.println("  [!] Invalid choice. Please enter 1, 2, or 3.");
            }
        }

        scanner.close();
    }
}