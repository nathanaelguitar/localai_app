package com.nathanael.canopy.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nathanael.canopy.model.Screen
import com.nathanael.canopy.model.Workflow
import com.nathanael.canopy.state.CanopyState

@Composable
fun WorkflowsScreen(state: CanopyState) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("Saved workflows", "Reusable prompts that make the product feel purpose-built.")
            PremiumButton("New chat", onClick = { state.startNewChat() })
        }
        Spacer(Modifier.height(14.dp))
        LazyColumn(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.workflows, key = { it.id }) { workflow ->
                WorkflowCard(workflow = workflow, onUse = { state.useWorkflow(workflow) })
            }
        }
        AppBottomBar(
            current = Screen.Workflows,
            onHome = state::goHome,
            onWorkflows = state::goWorkflows,
            onSettings = state::goSettings
        )
    }
}

@Composable
private fun WorkflowCard(workflow: Workflow, onUse: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(workflow.category, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(workflow.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(workflow.prompt, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
            Spacer(Modifier.height(12.dp))
            PremiumButton("Use workflow", onClick = onUse, modifier = Modifier.fillMaxWidth())
        }
    }
}
