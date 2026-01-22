package com.example.repository

import com.example.database.DatabaseFactory.dbQuery
import com.example.database.TaskTable
import com.example.model.Priority
import com.example.model.Task
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object TaskRepository {
    // a mapper -> to translate
    private fun resultRowToTask(row: ResultRow) = Task(
        id = row[TaskTable.id],
        name = row[TaskTable.name],
        description = row[TaskTable.description],
        priority = Priority.valueOf(row[TaskTable.priority]),
        due_date = row[TaskTable.due_date],
        status = row[TaskTable.status]
    )

    suspend fun allTasks(): List<Task> = dbQuery {
        TaskTable.selectAll().map(::resultRowToTask)
    }

    suspend fun tasksByPriority(priority: Priority): List<Task> = dbQuery {
        TaskTable.selectAll()
            .where { TaskTable.priority eq priority.name }
            .map(::resultRowToTask)
    }

    suspend fun taskByName(name: String): Task? = dbQuery {
        TaskTable.selectAll()
            .where { TaskTable.name eq name }
            .map(::resultRowToTask)
            .singleOrNull()
    }

    suspend fun addTask(task: Task) = dbQuery {
        TaskTable.insert {
            it[name] = task.name
            it[description] = task.description
            it[priority] = task.priority.name
        }
    }

    suspend fun updateTask(originalName: String, updatedTask: Task): Boolean = dbQuery {
        TaskTable.update({ TaskTable.name eq originalName }) {
            it[name] = updatedTask.name
            it[description] = updatedTask.description
            it[priority] = updatedTask.priority.name
        } > 0
    }

    suspend fun removeTask(name: String): Boolean = dbQuery {
        TaskTable.deleteWhere { TaskTable.name eq name } > 0
    }

}