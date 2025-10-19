//N.B. usa psvm per creazione del main e sout per la stampa

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        //1. MENU PRINCIPALE
        System.out.println("----------- CHI VUOLE ESSERE MATURATO -----------\n");
        System.out.println("Dovrai rispondere alle domande e avrai a disposizione degli aiuti: \n-'H1': 50/50 \n-'H2': Aiuto dal pubblico\nOppure puoi arrenderti con 'R'");

        //Variabili che verrano usate alla fine per creare playerStatistics
        String playerName;
        int correctAnswers = 0;
        boolean used5050 = false;
        boolean usedAudiance = false;

        System.out.print("Inserisci il nome del partecipante: ");
        playerName = sc.nextLine();


        //2. PREPARAZIONE DELLE DOMANDE
        ApiClient api = new ApiClient();
        ApiResponse apiResponseEasy = api.fetchQuestions(5, "easy", "multiple");
        ApiResponse apiResponseMedium = api.fetchQuestions(3, "medium", "multiple");
        ApiResponse apiResponseHard = api.fetchQuestions(2, "hard", "multiple");

        if(apiResponseEasy == null || apiResponseMedium == null || apiResponseHard == null)
            return;


        //3. LOOP DI GIOCO
        boolean perso = false;
        //AGGIUNGI GESTIONE PUNTEGGIO

        perso = faiDomande(apiResponseEasy, 0, used5050, usedAudiance);
        if(!perso) perso = faiDomande(apiResponseMedium, 5, used5050, usedAudiance);
        if(!perso) perso = faiDomande(apiResponseHard, 8, used5050, usedAudiance);


        //4. FINE ESECUZIONE --> scrittura nel json
        PlayerStatistics stats = new PlayerStatistics(playerName, correctAnswers, used5050, usedAudiance);

        Gson gson = new Gson();
        try(FileWriter fw = new FileWriter(playerName + "_stats.json")){
            gson.toJson(stats, fw);
            System.out.println("Statistiche salvate in " + playerName + "_stats.json");
        } catch (IOException e){
            System.out.println("Errore durante il salvataggio delle statistiche: " + e.getMessage());
        }

    }

    private static boolean faiDomande(ApiResponse apiResponse, int nDomanda, boolean used5050, boolean usedAudiance) {
        Scanner sc = new Scanner(System.in);
        boolean perso = false;

        for(APIQuestion domanda : apiResponse.results){
            nDomanda++;
            System.out.println("Domanda " + nDomanda + ", Difficolta " + domanda.difficulty + " - Categoria " + domanda.category);
            System.out.println(domanda.question + "\n");

            List<AnswerOption> options = domanda.getShuffledAnswers();
            System.out.println("A. " + options.get(0) + "\t\tB. " +  options.get(1) +
                                "\nC. " + options.get(2) + "\t\tD. " +  options.get(3));

            System.out.print("\nInserisci la risposta: ");
            String response = sc.next();

            //Controllo la risposta sia giusta / H1 e H2 / R
            switch (response.charAt(0)) {
                case 'A', 'B', 'C', 'D':
                    if(options.get((int)response.charAt(0) - 65).isCorrect())
                        System.out.println("Risposta corretta!");
                    else{
                        perso = true;
                        System.out.println("Sbagliato! \nLa risposta era: " + domanda.correct_answer);
                    }
                    break;

                case 'H':
                    //IMPLEMENTA AIUTI

                    break;

                default:
                    perso = true;
                    break;
            }

            if(perso)
                return true;
        }

        return perso;
    }
}
