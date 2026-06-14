package com.nathanael.canopy.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nathanael.canopy.data.ChatRepository
import com.nathanael.canopy.data.DemoChatRepository
import com.nathanael.canopy.model.ChatMessage
import com.nathanael.canopy.model.Conversation
import com.nathanael.canopy.model.Persona
import com.nathanael.canopy.model.Role
import com.nathanael.canopy.model.Screen
import com.nathanael.canopy.model.Workflow
import com.nathanael.canopy.model.Workspace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CanopyState(
    private val repository: ChatRepository = DemoChatRepository()
) {
    var screen by mutableStateOf<Screen>(Screen.Onboarding)
    var selectedConversationId by mutableStateOf("venture-brief")
    var selectedWorkspaceId by mutableStateOf("ventures")
    var persona by mutableStateOf(Persona.Strategist)
    var composerText by mutableStateOf("")
    var notice by mutableStateOf("")
    var isDark by mutableStateOf(true)
    var providerName by mutableStateOf("Local Demo")
    var endpoint by mutableStateOf("https://your-api.example.com/chat")
    var apiKeyLabel by mutableStateOf("Not connected")
    var onboardingStep by mutableIntStateOf(0)

    val workspaces = listOf(
        Workspace("ventures", "Ventures", "Deals, startups, and messy commercial ideas"),
        Workspace("finance", "Finance Lab", "Backtests, filings, revisions, and market logic"),
        Workspace("career", "Career", "Interview prep, GitHub polish, and role targeting")
    )

    val workflows = listOf(
        Workflow(
            "wf-deal",
            "Buyer Diligence Brief",
            "M&A",
            "Create a buyer diligence brief. Include fit, red flags, financing risk, operator risk, and the next question I should ask."
        ),
        Workflow(
            "wf-backtest",
            "Backtest Sanity Check",
            "Finance",
            "Sanity check this backtest design. Focus on leakage, survivorship bias, split timing, transaction costs, and evaluation metrics."
        ),
        Workflow(
            "wf-interview",
            "Interview Drill",
            "Career",
            "Drill me with SQL, DAX, and analytics case questions. Give hints first, then grade my answer directly."
        ),
        Workflow(
            "wf-builder",
            "Build Plan",
            "Product",
            "Turn this product idea into a build plan with screens, state, data model, API surface, and first-week priorities."
        )
    )

    val conversations = mutableStateListOf<Conversation>()

    init {
        seedConversations()
    }

    val selectedWorkspace: Workspace
        get() = workspaces.firstOrNull { it.id == selectedWorkspaceId } ?: workspaces.first()

    val selectedConversation: Conversation?
        get() = conversations.firstOrNull { it.id == selectedConversationId }

    val filteredConversations: List<Conversation>
        get() = conversations
            .filter { it.workspaceId == selectedWorkspaceId }
            .sortedWith(compareByDescending<Conversation> { it.pinned }.thenBy { it.title })

    fun goHome() {
        screen = Screen.Home
    }

    fun goChat(conversationId: String) {
        selectedConversationId = conversationId
        selectedWorkspaceId = conversations.firstOrNull { it.id == conversationId }?.workspaceId ?: selectedWorkspaceId
        screen = Screen.Chat
    }

    fun goWorkflows() {
        screen = Screen.Workflows
    }

    fun goSettings() {
        screen = Screen.Settings
    }

    fun selectWorkspace(id: String) {
        selectedWorkspaceId = id
        filteredConversations.firstOrNull()?.let { selectedConversationId = it.id }
    }

    fun nextPersona() {
        persona = persona.next()
        showNotice("Persona set to ${persona.label}")
    }

    fun nextOnboardingStep() {
        if (onboardingStep < 2) onboardingStep++ else goHome()
    }

    fun previousOnboardingStep() {
        if (onboardingStep > 0) onboardingStep--
    }

    fun startNewChat(workspaceId: String = selectedWorkspaceId) {
        val count = conversations.count { it.workspaceId == workspaceId } + 1
        val workspace = workspaces.first { it.id == workspaceId }
        val conversation = Conversation(
            id = "chat-${workspaceId}-${count}-${conversations.size}",
            title = "New ${workspace.title} thread",
            preview = "Fresh workspace chat",
            workspaceId = workspaceId,
            pinned = false
        )
        conversation.messages += ChatMessage(
            id = nextId("assistant"),
            role = Role.Assistant,
            text = "Canopy is ready. Pick a workflow, paste context, or ask directly."
        )
        conversations.add(0, conversation)
        selectedWorkspaceId = workspaceId
        selectedConversationId = conversation.id
        composerText = ""
        screen = Screen.Chat
    }

    fun togglePin(conversation: Conversation? = null) {
        val conv = conversation ?: selectedConversation ?: return
        conv.pinned = !conv.pinned
        showNotice(if (conv.pinned) "Pinned" else "Unpinned")
    }

    fun renameSelected() {
        val conversation = selectedConversation ?: return
        conversation.title = smartTitle(conversation.messages.lastOrNull()?.text ?: conversation.title)
        showNotice("Title refreshed")
    }

    fun clearSelectedChat() {
        val conversation = selectedConversation ?: return
        conversation.messages.clear()
        conversation.messages += ChatMessage(
            id = nextId("assistant"),
            role = Role.Assistant,
            text = "Thread cleared. Your backend can replace this local state later."
        )
        conversation.preview = "Thread cleared"
        showNotice("Thread cleared")
    }

    fun useWorkflow(workflow: Workflow) {
        if (selectedConversation == null || screen != Screen.Chat) startNewChat(selectedWorkspaceId)
        composerText = workflow.prompt
        screen = Screen.Chat
        showNotice("Workflow loaded")
    }

    fun testConnection() {
        showNotice("Demo only. Replace DemoChatRepository with your backend client.")
    }

    fun saveSettings() {
        val label = providerName.ifBlank { "Custom Provider" }
        providerName = label
        apiKeyLabel = "Configured locally"
        showNotice("Settings saved locally")
    }

    fun sendMessage(scope: CoroutineScope) {
        val text = composerText.trim()
        if (text.isEmpty()) return

        val conversation = selectedConversation ?: run {
            startNewChat(selectedWorkspaceId)
            selectedConversation ?: return
        }

        val userMessage = ChatMessage(nextId("user"), Role.User, text)
        val loadingMessage = ChatMessage(nextId("assistant"), Role.Assistant, "Thinking under the canopy…", isLoading = true)

        conversation.messages += userMessage
        conversation.messages += loadingMessage
        conversation.preview = text.take(72)
        if (conversation.title.startsWith("New ")) conversation.title = smartTitle(text)
        composerText = ""

        val workspace = workspaces.first { it.id == conversation.workspaceId }
        val currentPersona = persona
        scope.launch {
            val response = repository.send(conversation.messages.toList(), text, currentPersona, workspace)
            val index = conversation.messages.indexOfFirst { it.id == loadingMessage.id }
            if (index >= 0) {
                conversation.messages[index] = ChatMessage(
                    id = loadingMessage.id,
                    role = Role.Assistant,
                    text = response,
                    isLoading = false
                )
            }
            conversation.preview = response.take(72)
        }
    }

    fun showNotice(message: String) {
        notice = message
    }

    fun dismissNotice() {
        notice = ""
    }

    private fun seedConversations() {
        val venture = Conversation(
            id = "venture-brief",
            title = "Operator acquisition notes",
            preview = "Build the buyer screen around operator fit first.",
            workspaceId = "ventures",
            pinned = true
        )
        venture.messages += ChatMessage(
            "m1",
            Role.Assistant,
            "This workspace is for deal thinking. Keep the UI opinionated: operator fit, diligence, structure, next question."
        )
        venture.messages += ChatMessage(
            "m2",
            Role.User,
            "Make the app feel premium and not cookie cutter."
        )
        venture.messages += ChatMessage(
            "m3",
            Role.Assistant,
            "Use a distinct identity, project organization, saved workflows, and a custom oak-inspired background. That gives reviewers something real to inspect."
        )

        val finance = Conversation(
            id = "finance-lab",
            title = "Market reaction harness",
            preview = "Watch leakage, ticker mapping, and event windows.",
            workspaceId = "finance",
            pinned = true
        )
        finance.messages += ChatMessage(
            "m4",
            Role.Assistant,
            "Finance Lab is ready for backtest prompts, model evals, and sanity checks."
        )

        val career = Conversation(
            id = "career-drill",
            title = "SQL and DAX assessment",
            preview = "Practice with hints first, answers second.",
            workspaceId = "career"
        )
        career.messages += ChatMessage(
            "m5",
            Role.Assistant,
            "Career workspace loaded. Use the Interview Drill workflow when you want pressure testing."
        )

        conversations += listOf(venture, finance, career)
    }

    private fun smartTitle(text: String): String {
        val cleaned = text.trim().replace("\n", " ")
        return cleaned.split(" ").filter { it.isNotBlank() }.take(5).joinToString(" ")
            .replaceFirstChar { it.uppercase() }
            .ifBlank { "Untitled Thread" }
    }

    private fun nextId(prefix: String): String = "$prefix-${conversations.sumOf { it.messages.size } + 1}"
}
