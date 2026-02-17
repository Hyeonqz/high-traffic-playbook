plugins {
    kotlin("jvm")
}

dependencies {
    testImplementation("io.mockk:mockk:1.13.8")
}

// bootJar 비활성화 (라이브러리 모듈)
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
