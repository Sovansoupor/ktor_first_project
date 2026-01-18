package com.example.database

import liquibase.Liquibase
import liquibase.database.DatabaseFactory as LiquibaseDbFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import java.sql.DriverManager
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {
    fun init(config: ApplicationConfig) {

            val dbUrl = config.property("ktor.database.url").getString()
            val dbUser = config.property("ktor.database.user").getString()
            val dbPassword = config.property("ktor.database.password").getString()
            val dbDriver = "org.postgresql.Driver"

            try {
                runLiquibase(dbUrl, dbUser, dbPassword)
            }catch (e: Exception){
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
                ClassLoaderResourceAccessor(), database)
            liquibase.update("")
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
        // threading: allow doing many things at once.
}