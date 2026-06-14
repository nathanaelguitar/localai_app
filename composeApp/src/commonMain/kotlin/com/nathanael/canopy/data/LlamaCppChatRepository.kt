package com.nathanael.canopy.data

import com.llamatik.library.platform.LlamaBridge
import com.nathanael.canopy.model.ChatMessage
import com.nathanael.canopy.model.Persona
import com.nathanael.canopy.model.Role
import com.nathanael.canopy.model.Workspace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LlamaCppChatRepository(
    private val modelManager: ModelManager
) : ChatRepository {

    private var isInitialized = false

    suspend fun initialize() {
        if (isInitialized) return
        modelManager.markLoadingIntoMemory()
        withContext(Dispatchers.IO) {
            LlamaBridge.updateGenerateParams(
                temperature = 0.7f,
                maxTokens = 512,
                topP = 0.95f,
                topK = 40,
                repeatPenalty = 1.1f,
                contextLength = 4096,
                numThreads = 4,
                useMmap = true,
                flashAttention = false,
                batchSize = 512,
                gpuLayers = 99
            )
            LlamaBridge.initGenerateModel(modelManager.modelPath())
        }
        isInitialized = true
        modelManager.markReady()
    }

    override suspend fun send(
        history: List<ChatMessage>,
        message: String,
        persona: Persona,
        workspace: Workspace
    ): String {
        if (!isInitialized) {
            initialize()
        }

        val systemPrompt = buildSystemPrompt(persona, workspace)
        val context = buildContext(history)
        val userPrompt = message

        return withContext(Dispatchers.IO) {
            LlamaBridge.generateWithContext(
                systemPrompt = systemPrompt,
                contextBlock = context,
                userPrompt = userPrompt
            )
        }
    }

    private fun buildSystemPrompt(persona: Persona, workspace: Workspace): String {
        return buildString {
            append("You are Canopy, an AI assistant in ${persona.label} mode. ")
            append("${persona.tagline}. ")
            append("Current workspace: ${workspace.title} - ${workspace.subtitle}. ")
            append("Be concise, direct, and professional.")
        }
    }

    private fun buildContext(history: List<ChatMessage>): String {
        val recent = history
            .filter { !it.isLoading }
            .takeLast(6)

        if (recent.isEmpty()) return ""

        return buildString {
            for (msg in recent) {
                when (msg.role) {
                    Role.User -> append("User: ${msg.text}\n")
                    Role.Assistant -> append("Assistant: ${msg.text}\n")
                }
            }
        }.trimEnd()
    }

    fun shutdown() {
        if (isInitialized) {
            LlamaBridge.shutdown()
            isInitialized = false
        }
    }
}
