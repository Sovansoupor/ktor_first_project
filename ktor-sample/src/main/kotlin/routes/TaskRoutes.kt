package com.example.routes

import com.example.model.Priority
import com.example.model.Task
import com.example.repository.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes() {
    route("/tasks") {
        get {
            val tasks = TaskRepository.allTasks()
            call.respond(tasks)
        }

        get("/byPriority/{priority}") {
            val priorityText = call.parameters["priority"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            try {
                val priority = Priority.valueOf(priorityText)
                val tasks = TaskRepository.tasksByPriority(priority)
                call.respond(tasks)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Priority")
            }
        }

        post {
            val task = call.receive<Task>()
            TaskRepository.addTask(task)
            call.respond(HttpStatusCode.Created)
        }

        put("/{taskName}") {
            val name = call.parameters["taskName"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val updatedTask = call.receive<Task>()
            if (TaskRepository.updateTask(name, updatedTask)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete("/{taskName}") {
            val name = call.parameters["taskName"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (TaskRepository.removeTask(name)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}