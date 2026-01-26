package com.example.database

import liquibase.Liquibase
import liquibase.database.DatabaseFactory as LiquibaseDbFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.DriverManager
import kotlinx.coroutines.Dispatchers
import liquibase.Contexts
import liquibase.LabelExpression
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {
    fun init() {
        val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/first_project_db?sslmode=require"
        val dbUser = System.getenv("DB_USER") ?: "sovansoupor"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "password"
        val dbDriver = "org.postgresql.Driver"

        // Wait until DB is ready
        waitForDatabase(dbUrl, dbUser, dbPassword)

        // Run Liquibase migrations
        runLiquibase(dbUrl, dbUser, dbPassword)

        // Connect Exposed
        connect(
            url = dbUrl,
            driver = dbDriver,
            user = dbUser,
            password = dbPassword
        )
    }

    private fun waitForDatabase(url: String, user: String?, pass: String?) {
        var retries = 10
        while (retries > 0) {
            try {
                DriverManager.getConnection(url, user, pass).use { return }
            } catch (e: Exception) {
                println("Waiting for database... (${retries} retries left)")
                Thread.sleep(3000)
                retries--
            }
        }
        throw Exception("Database not available after retries")
    }

    private fun runLiquibase(url: String, user: String?, pass: String?) {
        try {
            DriverManager.getConnection(url, user, pass).use { connection ->
                val database = LiquibaseDbFactory.getInstance()
                    .findCorrectDatabaseImplementation(JdbcConnection(connection))

                val liquibase = Liquibase(
                    "db/master.xml",        
                    ClassLoaderResourceAccessor(),
                    database
                )
                liquibase.update(Contexts(), LabelExpression())
                println("Liquibase migrations applied successfully")
            }
        } catch (e: Exception) {
            println("Failed to run Liquibase: ${e.message}")
            throw e
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
        // threading: allow doing many things at once.
}