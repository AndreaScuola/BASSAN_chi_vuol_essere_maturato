import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        //1. MENU PRINCIPALE
        System.out.println("----------- CHI VUOLE ESSERE MATURATO -----------\n");
        System.out.println("REGOLE: \nDovrai rispondere alle domande e avrai a disposizione degli aiuti: \n-'HB': 50/50 \n-'HP': Aiuto dal pubblico\nOppure puoi arrenderti con 'R'\n\n");

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
        stampaJson(stats);
    }

    private static boolean faiDomande(ApiResponse apiResponse, PlayerStatistics stats) {
        Scanner sc = new Scanner(System.in);
        int nDomanda = stats.correctAnswers;
        boolean perso = false;
        boolean risposto;

        for(APIQuestion domanda : apiResponse.results){
            nDomanda++;
            risposto = false;
            List<AnswerOption> options = domanda.getShuffledAnswers();

            while(!risposto){
                System.out.println("\nDomanda " + nDomanda + ", Difficolta " + domanda.difficulty + " - Categoria " + domanda.category);
                System.out.println(domanda.question + "\n");

                System.out.println("A. " + (options.get(0) == null ? "---" : options.get(0)) +
                        "\t\tB. " + (options.get(1) == null ? "---" : options.get(1)) +
                        "\nC. " + (options.get(2) == null ? "---" : options.get(2)) +
                        "\t\tD. " + (options.get(3) == null ? "---" : options.get(3)));

                System.out.print("\nInserisci la risposta: ");
                String response = sc.next();

                //Controllo la risposta / HB e HP / R
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
                        if(response.charAt(1) == 'B')
                            if(stats.used5050){
                                System.out.println("Il 50/50 e' gia' stato usato");
                                continue;
                            }
                            else{
                                stats.used5050 = true;
                                usa5050(options);
                                continue;
                            }

                        else if(response.charAt(1) == 'P')
                            if(stats.usedAudiance){
                                System.out.println("L'aiuto dal pubblico e' gia' stato usato");
                                continue;
                            }
                            else{
                                stats.usedAudiance = true;
                                usaAiutoPubblico(options);
                                continue;
                            }

                        break;

                    default:
                        perso = true;
                        stats.correctAnswers = nDomanda-1;
                        break;
                }

                risposto = true;
            }

            if(perso)
                return true;
        }

        stats.correctAnswers = nDomanda;
        return perso;
    }

    private static void usa5050(List<AnswerOption> options) {
        int removed = 0;
        int index;

        while(removed < 2){
            index = (int)(Math.random() * options.size());
            if(options.get(index) != null && !options.get(index).isCorrect()){
                removed++;
                options.set(index, null);
            }
        }
    }

    private static void usaAiutoPubblico(List<AnswerOption> options) {
        System.out.println("Il pubblico vota:");
        for (AnswerOption opt : options) {
            if (opt == null) continue;
            int percentuale = opt.isCorrect() ? 60 + (int)(Math.random() * 20) : (int)(Math.random() * 20);
            System.out.println(opt + " -> " + percentuale + "%");
        }
    }

    private static void stampaJson(PlayerStatistics stats) {
        Gson gson = new Gson();
        List<PlayerStatistics> allStats = new ArrayList<>();

        //Leggo il file se esiste
        File file = new File("stats.json");
        if (file.exists()) {
            try (FileReader fr = new FileReader(file)) {
                PlayerStatistics[] existing = gson.fromJson(fr, PlayerStatistics[].class);
                if (existing != null)
                    allStats.addAll(Arrays.asList(existing));
            } catch (IOException e) {
                System.out.println("Errore durante la lettura del file esistente: " + e.getMessage());
            }
        }

        //Aggiungo le nuove statistiche
        allStats.add(stats);

        //Sovrascrivo il file con la lista aggiornata
        try (FileWriter fw = new FileWriter(file)) {
            gson.toJson(allStats, fw);
            System.out.println("Statistiche aggiornate in 'stats.json'");
        } catch (IOException e) {
            System.out.println("Errore durante il salvataggio delle statistiche: " + e.getMessage());
        }
    }

}
