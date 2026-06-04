
public class Question {
    private String questionText;
    private String[] options;       
    private int correctIndex;       
    private String difficulty;      

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
