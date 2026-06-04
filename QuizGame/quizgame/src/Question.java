
public class Question {
    private String questionText;
    private String[] options;       // 4 options: A, B, C, D
    private int correctIndex;       // 0=A, 1=B, 2=C, 3=D
    private String difficulty;      // "easy", "medium", "hard"

    public Question(String questionText, String[] options, int correctIndex, String difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctIndex = correctIndex;
        this.difficulty = difficulty;
    }

    public String getQuestionText() { return questionText; }
    public String[] getOptions()    { return options; }
    public int getCorrectIndex()    { return correctIndex; }
    public String getDifficulty()   { return difficulty; }

    public boolean isCorrect(int answerIndex) {
        return answerIndex == correctIndex;
    }
}
