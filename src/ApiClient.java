import com.google.gson.Gson;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private final HttpClient client = HttpClient.newHttpClient();

    public void fetchQuestions(int amount, String difficulty, String type){
        //https://opentdb.com/api.php?amount=5&difficulty=easy&type=multiple
        String url = "https://opentdb.com/api.php?amount=" + amount + "&difficulty=" + difficulty + "&type=" + type;

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json") //Sto chiedendo un json in risposta
                .uri(java.net.URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response;
        try{
            response = client.send(request, HttpResponse.BodyHandlers.ofString()); //Usiamo il clint per mandare la richiesta
                                                                                   // body scritto come una stringa
        } catch (IOException | InterruptedException e) {
            System.out.println("Error" + e.getMessage());
            return;
        };

        //Deserializzazione del JSON con Gson
        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(response.body(), ApiResponse.class);

        for(APIQuestion question : apiResponse.results){    //foreach --> stampa per ogni domanda quello che voglio
            System.out.println(question.question);
            System.out.println(question.correct_answer + "\n");
        }
    }
}
