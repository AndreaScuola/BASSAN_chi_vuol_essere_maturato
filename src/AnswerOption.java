public class AnswerOption {
    private String text;
    private boolean correct;

    public AnswerOption(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return text;
    }
}