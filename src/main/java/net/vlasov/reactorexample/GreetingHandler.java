package net.vlasov.reactorexample;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

import static reactor.core.publisher.Flux.just;

@Component
public class GreetingHandler {



    public Mono<ServerResponse> hello(ServerRequest request) {
        Long start = request.queryParam("start")
                        .map(Long::valueOf)
                        .orElse(0L);
        Long count = request.queryParam("count")
                .map(Long::valueOf)
                .orElse(5L);

        Flux<Message> data = just(
                        "Hello, reactive!",
                        "Fuck",
                        "you",
                        "Dude",
                        "HA HA HA!"
                )
                .skip(start)
                .take(count)
                .map(Message::new)
                .delayElements(Duration.ofMillis(1000));

        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(data, Message.class);
    }

    public Mono<ServerResponse> index(ServerRequest serverRequest) {
        String user = serverRequest.queryParam("user")
                .orElse("Nobody");

        return ServerResponse
                .ok()
                .render("index", Map.of("user", user));
    }

    public Mono<ServerResponse> poo(ServerRequest serverRequest) {
        Flux<String> f =
                just("String ", "Pancake ")
                        .map(String::new)
                        .delayElements(Duration.ofSeconds(1))
                ;
        return ServerResponse
                .ok()
                .body(f, String.class);
    }
}
