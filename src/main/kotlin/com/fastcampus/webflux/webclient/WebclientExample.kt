package com.fastcampus.webflux.webclient

import com.fastcampus.webflux.book.Book
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@RestController
class WebclientExample {

    val url = "http://localhost:8080/books"

    val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/books/block")
    fun getBooksBlockingWay(): List<Book> {
        //resttemmplate문제
        //요청을 보낸 서버로 부터 응답을 받을때까지 쓰레드가 블로킹 되기 때문에
        //해당 쓰레드는다른일을하지 못하게됨. exchange 를 호출시 그 시간동안 요청쓰레드가 블록킹됨.
        //다른 일을 못함

        //병렬적으로 동작할수있도록 compleatablefuture와 같은 논 블록킹 api를 사용하면 되지만 . 불편하기 때문에 webclient를 사용하여 해결할수있음.
        log.info("Start RestTemplate")
        val restTemplate = RestTemplate()
        val response = restTemplate.exchange(url, HttpMethod.GET, null,
            object : ParameterizedTypeReference<List<Book>>() {}
        )

        val result = response.body!!
        log.info("result: {}", result)
        log.info("Finish RestTemplate")

        return result

    }

    //WebClient 란?
    //스프링에서 제공하는 reactive 기반의 논블록킹 http client
    //spring5 부터는 RestTemplate 를 대체하며. 블록킹, 논블록킹 방식 둘다 사용가능합니다
    //WebClient 를 사용하게 되면 쓰레드가 별도의 응답을 기다릴 필요없이 처리할수 있기 때문에. RestTemplate 보다 부하를 줄일수있다.
    //즉 컨텍스트 스위칭이 줄어들기 때문에 부하를 줄일 수 도있고 . 더적은 메모리로 처리할수 있게 되구요
    //또한 여러 서버의 응답을 받아 처리하는 경우에는 동시에 여러 서버를 호출하기 때문에 빠르게 처리가 가능하다는 장점이있음
    @GetMapping("/books/nonblock")
    fun getBooksNonBlockingWay(): Flux<Book> {
        log.info("Start WebClient")

        val flux = WebClient.create()
            .get()
            .uri(url)
            .retrieve()
            .bodyToFlux(Book::class.java)
            .map {
                log.info("result: {}", it)
                it
            }

        log.info("Finish WebClient")
        log.info("Finish WebClient {}", flux)

        return flux

    }
}