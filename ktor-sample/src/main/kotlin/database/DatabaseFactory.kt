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

        val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/first_project_db"
        val dbUser = System.getenv("DB_USER")
        val dbPassword = System.getenv("DB_PASSWORD")
        val dbDriver = "org.postgresql.Driver"

            try {
                runLiquibase(dbUrl, dbUser, dbPassword)
            }catch (_: Exception){
                println("Database not initialized")
            }

            connect(
                url = dbUrl,
                driver = dbDriver,
                user = dbUser,
                password = dbPassword
            )
    }
    private fun runLiquibase(url: String, user: String, pass: String) {
        val connection = DriverManager.getConnection(url, user, pass)
        connection.use { connection ->
            val database = LiquibaseDbFactory.getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(connection))

            // Point this to your master.xml
            val liquibase = Liquibase(
                "db/master.xml",
                ClassLoaderResourceAccessor(), database
            )
            liquibase.update(Contexts(), LabelExpression())
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
        // threading: allow doing many things at once.
}