package io.github.hyeonqz

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableAdminServer
@SpringBootApplication
class AdminServerApplication

fun main(args: Array<String>) {
    runApplication<AdminServerApplication>(*args)
}
