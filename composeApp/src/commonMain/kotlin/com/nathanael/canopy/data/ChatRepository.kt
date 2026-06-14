package com.nathanael.canopy.data

import com.nathanael.canopy.model.ChatMessage
import com.nathanael.canopy.model.Persona
import com.nathanael.canopy.model.Workspace
import kotlinx.coroutines.delay

interface ChatRepository {
    suspend fun send(
        history: List<ChatMessage>,
        message: String,
        persona: Persona,
        workspace: Workspace
    ): String
}

class DemoChatRepository : ChatRepository {
    override suspend fun send(
        history: List<ChatMessage>,
        message: String,
        persona: Persona,
        workspace: Workspace
    ): String {
        delay(650)
        val trimmed = message.trim().replace("\n", " ")
        return buildString {
            append("${persona.label} mode for ${workspace.title}. ")
            append("I would treat this as a local draft until your backend is wired. ")
            append("For: \"")
            append(trimmed.take(90))
            if (trimmed.length > 90) append("…")
            append("\". ")
            append("Next useful action: turn it into a concrete workflow, pick the data needed, then push the request through your real model provider.")
        }
    }
}
