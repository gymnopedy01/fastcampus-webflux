모노와 플럭스

- 리액터는 리액티브 스트림의 Publisher 인터페이스를 구현하는 모노(Mono) 와 플럭스(Flux) 라는 두가지 핵심 타입을 제공한다
- 모노는 0..1 개의 단일 요소 스트림을 통지하는 발행자이다
- 플럭스는 0..N 개로 위러진 다수 요소 스트림을 통지하는 발행자이다
- 두타입 모두 리액티브 스트림 데이터 처리 프로토콜대로 onComplete 또는 onError 시그널이 발생할 때 까지 onNext를 사용해 구독자에게 데이터를 통지한다

## Mono.just를 사용한 Hello World 예제

```kotlin
package mono

import reactor.core.publisher.Mono

fun main() {
    val mono: Mono<String> Mon.just("Hello Reactive World")
    mono.subscribe(::println)
}

-------------------
출력결과
-------------------
Hello Reactive World
```

- Mono.just(data: T) 는 객체를 인자로 받은뒤 모노로 래핑하는 팩토리 함수이다

## subscribe()를 호출 하지 않은경우

```kotlin
package mono

import reactor.core.publisher.Mono

fun main() {
    val mono: Mono<String> = Mono.just("Hello Reactive World")
    println(mono)
}
------------------------
출력결과
-------------------------
MonoJust
```

- 모노와 플럭스의 연산자는 모두 Lazy(게으르게) 동작하여 subscribe를 호출하지 않으면 리액티브 스트림 사양대로 코드가 동작하지 않는다
- 즉 subscribe는 Terminal Operator(최종 연산자) 이다
- Java8 의 스트림도 이와 유사하게 map, flatMap, filter 등은 중간 연산자이고 collect, findFirst, count 등이 최종 연산자 이다

## Flux를 사용한 예제

```kotlin
import reactor.core.publisher.Flux

data class CellPhone(
    val name: String,
    val price: Int,
    val currency: Currency,
)

enum class Currency {
    KRW, USD
}

fun main() {
    val iphone = Cellphone(name = "Iphone", price = 100, currency = Currency.KRW)
    val galaxy = Cellphone(name = "Galaxy", price = 90, currency = Currency.KRW)

    val flux: Flux<CellPhone> = Flux.just(iphone, galaxy)
    flux.subscribe(::println)
}

----------------------
출력결과
-----------------
Cellphone(name = Iphone, price = 100, currency = KRW)
Cellphone(name = Galaxy, price = 90, currency = KRW)
```
- Flux는 Mono와 다르게 다수의 요소를 통지할 수 있다

# 2. 스프링 WebFlux와 스프링 MVC 비교
## 2.1 스프링 MVC
- 스프링으로 개발된 대부분의 웹 애플리케이션은 서블릿 기반의 스프링 MVC 이다
- 스프링 MVC는 동시성 처리를 전통적 웹방식인 하나의 스레드가 하나의 요청을 처리하는 Thread per RequestModel 사용한다
- Thread per RequestModel은 DB, Notwork IO 등이 발생할 경우 결과를 받기까지 스레드가 블로킹됨
- 이러한 문제를 해결하기 위해 스레드 풀을 사용해 동시성을 제어한다

## 2.2 스프링 WebFlux
- 스프링 WebFlux는 전통적 웹 프레임워크인 스프링 MVC 와 대비되는 리액티브 기반의 웹스택 프레임워크이다
- 기본적으로 프로젝트 리액터 기반이며 리액티브의 다른 구현체인 RxJava나 코틀린 코루틴으로도 개발이 가능하다
- 스프링 WebFlux 는 비동기- 논 블로킹로 동작하므로 적은수의 스레드로도 대량의 동시성을 제어할 수 있다
- 스프링 MVC와 스프링 WebFlux의 공통점과 각각이 고유하게 지원하는 기능들
- 스프링 MVC
  - 명령형 코드 작성은 코드의 흐름을 쉽게 이해할 수 있고 디버깅하기 쉽다
  - 대부분의 스프링 웹애플리케이션이 스프링 MVC 기반이므로 안정성과 풍부한 라이브러리를 지원
  - JPA, JDBC와 같은 블로킹 API 를 사용하는 경우에는 스프링 MVC 를 사용하는 것이 낫다
  - 스프링 WebFlux
  - 함수형 엔드포인트와 애노테이션 컨트롤러 방식을 모두 지원
  - 이벤트 루프 동시성 모델
  - 스프링 MVC 에 비해 러닝 커브가 많이 높은편
  - 전구간 비동기-논블로킹인 경우에 최적의 성능을 보여준다
  - 
```kotlin
        // 어쩔수 없이 블로킹 API를 쓰는 경우 별도의 스케쥴러로 동작시키는게 좋다
        var blockingWrapper = Mono.fromCallable {
        //JPA의 블로킹 코드
            jpaRepository.findById(id)
        }.subscribeOn(Schedulers.boundedElastic())
```
  - 스프링 MVC 에서도 리액터와 WebFlux 의존성을 추가하여 리액티브 코드와 논블로킹 라이브러리를 사용할 수 있다
<hr/>


함수형 엔드포인트
 람다기반 프로그그램 모델인 함수형엔드포인트를 제공
요청을 분석해서 핸들러로 라우팅 하는 라우터 함수와 
핸들러 함수로 이루워져 있음.
