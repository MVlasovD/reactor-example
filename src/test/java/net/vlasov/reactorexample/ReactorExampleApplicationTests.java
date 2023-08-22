package net.vlasov.reactorexample;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

@SpringBootTest
class ReactorExampleApplicationTests {

    MeterRegistry registry = new SimpleMeterRegistry();

    @Test
    void contextLoads() {
    }

    @Test
    void simpleFluxExample() {
        Flux<Integer> fluxColors = Flux.just("red", "green", "blue")
                .map(e -> e.length()).sort().filter(e -> e > 3);
        fluxColors.log().subscribe(System.out::println);
    }

    @Test
    void mapExample() {
        Flux<String> fluxColors = Flux.just("red", "green", "blue");
        fluxColors.map(color -> color.charAt(0)).subscribe(System.out::println);
    }

    @Test
    void zipExample() throws InterruptedException {
        Flux<String> fluxFruits = Flux.just("apple", "pear", "plum");
        Flux<String> fluxColors = Flux.just("red", "green", "blue");
        Flux<Integer> fluxAmounts = Flux.just(10, 20, 30);
        Flux.zip(fluxFruits, fluxColors, fluxAmounts).subscribe(System.out::println);
    }

    @Test
    public void onErrorExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i));

        fluxCalc.subscribe(value -> System.out.println("Next: " + value),
                error -> System.err.println("Error: " + error));
    }

    @Test
    public void onErrorReturnExample() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i))
                .onErrorReturn(ArithmeticException.class, "Division by 0 not allowed");

        fluxCalc.subscribe(value -> System.out.println("Next: " + value),
                error -> System.err.println("Error: " + error));

    }

    @Test
    public void stepVerifierTest() {
        Flux<String> fluxCalc = Flux.just(-1, 0, 1)
                .map(i -> "10 / " + i + " = " + (10 / i)).log();

        StepVerifier.create(fluxCalc)
                .expectNextCount(1)
                .expectError(ArithmeticException.class)
                .verify()
                ;
    }

    @Test
    public void publishSubscribeExample() {
        Scheduler schedulerA = Schedulers.newParallel("Scheduler A");
        Scheduler schedulerB = Schedulers.newParallel("Scheduler B");
        Scheduler schedulerC = Schedulers.newParallel("Scheduler C");

        Flux.just(1)
                .map(i -> {
                    System.out.println("First map: " + Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(schedulerA)
                .map(i -> {
                    System.out.println("Second map: " + Thread.currentThread().getName());
                    return i;
                })
                .publishOn(schedulerB)
                .map(i -> {
                    System.out.println("Third map: " + Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(schedulerC)
                .map(i -> {
                    System.out.println("Fourth map: " + Thread.currentThread().getName());
                    return i;
                })
                .publishOn(schedulerA)
                .map(i -> {
                    System.out.println("Fifth map: " + Thread.currentThread().getName());
                    return i;
                })
                .blockLast();
    }

    @Test
    public void backpressureExample() {
        Flux.range(1, 5)
                .subscribe(new Subscriber<Integer>() {
                    private Subscription s;
                    int counter;

                    @Override
                    public void onSubscribe(Subscription s) {
                        System.out.println("onSubscribe");
                        this.s = s;
                        System.out.println("Requesting 2 emissions");
                        s.request(2);
                    }

                    @Override
                    public void onNext(Integer i) {
                        System.out.println("onNext " + i);
                        counter++;
                        if (counter % 2 == 0) {
                            System.out.println("Requesting 2 emissions");
                            s.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.err.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }

    @Test
    public void coldPublisherExample() throws InterruptedException {
        Flux<Long> intervalFlux = Flux.interval(ofSeconds(1));
        Thread.sleep(2000);
        intervalFlux.subscribe(i -> System.out.println(String.format("Subscriber A, value: %d", i)));
        Thread.sleep(2000);
        intervalFlux.subscribe(i -> System.out.println(String.format("Subscriber B, value: %d", i)));
        Thread.sleep(3000);
    }

    @Test
    public void hotPublisherExample() throws InterruptedException {
        Flux<Long> intervalFlux = Flux.interval(ofSeconds(1));
        ConnectableFlux<Long> intervalCF = intervalFlux.publish();
        intervalCF.connect();
        Thread.sleep(2000);
        intervalCF.subscribe(i -> System.out.println(String.format("Subscriber A, value: %d", i)));
        Thread.sleep(2000);
        intervalCF.subscribe(i -> System.out.println(String.format("Subscriber B, value: %d", i)));
        Thread.sleep(3000);
    }

    @Test
    public void generateTest() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3 * i);
                    if (i == 10) sink.complete();
                    return state;
                }, (state) -> System.out.println("state: " + state));
        flux.subscribe(System.out::println);
    }

    @Test
    public void generateIntTest() {
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3 * state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });
        flux.subscribe(System.out::println);
    }

    @Test
    public void generateInfinityFluxTest() {
        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
                    while (true) {
                        fluxSink.next(System.currentTimeMillis());
                    }
                })
                .log()
                .sample(ofSeconds(2))
                .publish();
        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);
        publish.connect();
    }

    @Test
    public void bufferFiveElementsTest() throws InterruptedException {
        var disposable = Flux.range(1, 20)
                .delayElements(ofMillis(400))
                .buffer(5)
                // collect the items in batches of 5
                .log()
                .doOnNext(l -> System.out.println("Received :: " + l))
                .subscribe();
        Thread.sleep(10000);
    }

    @Test
    public void windowFiveElementsTest() {
        var disposable = Flux.range(1, 10)
                .window(5)
                .doOnNext(flux -> flux.collectList().subscribe(l -> System.out.println("Received :: " + l)))
                .log()
                .subscribe();
    }

    @Test
    public void mapMethodsTest() throws InterruptedException {
        System.out.println("Using flatMap():");
        Flux.range(1, 15)
                .flatMap(item -> Flux.just(item).delayElements(ofMillis(1)))
                .subscribe(x -> System.out.print(x + " "));

        Thread.sleep(100);

        System.out.println("\n\nUsing concatMap():");
        Flux.range(1, 15)
                .concatMap(item -> Flux.just(item).delayElements(ofMillis(1)))
                .subscribe(x -> System.out.print(x + " "));

        Thread.sleep(100);
        System.out.println("\n\nUsing switchMap():");
        Flux.range(1, 17)
                .switchMap(item -> Flux.just(item).delayElements(ofMillis(1)))
                .subscribe(x -> System.out.print(x + " "));

        Thread.sleep(100);
    }

    @Test
    public void parallelMethodTest() {
        Flux.range(1, 10)
                .parallel(2)
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));

        Flux.range(1, 10)
                .parallel(5)
                .runOn(Schedulers.parallel())
                .subscribe(i -> System.out.println("RunOn::" + Thread.currentThread().getName() + " -> " + i));
    }

    @Test
    public void groupByMethodTest() {
        Flux.just(1, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even:" : "odd:")
                .concatMap(Flux::collectList)
                .subscribe(System.out::println);

        Flux.just(1, 10 , 8, 3, 5, 2, 4, 6, 11, 12, 13)
                .groupBy(i -> i % 2 == 0 ? "even:" : "odd:")
                .concatMap(g -> g.defaultIfEmpty(1)
                        // if empty groups, show them
                        .map(String::valueOf)
                        .sort(Comparator.comparing(Integer::valueOf))
                        // map to string
                        .startWith(g.key()))
                // start with the group's key
                .subscribe(System.out::println);
    }

    @Test
    public void simpleFuncTest() {
        Mono.just("Hello")
                .doOnNext(System.out::println)
                .map(w -> w + " Miha")
                .doOnNext(System.out::println)
                .flatMapIterable(w -> Arrays.asList((w.split(""))))
                .doOnNext(System.out::println)
                .subscribe();
    }
}
