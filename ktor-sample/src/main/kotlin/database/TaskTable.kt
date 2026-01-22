package com.example.database

import org.jetbrains.exposed.sql.Table

//ORM- Map in Exposed
object  TaskTable : Table("tasks") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val description = varchar("description", 255)
    val priority = varchar("priority", 20)
    val due_date = varchar("due_date", 20).nullable()
    val status = varchar("status", 20).default("OPEN")
    override val primaryKey = PrimaryKey(id)


    init {
        index(false, priority)
    }
}

