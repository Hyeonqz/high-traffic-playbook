package io.github.hyeonqz

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FeedSystemApplication

fun main(args: Array<String>) {
    runApplication<FeedSystemApplication>(*args)
}
