package com.nathanael.canopy.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class Screen {
    data object Onboarding : Screen()
    data object ModelDownload : Screen()
    data object Home : Screen()
    data object Chat : Screen()
    data object Workflows : Screen()
    data object Settings : Screen()
}

enum class Role { User, Assistant }

enum class Persona(val label: String, val tagline: String) {
    Strategist("Strategist", "Sharp planning, tradeoffs, and next steps"),
    Builder("Builder", "Turns ideas into implementation plans"),
    Analyst("Analyst", "Numbers, assumptions, and risk checks"),
    Editor("Editor", "Tightens language without killing your voice");

    fun next(): Persona {
        val all = entries
        return all[(ordinal + 1) % all.size]
    }
}

data class Workspace(
    val id: String,
    val title: String,
    val subtitle: String
)

data class ChatMessage(
    val id: String,
    val role: Role,
    val text: String,
    val isLoading: Boolean = false
)

@Stable
class Conversation(
    val id: String,
    title: String,
    preview: String,
    val workspaceId: String,
    pinned: Boolean = false,
    val createdLabel: String = "Today"
) {
    var title by mutableStateOf(title)
    var preview by mutableStateOf(preview)
    var pinned by mutableStateOf(pinned)
    val messages = mutableStateListOf<ChatMessage>()
}

data class Workflow(
    val id: String,
    val title: String,
    val category: String,
    val prompt: String
)
