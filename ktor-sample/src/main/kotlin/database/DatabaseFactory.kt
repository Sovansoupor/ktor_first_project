package com.example.database

import liquibase.Liquibase
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.database.DatabaseFactory as LiquibaseDbFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.sql.DriverManager

object DatabaseFactory {

    fun init() {
        val dbUrl = "jdbc:postgresql://${System.getenv("DB_HOST")}:${System.getenv("DB_PORT")}/${System.getenv("DB_NAME")}?sslmode=disable" ?: error("DATABASE_URL is not set")
        val dbUser = System.getenv("DB_USER") ?: error("DB_USER is not set")
        val dbPassword = System.getenv("DB_PASSWORD") ?: error("DB_PASSWORD is not set")
        val dbDriver = "org.postgresql.Driver"

        // 1 Run Liquibase FIRST
        runLiquibase(dbUrl, dbUser, dbPassword)

        // 2 Connect Exposed AFTER migrations
        Database.connect(
            url = dbUrl,
            driver = dbDriver,
            user = dbUser,
            password = dbPassword
        )

        println("Database connected on Render")
    }

    private fun runLiquibase(url: String, user: String, pass: String) {
        DriverManager.getConnection(url, user, pass).use { connection ->
            val database = LiquibaseDbFactory.getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(connection))

            database.defaultSchemaName = "public"

            val liquibase = Liquibase(
                "db/master.xml",
                ClassLoaderResourceAccessor(),
                database
            )

            liquibase.update(Contexts(), LabelExpression())
            println("Liquibase migrations applied")
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
