package com.nathanael.canopy.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nathanael.canopy.model.Conversation
import com.nathanael.canopy.model.Screen
import com.nathanael.canopy.state.CanopyState

@Composable
fun HomeScreen(state: CanopyState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BrandMark()
                Column(Modifier.padding(start = 12.dp)) {
                    Text("Canopy", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Premium AI workspace", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            PremiumButton("New", onClick = { state.startNewChat() })
        }

        Spacer(Modifier.height(16.dp))
        GlassPanel(Modifier.fillMaxWidth()) {
            Text("Oak-rooted workspace", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Threads are organized by the work you actually do: ventures, finance, and career. The UI is distinct enough to avoid looking like a thin ChatGPT wrapper.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            WorkspaceChips(state.workspaces, state.selectedWorkspaceId, state::selectWorkspace)
        }

        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("Threads", state.selectedWorkspace.subtitle)
            TextButton(onClick = state::goWorkflows) { Text("Workflows") }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.filteredConversations, key = { it.id }) { conversation ->
                ConversationRow(
                    conversation = conversation,
                    onOpen = { state.goChat(conversation.id) },
                    onPin = { state.togglePin(conversation) }
                )
            }
            item {
                SecondaryButton(
                    "Start empty thread",
                    onClick = { state.startNewChat() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        AppBottomBar(
            current = Screen.Home,
            onHome = state::goHome,
            onWorkflows = state::goWorkflows,
            onSettings = state::goSettings
        )
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onOpen: () -> Unit,
    onPin: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        tonalElevation = 0.dp
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            if (conversation.pinned) "Pinned · ${conversation.title}" else conversation.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    Text(
                        conversation.preview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            TextButton(onClick = onPin) { Text(if (conversation.pinned) "Unpin" else "Pin") }
        }
    }
}
