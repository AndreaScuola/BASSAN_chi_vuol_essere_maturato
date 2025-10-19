import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class APIQuestion {
    public String question;
    public String category;
    public String difficulty;
    public String correct_answer;
    public String[] incorrect_answers;

    public List<AnswerOption> getShuffledAnswers() {
        List<AnswerOption> options = new ArrayList<>();

        //Aggiungo le risposte e le mescolo
        options.add(new AnswerOption(correct_answer, true));
        for (String wrong : incorrect_answers) {
            options.add(new AnswerOption(wrong, false));
        }
        Collections.shuffle(options);

        return options;
    }

}
