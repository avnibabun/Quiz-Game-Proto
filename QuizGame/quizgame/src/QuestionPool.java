
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionPool {
    private List<Question> questions;

    public QuestionPool() {
        questions = new ArrayList<>();
        loadQuestions();
    }

    private void loadQuestions() {
        // --- SCIENCE ---
        questions.add(new Question(
            "What is the chemical symbol for water?",
            new String[]{"A) H2O", "B) CO2", "C) O2", "D) NaCl"},
            0, "easy"
        ));
        questions.add(new Question(
            "How many planets are in our Solar System?",
            new String[]{"A) 7", "B) 8", "C) 9", "D) 10"},
            1, "easy"
        ));
        questions.add(new Question(
            "What is the powerhouse of the cell?",
            new String[]{"A) Nucleus", "B) Ribosome", "C) Mitochondria", "D) Chloroplast"},
            2, "medium"
        ));
        questions.add(new Question(
            "What is the speed of light (approx.) in km/s?",
            new String[]{"A) 100,000", "B) 300,000", "C) 500,000", "D) 1,000,000"},
            1, "medium"
        ));
        questions.add(new Question(
            "What particle has a negative charge?",
            new String[]{"A) Proton", "B) Neutron", "C) Electron", "D) Positron"},
            2, "easy"
        ));

        // --- HISTORY ---
        questions.add(new Question(
            "In which year did World War II end?",
            new String[]{"A) 1943", "B) 1944", "C) 1945", "D) 1946"},
            2, "easy"
        ));
        questions.add(new Question(
            "Who was the first President of the United States?",
            new String[]{"A) Abraham Lincoln", "B) Thomas Jefferson", "C) John Adams", "D) George Washington"},
            3, "easy"
        ));
        questions.add(new Question(
            "Which empire was ruled by Suleiman the Magnificent?",
            new String[]{"A) Roman Empire", "B) Ottoman Empire", "C) Byzantine Empire", "D) Mongol Empire"},
            1, "medium"
        ));
        questions.add(new Question(
            "The French Revolution began in which year?",
            new String[]{"A) 1776", "B) 1789", "C) 1799", "D) 1804"},
            1, "medium"
        ));
        questions.add(new Question(
            "Who wrote 'The Art of War'?",
            new String[]{"A) Confucius", "B) Laozi", "C) Sun Tzu", "D) Genghis Khan"},
            2, "hard"
        ));

        // --- TECHNOLOGY ---
        questions.add(new Question(
            "What does CPU stand for?",
            new String[]{"A) Central Process Unit", "B) Core Processing Unit", "C) Central Processing Unit", "D) Computer Processing Unit"},
            2, "easy"
        ));
        questions.add(new Question(
            "Which company created the Java programming language?",
            new String[]{"A) Microsoft", "B) Apple", "C) Google", "D) Sun Microsystems"},
            3, "medium"
        ));
        questions.add(new Question(
            "What does OOP stand for in programming?",
            new String[]{"A) Object Oriented Programming", "B) Open Object Protocol", "C) Ordered Output Processing", "D) Object Optimal Processing"},
            0, "easy"
        ));
        questions.add(new Question(
            "Which data structure works on LIFO principle?",
            new String[]{"A) Queue", "B) Stack", "C) Array", "D) Tree"},
            1, "medium"
        ));
        questions.add(new Question(
            "What is binary code's base number?",
            new String[]{"A) 8", "B) 10", "C) 16", "D) 2"},
            3, "easy"
        ));

        // --- GEOGRAPHY ---
        questions.add(new Question(
            "What is the capital of Australia?",
            new String[]{"A) Sydney", "B) Melbourne", "C) Canberra", "D) Brisbane"},
            2, "medium"
        ));
        questions.add(new Question(
            "Which is the longest river in the world?",
            new String[]{"A) Amazon", "B) Yangtze", "C) Mississippi", "D) Nile"},
            3, "easy"
        ));
        questions.add(new Question(
            "Mount Everest is located in which mountain range?",
            new String[]{"A) Andes", "B) Alps", "C) Himalayas", "D) Rockies"},
            2, "easy"
        ));
        questions.add(new Question(
            "Which country has the most natural lakes?",
            new String[]{"A) Russia", "B) USA", "C) Canada", "D) Brazil"},
            2, "hard"
        ));
        questions.add(new Question(
            "The Sahara Desert is located on which continent?",
            new String[]{"A) Asia", "B) Australia", "C) South America", "D) Africa"},
            3, "easy"
        ));

        Collections.shuffle(questions);
    }

    
    public List<Question> getAllQuestions() {
        List<Question> copy = new ArrayList<>(questions);
        Collections.shuffle(copy);
        return copy;
    }

    
    public int[] getFiftyFiftyEliminations(Question question) {
        List<Integer> wrongIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != question.getCorrectIndex()) {
                wrongIndices.add(i);
            }
        }
        Collections.shuffle(wrongIndices);
        return new int[]{wrongIndices.get(0), wrongIndices.get(1)};
    }
}
