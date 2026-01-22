plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.liquibase)
}

group = "com.example"
version = "0.0.1"


application {
    mainClass = "io.ktor.server.cio.EngineMain"
}


dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.cio)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite.jdbc)
    implementation(libs.liquibase.core)
    implementation(libs.postgresql)

    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.sqlite.jdbc)
    liquibaseRuntime(libs.postgresql)
    liquibaseRuntime("org.yaml:snakeyaml:2.2")

    implementation(libs.dotenv.kotlin)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")

}

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changelogFile" to "src/main/resources/db/master.xml",
            "url" to (System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/first_project_db"),
            "username" to (System.getenv("DB_USER") ?: "postgres"),
            "password" to (System.getenv("DB_PASSWORD") ?: "password"),
            "driver" to "org.postgresql.Driver"
        )
    }
}