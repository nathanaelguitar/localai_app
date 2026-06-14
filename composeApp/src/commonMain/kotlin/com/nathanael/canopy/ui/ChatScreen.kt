package com.nathanael.canopy.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nathanael.canopy.model.ChatMessage
import com.nathanael.canopy.model.Role
import com.nathanael.canopy.state.CanopyState

@Composable
fun ChatScreen(state: CanopyState) {
    val conversation = state.selectedConversation
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    if (conversation == null) {
        state.startNewChat()
        return
    }

    LaunchedEffect(conversation.messages.size) {
        if (conversation.messages.isNotEmpty()) listState.animateScrollToItem(conversation.messages.lastIndex)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GlassPanel(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = state::goHome) { Text("Back") }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(conversation.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${state.persona.label} · ${state.selectedWorkspace.title}", color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                TextButton(onClick = state::goSettings) { Text("Settings") }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton(if (conversation.pinned) "Unpin" else "Pin", onClick = { state.togglePin() })
                SecondaryButton("Persona", onClick = state::nextPersona)
                SecondaryButton("Rename", onClick = state::renameSelected)
                SecondaryButton("Clear", onClick = state::clearSelectedChat)
            }
        }

        Spacer(Modifier.height(12.dp))
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(conversation.messages, key = { it.id }) { message ->
                MessageBubble(message)
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            state.workflows.take(2).forEach { workflow ->
                TextButton(onClick = { state.useWorkflow(workflow) }) {
                    Text(workflow.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        GlassPanel(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.composerText,
                onValueChange = { state.composerText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ask Canopy, paste notes, or load a workflow…") },
                minLines = 1,
                maxLines = 5,
                shape = RoundedCornerShape(18.dp)
            )
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Backend: ${state.providerName}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SecondaryButton("Workflows", state::goWorkflows)
                    PremiumButton(
                        "Send",
                        onClick = { state.sendMessage(scope) },
                        enabled = state.composerText.isNotBlank()
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == Role.User
    val transition = rememberInfiniteTransition(label = "loading-alpha")
    val loadingAlpha by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(850), repeatMode = RepeatMode.Reverse),
        label = "loading-alpha"
    )

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = Modifier.widthIn(max = if (isUser) 320.dp else 620.dp),
            shape = RoundedCornerShape(
                topStart = 22.dp,
                topEnd = 22.dp,
                bottomStart = if (isUser) 22.dp else 6.dp,
                bottomEnd = if (isUser) 6.dp else 22.dp
            ),
            color = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.92f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.84f),
            contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    if (isUser) "You" else "Canopy",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f) else MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    message.text,
                    modifier = if (message.isLoading) Modifier.alpha(loadingAlpha) else Modifier,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = if (message.isLoading) FontStyle.Italic else FontStyle.Normal
                )
            }
        }
    }
}
