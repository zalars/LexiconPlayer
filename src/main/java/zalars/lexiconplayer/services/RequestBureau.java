package zalars.lexiconplayer.services;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@Service
public class RequestBureau {

    private final HttpClient client;

    public RequestBureau() {
        this.client = HttpClient.newBuilder().build();
    }

    private HttpRequest createRequestWith(String endPoint) throws URISyntaxException {
        return HttpRequest.newBuilder()
                          .uri(new URI("http://localhost:8081/dictionary/" + endPoint))
                          .GET()
                          .build();
    }

    public String sendRequestBy(int value) {
        try {
            return this.client.sendAsync(createRequestWith("" + value),
                            HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                                .thenApply(HttpResponse::body)
                                .get();
        } catch (ExecutionException e) {
            return "ERROR:1";
        } catch (URISyntaxException e) {
            return "ERROR:2";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
