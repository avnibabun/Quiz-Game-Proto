
public class Player {
    private String name;
    private int score;
    private int lives;          // consecutive wrong answers → game over
    private int joker5050;      // 50/50 lifeline count
    private int jokerFreeze;    // time freeze lifeline count
    private int wrongStreak;    // consecutive wrong answer counter

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.lives = 3;         // 3 consecutive wrongs = game over
        this.joker5050 = 1;
        this.jokerFreeze = 1;
        this.wrongStreak = 0;
    }

    public void addScore(int points)    { score += points; }
    public void resetWrongStreak()      { wrongStreak = 0; }
    public boolean incrementWrongStreak() {
        wrongStreak++;
        return wrongStreak >= 3; // returns true if game over condition met
    }

    public boolean useJoker5050() {
        if (joker5050 > 0) { joker5050--; return true; }
        return false;
    }

    public boolean useJokerFreeze() {
        if (jokerFreeze > 0) { jokerFreeze--; return true; }
        return false;
    }

    public String getName()     { return name; }
    public int getScore()       { return score; }
    public int getLives()       { return lives; }
    public int getJoker5050()   { return joker5050; }
    public int getJokerFreeze() { return jokerFreeze; }
    public int getWrongStreak() { return wrongStreak; }
}
