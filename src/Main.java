//N.B. usa psvm per creazione del main e sout per la stampa

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ApiClient api = new ApiClient();

        System.out.print("Inserisci il numero di domande che vuoi: ");
        int amount = sc.nextInt();

        api.fetchQuestions(amount, "easy", "multiple");


        //Variabili che verrano usate alla fine per creare playerStatistics
        String playerName;
        int correctAnswers = 0;
        boolean used5050 = false;
        boolean usedAudiance = false;

        System.out.print("Inserisci il nome: ");
        playerName = sc.nextLine();




        //Fine esecuzione --> scrittura nel json
        PlayerStatistics stats = new PlayerStatistics(playerName, correctAnswers, used5050, usedAudiance);

        Gson gson = new Gson();
        try(FileWriter fw = new FileWriter(playerName + "_stats.json")){
            gson.toJson(stats, fw);
            System.out.println("Statistiche salvate in " + playerName + "_stats.json");
        } catch (IOException e){
            System.out.println("Errore durante il salvataggio delle statistiche: " + e.getMessage());
        }

    }
}
