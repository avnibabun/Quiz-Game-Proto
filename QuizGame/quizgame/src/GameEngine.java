import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class GameEngine {
    private Player player;
    private QuestionPool questionPool;
    private Leaderboard leaderboard;
    private Scanner scanner;
    // Zaman ayarlı girdi için ExecutorService tanımı
    private ExecutorService executor;

    // Difficulty scaling thresholds
    private static final int MEDIUMTHRESHOLD = 3;
    private static final int HARDTHRESHOLD = 7;

    // Time limits per difficulty (seconds)
    private static final int TIMEEASY   = 15;
    private static final int TIMEMEDIUM = 12;
    private static final int TIMEHARD   = 9;
    private static final int TIME_EASY = 0;

    public GameEngine(Player player) {
        this.player = player;
        this.questionPool = new QuestionPool();
        this.leaderboard = new Leaderboard();
        this.scanner = new Scanner(System.in);
        // Arka plan görevlerini yönetecek thread havuzunu oluşturuyoruz
        this.executor = Executors.newSingleThreadExecutor();
    }

    // Kullanıcıdan zaman ayarlı girdi alan yardımcı metot
    private String getInputWithTimeout(int seconds) throws TimeoutException, InterruptedException, ExecutionException {
        Callable<String> task = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (System.in.available() > 0) {
                    return scanner.nextLine().trim().toUpperCase();
                }
                Thread.sleep(50); // İşlemciyi yormamak için küçük bir bekleme
            }
            return "";
        };

        Future<String> future = executor.submit(task);
        try {
            // Belirtilen süre boyunca girdiyi bekler
            return future.get(seconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // Süre dolduğunda görevi iptal et
            throw e;
        }
    }

    public void startGame() {
        System.out.println("\n  ════════════════════════════════");
        System.out.println("       QUIZ GAME  —  Starting!");
        System.out.println("  ════════════════════════════════");
        System.out.println("  Player : " + player.getName());
        System.out.println("  Lives  : 3 consecutive wrong = Game Over");
        System.out.println("  Jokers : 50/50 (press J1) | Time Freeze (press J2)");
        System.out.println("  ────────────────────────────────\n");

        List<Question> questions = questionPool.getAllQuestions();
        int questionNumber = 0;

        for (Question question : questions) {
            questionNumber++;

            // Progressive difficulty: skip questions not matching current tier
            String currentTier = getCurrentTier(questionNumber);
            if (!question.getDifficulty().equals(currentTier) && questionNumber <= 5) {
                // For first 5 questions, only show easy questions
                if (!question.getDifficulty().equals("easy")) continue;
            }

            boolean gameOver = askQuestion(question, questionNumber);
            if (gameOver) {
                endGame(false);
                return;
            }

            // Small pause feel between questions
            System.out.println();
        }

        endGame(true); // Completed all questions
    }

    private String getCurrentTier(int questionNumber) {
        if (questionNumber <= MEDIUMTHRESHOLD) return "easy";
        if (questionNumber <= HARDTHRESHOLD)   return "medium";
        return "hard";
    }

    // Returns true if game over condition is triggered
    private boolean askQuestion(Question question, int number) {
        int timeLimit = getTimeLimit(question.getDifficulty());
        long startTime = System.currentTimeMillis();
        boolean freezeActive = false;

        System.out.println("  ┌─ Question " + number
            + "  [" + question.getDifficulty().toUpperCase() + "]"
            + "  Score: " + player.getScore()
            + "  Wrong Streak: " + player.getWrongStreak() + "/3"
            + "  ─────────────────────────────────");
        System.out.println("  │ " + question.getQuestionText());
        System.out.println("  │");

        boolean[] eliminated = new boolean[4]; // tracks 50/50 eliminated options

        // Show joker status
        System.out.println("  │ [J1] 50/50 (" + player.getJoker5050() + " left)"
            + "  [J2] Time Freeze (" + player.getJokerFreeze() + " left)");
        System.out.println("  │ Time limit: " + timeLimit + "s");
        System.out.println("  │");

        // Print options
        String[] opts = question.getOptions();
        for (int i = 0; i < 4; i++) {
            if (!eliminated[i]) {
                System.out.println("  │   " + opts[i]);
            }
        }
        System.out.print("  └─ Your answer (A/B/C/D) or J1/J2: ");

        String input = "";
        try {
            // Zaman ayarlı girdiyi çağırıyoruz
            input = getInputWithTimeout(timeLimit);
        } catch (TimeoutException e) {
            System.out.println("\n\n  ⏰ SÜRE DOLDU! Zaman sınırını aştınız.");
            return handleWrongAnswer();
        } catch (Exception e) {
            input = ""; // Olası diğer hatalarda boş girdi kabul etsin
        }

        // Handle jokers
        if (input.equals("J1")) {
            if (player.useJoker5050()) {
                int[] toEliminate = questionPool.getFiftyFiftyEliminations(question);
                eliminated[toEliminate[0]] = true;
                eliminated[toEliminate[1]] = true;
                System.out.println("\n  ► 50/50 applied! Two wrong answers removed:\n");
                for (int i = 0; i < 4; i++) {
                    if (!eliminated[i]) {
                        System.out.println("       " + opts[i]);
                    }
                }
                System.out.print("  └─ Your answer (A/B/C/D): ");
                
                // Joker sonrası kalan süreyi hesapla
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                int remainingTime = Math.max(1, timeLimit - (int)elapsedTime);
                
                try {
                    input = getInputWithTimeout(remainingTime);
                } catch (TimeoutException e) {
                    System.out.println("\n\n  ⏰ SÜRE DOLDU! Zaman sınırını aştınız.");
                    return handleWrongAnswer();
                } catch (Exception e) { input = ""; }
            } else {
                System.out.println("  [!] No 50/50 jokers left!");
                System.out.print("  └─ Your answer (A/B/C/D): ");
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                int remainingTime = Math.max(1, timeLimit - (int)elapsedTime);
                try {
                    input = getInputWithTimeout(remainingTime);
                } catch (TimeoutException e) {
                    System.out.println("\n\n  ⏰ SÜRE DOLDU! Zaman sınırını aştınız.");
                    return handleWrongAnswer();
                } catch (Exception e) { input = ""; }
            }
        } else if (input.equals("J2")) {
            if (player.useJokerFreeze()) {
                freezeActive = true;
                timeLimit = 999; // effectively paused
                System.out.println("  ► Time Freeze activated! Take your time.\n");
                for (int i = 0; i < 4; i++) {
                    if (!eliminated[i]) { // Elenenler varsa onları tekrar basma
                        System.out.println("       " + opts[i]);
                    }
                }
                System.out.print("  └─ Your answer (A/B/C/D): ");
                try {
                    // Dondurma aktifken geniş bir süre (999sn) veriyoruz
                    input = getInputWithTimeout(timeLimit);
                } catch (TimeoutException e) {
                    System.out.println("\n\n  ⏰ SÜRE DOLDU! Zaman sınırını aştınız.");
                    return handleWrongAnswer();
                } catch (Exception e) { input = ""; }
            } else {
                System.out.println("  [!] No Time Freeze jokers left!");
                System.out.print("  └─ Your answer (A/B/C/D): ");
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                int remainingTime = Math.max(1, timeLimit - (int)elapsedTime);
                try {
                    input = getInputWithTimeout(remainingTime);
                } catch (TimeoutException e) {
                    System.out.println("\n\n  ⏰ Time is up! You've exceeded the limit.");
                    return handleWrongAnswer();
                } catch (Exception e) { input = ""; }
            }
        }

        // Convert letter to index
        int answerIndex = letterToIndex(input);
        if (answerIndex == -1) {
            System.out.println("  [!] Invalid input — counted as wrong answer.");
            return handleWrongAnswer();
        }

        // Check if eliminated by 50/50
        if (eliminated[answerIndex]) {
            System.out.println("  [!] That option was eliminated by 50/50 — counted as wrong.");
            return handleWrongAnswer();
        }

        // Evaluate answer
        if (question.isCorrect(answerIndex)) {
            // Kalan gerçek zamana göre puan hesaplamak istersen elapsedTime kullanabilirsin.
            // Mevcut yapıyı bozmamak için direkt çağrıldı.
            int points = calculatePoints(question.getDifficulty(), timeLimit, freezeActive);
            player.addScore(points);
            player.resetWrongStreak();
            System.out.println("  ✔️  CORRECT! +" + points + " points  (Total: " + player.getScore() + ")");
            return false;
        } else {
            System.out.println("  ✘  WRONG! Correct answer was: "
                + opts[question.getCorrectIndex()]);
            return handleWrongAnswer();
        }
    }

    private boolean handleWrongAnswer() {
        boolean gameOver = player.incrementWrongStreak();
        if (gameOver) {
            System.out.println("  ✘✘✘ 3 consecutive wrong answers — GAME OVER!");
        } else {
            System.out.println("  [!] Wrong streak: " + player.getWrongStreak() + "/3");
        }
        return gameOver;
    }

    private int calculatePoints(String difficulty, int timeLimit, boolean frozen) {
        int base;
        switch (difficulty) {
            case "easy":   base = 100; break;
            case "medium": base = 200; break;
            case "hard":   base = 300; break;
            default:       base = 100;
        }
        int timeBonus = frozen ? timeLimit : (int)(Math.random() * timeLimit);
        return base + timeBonus;
    }

    private int getTimeLimit(String difficulty) {
        switch (difficulty) {
            case "easy":   return TIMEEASY;
            case "medium": return TIMEMEDIUM;
            case "hard":   return TIMEHARD;
            default:       return TIME_EASY;
        }
    }

    private int letterToIndex(String letter) {
        switch (letter) {
            case "A": return 0;
            case "B": return 1;
            case "C": return 2;
            case "D": return 3;
            default:  return -1;
        }
    }

    private void endGame(boolean completed) {
        System.out.println("\n  ════════════════════════════════");
        if (completed) {
            System.out.println("       GAME COMPLETE! Well done!");
        } else {
            System.out.println("            GAME OVER");
        }
        System.out.println("  ════════════════════════════════");
        System.out.println("  Player : " + player.getName());
        System.out.println("  Score  : " + player.getScore() + " points");
        System.out.println("  ────────────────────────────────");

        leaderboard.saveScore(player.getName(), player.getScore());
        leaderboard.displayLeaderboard();
        
        // Uygulamanın arka planda asılı kalmaması için executor'ı kapatıyoruz
        executor.shutdown();
    }
}
