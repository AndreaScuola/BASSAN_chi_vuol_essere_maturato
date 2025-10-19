public class PlayerStatistics {
    public String name;
    public int correctAnswers;
    public boolean used5050;
    public boolean usedAudiance;

    public PlayerStatistics(String name, int correctAnswers, boolean used5050, boolean usedAudiance) {
        this.name = name;
        this.correctAnswers = correctAnswers;
        this.used5050 = used5050;
        this.usedAudiance = usedAudiance;
    }
}
