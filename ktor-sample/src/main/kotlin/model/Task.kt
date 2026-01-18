package com.example.model

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
    val due_date: String? = null
)
