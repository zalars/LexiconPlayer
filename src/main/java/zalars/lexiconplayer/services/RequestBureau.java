package zalars.lexiconplayer.services;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
public class RequestBureau {

    private final HttpClient client;

    public RequestBureau() {
        this.client = HttpClient.newBuilder().build();
    }

    private HttpRequest createRequest(String endPoint) throws URISyntaxException {
        return HttpRequest.newBuilder()
                          .uri(new URI("http://localhost:8081/dictionary/" + endPoint))
                          .GET()
                          .timeout(Duration.ofSeconds(20))
                          .build();
    }

    public String requestSelectionBy(int wordLength) {
        try {
            return this.client.sendAsync(createRequest("" + wordLength),
                            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                                .thenApply(HttpResponse::body)
                                .get();
        } catch (URISyntaxException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
