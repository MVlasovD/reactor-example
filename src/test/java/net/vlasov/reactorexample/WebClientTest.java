package net.vlasov.reactorexample;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Collections;

public class WebClientTest {

    private final WebClient webClient;

    public WebClientTest(WebClient webClient) {
        this.webClient = webClient;
    }

    public static void main(String[] args) throws InterruptedException {

        WebClient webClient1 = WebClient.create();
        Message flux = webClient1.get()
                .uri("https://localhost:8088/hello")
                .retrieve()
                .bodyToFlux(Message.class)
                .log()
                .blockLast();

        System.out.println(flux);
        Thread.sleep(10000);
        WebClient client1 = WebClient.create();

        WebClient client2 = WebClient.create("http://localhost:8088");

        WebClient client3 = WebClient.builder()
                .baseUrl("http://localhost:8088")
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
                .build();

        WebTestClient testClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:8088")
                .build();

        RouterFunction function = RouterFunctions.route(
                RequestPredicates.GET("/hello"),
                request -> ServerResponse.ok().build()
        );

        WebTestClient
                .bindToRouterFunction(function).build()
                .get().uri("/resource")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:8088")
                .build()
                .post()
                .uri("/resource")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("field").isEqualTo("value");
    }
}
