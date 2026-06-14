package com.nathanael.canopy.data

import com.nathanael.canopy.db.CanopyDatabase
import com.nathanael.canopy.model.ChatMessage
import com.nathanael.canopy.model.Conversation
import com.nathanael.canopy.model.Role

class DatabaseRepository(private val database: CanopyDatabase) {

    // --- Conversations ---

    fun loadAllConversations(): List<Conversation> {
        return database.conversationQueries.selectAll().executeAsList().map { entity ->
            val conversation = Conversation(
                id = entity.id,
                title = entity.title,
                preview = entity.preview,
                workspaceId = entity.workspace_id,
                pinned = entity.pinned
            )
            val messages = database.chatMessageQueries
                .selectByConversation(entity.id)
                .executeAsList()
                .map { msg ->
                    ChatMessage(
                        id = msg.id,
                        role = Role.valueOf(msg.role),
                        text = msg.text
                    )
                }
            conversation.messages.addAll(messages)
            conversation
        }
    }

    fun saveConversation(conversation: Conversation) {
        val now = currentTimeMillis()
        database.conversationQueries.insert(
            id = conversation.id,
            title = conversation.title,
            preview = conversation.preview,
            workspace_id = conversation.workspaceId,
            pinned = conversation.pinned,
            created_at = now,
            updated_at = now
        )
    }

    fun updateConversationTitle(id: String, title: String) {
        database.conversationQueries.updateTitle(title, currentTimeMillis(), id)
    }

    fun updateConversationPreview(id: String, preview: String) {
        database.conversationQueries.updatePreview(preview, currentTimeMillis(), id)
    }

    fun updateConversationPinned(id: String, pinned: Boolean) {
        database.conversationQueries.updatePinned(pinned, currentTimeMillis(), id)
    }

    fun deleteConversation(id: String) {
        database.chatMessageQueries.deleteByConversation(id)
        database.conversationQueries.deleteById(id)
    }

    // --- Messages ---

    fun saveMessage(conversationId: String, message: ChatMessage) {
        val now = currentTimeMillis()
        val nextOrder = database.chatMessageQueries
            .maxSortOrder(conversationId)
            .executeAsOne()
            .let { it + 1 }
        database.chatMessageQueries.insert(
            id = message.id,
            conversation_id = conversationId,
            role = message.role.name,
            text = message.text,
            created_at = now,
            sort_order = nextOrder
        )
    }

    fun clearMessages(conversationId: String) {
        database.chatMessageQueries.deleteByConversation(conversationId)
    }

    // --- Settings ---

    fun getSetting(key: String): String? {
        return database.settingsQueries.get(key).executeAsOneOrNull()
    }

    fun setSetting(key: String, value: String) {
        database.settingsQueries.set(key, value)
    }

    fun hasCompletedOnboarding(): Boolean {
        return getSetting("onboarding_completed") == "true"
    }

    fun setOnboardingCompleted() {
        setSetting("onboarding_completed", "true")
    }

    fun isModelDownloaded(): Boolean {
        return getSetting("model_downloaded") == "true"
    }

    fun setModelDownloaded(downloaded: Boolean) {
        setSetting("model_downloaded", if (downloaded) "true" else "false")
    }

    fun getModelPath(): String? {
        return getSetting("model_path")
    }

    fun setModelPath(path: String) {
        setSetting("model_path", path)
    }

    private fun currentTimeMillis(): Long {
        return kotlin.time.Clock.System.now().toEpochMilliseconds()
    }
}
