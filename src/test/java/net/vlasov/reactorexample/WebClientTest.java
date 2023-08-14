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


    public static void main(String[] args) {

        WebClient client1 = WebClient.create();

        WebClient client2 = WebClient.create("http://localhost:8080");

        WebClient client3 = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .defaultCookie("cookieKey", "cookieValue")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8080"))
                .build();

        WebTestClient testClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:8080")
                .build();

        RouterFunction function = RouterFunctions.route(
                RequestPredicates.GET("/resource"),
                request -> ServerResponse.ok().build()
        );

        WebTestClient
                .bindToRouterFunction(function)
                .build().get().uri("/resource")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:8080")
                .build()
                .post()
                .uri("/resource")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Type", "application/json")
                .expectBody().jsonPath("field").isEqualTo("value");
    }
}
