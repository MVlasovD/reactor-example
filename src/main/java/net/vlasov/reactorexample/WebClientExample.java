package net.vlasov.reactorexample;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.lang.Thread.sleep;

public class WebClientExample {

    public static void main(String[] args) throws InterruptedException {

        String baseUrl = "http://localhost:8088";
        String endpoint = "/hello";
        String url = baseUrl + endpoint;

        WebClient webClient = WebClient.create(baseUrl);

        Mono<String> responseMono = webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class);

        responseMono.subscribe(
                response -> {
                    System.out.println("Ответ от сервера:");
                    System.out.println(response);
                },
                error -> {
                    System.err.println("Произошла ошибка: " + error.getMessage());
                }
        );

        try {
            Thread.sleep(6000); // Подождать 2 секунды
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        String response = responseMono.block(); // Блокирующий вызов
//
//        if (response != null) {
//            System.out.println("Ответ от сервера:");
//            System.out.println(response);
//        } else {
//            System.out.println("Ответ не получен.");
//        }
    }
}