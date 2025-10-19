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
        System.out.println("REGOLE: \nDovrai rispondere alle domande e avrai a disposizione degli aiuti: \n-'H1': 50/50 \n-'H2': Aiuto dal pubblico\nOppure puoi arrenderti con 'R'\n\n");

        System.out.print("Inserisci il nome del partecipante: ");
        String playerName = sc.nextLine();
        PlayerStatistics stats = new PlayerStatistics(playerName, 0, false, false); //Usato per passare i dati tra metodi


        //2. PREPARAZIONE DELLE DOMANDE + 3. LOOP DI GIOCO

        ApiClient api = new ApiClient();
        boolean perso;

        ApiResponse apiResponseEasy = api.fetchQuestions(5, "easy", "multiple");
        if(apiResponseEasy == null) return;
        perso = faiDomande(apiResponseEasy, stats);

        if(!perso){
            ApiResponse apiResponseMedium = api.fetchQuestions(3, "medium", "multiple");
            if(apiResponseMedium == null) return;
            perso = faiDomande(apiResponseMedium, stats);
        }

        if(!perso){
            ApiResponse apiResponseHard = api.fetchQuestions(2, "hard", "multiple");
            if(apiResponseHard == null) return;
            perso = faiDomande(apiResponseHard, stats);
        }


        //4. FINE DOMANDE
        System.out.println("\n\n----------- FINE DOMANDE -----------\n");
        if(stats.correctAnswers == 10)
            System.out.println("HAI VINTO!!");
        else
            System.out.println("Che peccato! Hai perso");


        //5. SALVATAGGIO JSON
        Gson gson = new Gson();
        try(FileWriter fw = new FileWriter(playerName + "_stats.json")){
            gson.toJson(stats, fw);
            System.out.println("Statistiche salvate in " + playerName + "_stats.json");
        } catch (IOException e){
            System.out.println("Errore durante il salvataggio delle statistiche: " + e.getMessage());
        }

    }

    private static boolean faiDomande(ApiResponse apiResponse, PlayerStatistics stats) {
        Scanner sc = new Scanner(System.in);
        int nDomanda = stats.correctAnswers;
        boolean perso = false;

        for(APIQuestion domanda : apiResponse.results){
            nDomanda++;
            System.out.println("\nDomanda " + nDomanda + ", Difficolta " + domanda.difficulty + " - Categoria " + domanda.category);
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
                        stats.correctAnswers = nDomanda-1;
                        System.out.println("Sbagliato! \nLa risposta era: " + domanda.correct_answer);
                    }
                    break;

                case 'H':
                    //IMPLEMENTA AIUTI

                    break;

                default:
                    perso = true;
                    stats.correctAnswers = nDomanda-1;
                    break;
            }

            if(perso)
                return true;
        }

        stats.correctAnswers = nDomanda;
        return perso;
    }
}
