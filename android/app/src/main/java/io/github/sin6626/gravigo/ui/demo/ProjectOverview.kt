package io.github.sin6626.gravigo.ui.demo

import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.R

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import io.github.sin6626.gravigo.data.model.ConversationItem
import io.github.sin6626.gravigo.data.model.ProjectItem
import io.github.sin6626.gravigo.ui.chat.ChatUiState
import androidx.compose.foundation.layout.heightIn

@Composable
internal fun ProjectOverview(
    projects: List<ProjectItem>,
    conversations: List<ConversationItem>,
    isLoading: Boolean,
    onOpenConversation: (String) -> Unit,
    expandedProjects: Set<String>,
    onToggleProject: (String) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    error: String? = null,
    onRetry: () -> Unit = {},
    onNewConversation: (ProjectItem) -> Unit = {},
    creationUnavailableReason: String? = null,
    onRetryModels: (() -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Text(appString(R.string.msg_79f326be4409), modifier = Modifier.padding(start = 28.dp, top = 29.dp, bottom = 12.dp),
                color = Ink, fontSize = 27.sp, fontWeight = FontWeight.SemiBold)
        }
        if (error != null) item { LoadFailure(error, isLoading, onRetry) }
        if (projects.isEmpty() && error == null) {
            item {
                Text(if (isLoading) appString(R.string.msg_70f35eb00ee8) else appString(R.string.msg_4550442e55f8),
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
                    color = SecondaryInk, fontSize = 16.sp)
            }
        }
        items(projects.distinctBy { it.navigationKey() }, key = { it.navigationKey() }) { project ->
            val projectKey = project.navigationKey()
            val projectChats = conversations.filter { !it.isSubagent && it.belongsTo(project, projects) }
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().semantics { stateDescription = if (projectKey in expandedProjects) appString(R.string.msg_df647b73b990) else appString(R.string.msg_ee60469308d3) }.quietClickable {
                        onToggleProject(projectKey)
                    }.padding(horizontal = 28.dp, vertical = 17.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Symbol("folder", size = 25)
                    Spacer(Modifier.width(15.dp))
                    ProjectLabel(project, projects, 18, Modifier.weight(1f))
                    Text(if (projectKey in expandedProjects) "−" else "+", color = SecondaryInk,
                        fontSize = 23.sp)
                }
                AnimatedVisibility(projectKey in expandedProjects,
                    enter = expandVertically(tween(200), expandFrom = Alignment.Top) + fadeIn(tween(150)),
                    exit = shrinkVertically(tween(200), shrinkTowards = Alignment.Top) + fadeOut(tween(120))) {
                    Column {
                    ProjectCreateAction(creationUnavailableReason, { onNewConversation(project) },
                        Modifier.padding(start = 68.dp, end = 28.dp), onRetryModels)
                    if (projectChats.isEmpty()) {
                        Text(appString(R.string.msg_a5d93f6f786f), modifier = Modifier.padding(start = 68.dp, bottom = 13.dp),
                            color = SecondaryInk, fontSize = 15.sp)
                    }
                    projectChats.forEach { chat ->
                        Text(chat.displayTitle, modifier = Modifier.fillMaxWidth()
                            .quietClickable { onOpenConversation(chat.id) }
                            .padding(start = 68.dp, end = 28.dp, top = 12.dp, bottom = 12.dp),
                            color = Ink, fontSize = 16.sp, maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                    }
                }
            }
        }
    }
}

internal fun ProjectItem.disambiguatingLocation(projects: List<ProjectItem>): String? =
    if (projects.any { it.name.equals(name, ignoreCase = true) && it.navigationKey() != navigationKey() })
        path.ifBlank { uri }.ifBlank { id }.takeIf { it.isNotBlank() }
    else null

@Composable
internal fun ProjectLabel(project: ProjectItem, projects: List<ProjectItem>, fontSize: Int, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(project.name, color = Ink, fontSize = fontSize.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        project.disambiguatingLocation(projects)?.let { location ->
            Text(location, color = SecondaryInk, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
        }
    }
}

internal fun ChatUiState.projectCreationUnavailableReason(): String? = when {
    creatingProjectKey != null -> appString(R.string.msg_911edfd07239)
    isReverting -> appString(R.string.msg_75c7e4ad6323)
    isSending -> appString(R.string.msg_13b240284c01)
    isLoadingModels -> if (models.isEmpty()) appString(R.string.msg_28dbf18ded56) else appString(R.string.msg_f2e5fc1a30a0)
    models.isEmpty() -> if (modelsError != null) appString(R.string.msg_37faf7dd755a) else appString(R.string.msg_0c4ff1cbb701)
    else -> null
}

@Composable
internal fun ProjectCreateAction(reason: String?, onCreate: () -> Unit, modifier: Modifier,
    onRetryModels: (() -> Unit)? = null, fontSize: Int = 15) {
    Column(modifier) {
        Text(appString(R.string.msg_58e21b8737cc), modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
            .semantics { if (reason != null) stateDescription = reason }
            .quietClickable(enabled = reason == null, onClick = onCreate)
            .padding(vertical = 14.dp),
            color = if (reason == null) SecondaryInk else SecondaryInk.copy(alpha = 0.4f), fontSize = fontSize.sp)
        AnimatedContent(reason, transitionSpec = {
            (fadeIn(tween(120)) togetherWith fadeOut(tween(100))).using(SizeTransform { _, _ -> tween(180) })
        }, label = "project creation feedback") { status ->
            if (status != null) Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(status, color = SecondaryInk, fontSize = 12.sp, modifier = Modifier.weight(1f))
                if (onRetryModels != null) TextButton(onClick = onRetryModels) { Text(appString(R.string.msg_b8784c8dd563), color = AccentBlue) }
            }
        }
    }
}
