package com.example.model

import ch.qos.logback.core.status.Status
import kotlinx.serialization.Serializable

enum class Priority {
    Low, Medium, High, Vital
}

@Serializable
data class Task(
    val id: Int? = null,
    val name: String,
    val description: String,
    val priority: Priority,
    val due_date: String? = null,
    val status: String = "OPEN"
)
