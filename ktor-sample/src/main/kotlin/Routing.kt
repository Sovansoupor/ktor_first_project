package com.example

import com.example.routes.taskRoutes // Import task function
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Serve frontend files
        staticResources("/", "static")

        taskRoutes()

    }
}