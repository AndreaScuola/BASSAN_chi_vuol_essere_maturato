//N.B. usa psvm per creazione del main e sout per la stampa

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ApiClient api = new ApiClient();

        System.out.print("Inserisci il numero di domande che vuoi: ");
        int amount = sc.nextInt();

        api.fetchQuestions(amount, "easy", "multiple");
    }
}
