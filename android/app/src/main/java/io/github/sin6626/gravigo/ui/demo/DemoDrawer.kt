package io.github.sin6626.gravigo.ui.demo

import io.github.sin6626.gravigo.i18n.appString

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import io.github.sin6626.gravigo.data.model.AccountProfile
import io.github.sin6626.gravigo.data.model.ConversationItem
import io.github.sin6626.gravigo.data.model.ProjectItem
import androidx.compose.ui.res.stringResource
import io.github.sin6626.gravigo.R

@Composable
internal fun DemoDrawer(
    modifier: Modifier,
    conversations: List<ConversationItem>,
    projects: List<ProjectItem>,
    isLoading: Boolean,
    isLoadingProjects: Boolean,
    onOpenConversation: (String) -> Unit,
    onOpenProjectConversation: (String) -> Unit,
    onNewChat: () -> Unit,
    onPairing: () -> Unit,
    onArchived: () -> Unit,
    selectedConversationId: String? = null,
    conversationsError: String? = null, projectsError: String? = null,
    onRetryConversations: () -> Unit = {}, onRetryProjects: () -> Unit = {},
    busy: Set<String> = emptySet(),
    onRename: (ConversationItem) -> Unit = {}, onPin: (ConversationItem) -> Unit = {},
    onArchive: (ConversationItem) -> Unit = {}, onDelete: (ConversationItem) -> Unit = {},
    onNewProjectConversation: (ProjectItem) -> Unit = {},
    creationUnavailableReason: String? = null,
    onRetryModels: (() -> Unit)? = null,
    onDesktopStatus: () -> Unit = {},
    onSettings: () -> Unit = {},
    accountProfile: AccountProfile? = null,
) {
    var searchOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var projectsOpen by remember { mutableStateOf(false) }
    var expandedProjects by remember { mutableStateOf(setOf<String>()) }
    Column(modifier = modifier.background(Color.White).padding(start = 28.dp, end = 22.dp)) {
        val density = LocalDensity.current
        var headerHeight by remember { mutableStateOf(105.dp) }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = headerHeight)) {
                DrawerMenuItem("chat_bubble", appString(R.string.msg_4b3510b8d86e), onNewChat)
                DrawerMenuItem("archive", appString(R.string.msg_82395d423099), onArchived)
                DrawerMenuItem("folder", appString(R.string.msg_79f326be4409)) { projectsOpen = !projectsOpen }
                DrawerMenuItem("computer", appString(R.string.msg_f7af68a7c161), onDesktopStatus)
                projectsError?.let { LoadFailure(it, isLoadingProjects, onRetryProjects) }
                conversationsError?.let { LoadFailure(it, isLoading, onRetryConversations) }
                if (projectsOpen || searchText.isNotBlank()) {
                    val visibleProjects = projects.filter { project ->
                        searchText.isBlank() || project.name.contains(searchText, ignoreCase = true) ||
                            conversations.any { it.belongsTo(project, projects) &&
                                it.displayTitle.contains(searchText, ignoreCase = true) }
                    }
                    if (isLoadingProjects && visibleProjects.isEmpty()) {
                        Text(appString(R.string.msg_70f35eb00ee8), modifier = Modifier.padding(start = 45.dp, top = 8.dp),
                            color = SecondaryInk, fontSize = 15.sp)
                    } else if (visibleProjects.isEmpty()) {
                        Text(if (searchText.isBlank()) appString(R.string.msg_4550442e55f8) else appString(R.string.msg_3cc529ee7e03), modifier = Modifier.padding(start = 45.dp, top = 8.dp),
                            color = SecondaryInk, fontSize = 15.sp)
                    }
                    visibleProjects.distinctBy { it.navigationKey() }.forEach { project ->
                        val projectKey = project.navigationKey()
                        val projectChats = conversations.filter { it.belongsTo(project, projects) && !it.isSubagent }
                        DrawerProject(project, projects, expandedProjects.contains(projectKey)) {
                            expandedProjects = if (projectKey in expandedProjects)
                                expandedProjects - projectKey else expandedProjects + projectKey
                        }
                        AnimatedVisibility(projectKey in expandedProjects || searchText.isNotBlank() &&
                            projectChats.any { it.displayTitle.contains(searchText, ignoreCase = true) },
                            enter = expandVertically(tween(200), expandFrom = Alignment.Top) + fadeIn(tween(150)),
                            exit = shrinkVertically(tween(200), shrinkTowards = Alignment.Top) + fadeOut(tween(120))) {
                            Column {
                            ProjectCreateAction(creationUnavailableReason, { onNewProjectConversation(project) },
                                Modifier.padding(start = 45.dp), onRetryModels, fontSize = 14)
                            val visibleChats = if (searchText.isBlank() || project.name.contains(searchText, ignoreCase = true))
                                projectChats else projectChats.filter { it.displayTitle.contains(searchText, ignoreCase = true) }
                            if (visibleChats.isEmpty()) {
                                Text(appString(R.string.msg_a5d93f6f786f), modifier = Modifier.padding(start = 45.dp, top = 8.dp, bottom = 8.dp),
                                    color = SecondaryInk, fontSize = 14.sp)
                            }
                            visibleChats.forEach { item ->
                                DrawerConversation(item, indent = 45.dp, current = item.id == selectedConversationId,
                                    enabled = item.id !in busy, onRename = onRename, onPin = onPin, onArchive = onArchive, onDelete = onDelete) {
                                    onOpenProjectConversation(item.id)
                                }
                            }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(27.dp))
                val pinned = conversations.filter { it.isPinned && it.displayTitle.contains(searchText, true) }
                if (pinned.isNotEmpty()) {
                    DrawerSection(appString(R.string.msg_173f88d28ebf))
                    pinned.forEach { item -> DrawerConversation(item, current = item.id == selectedConversationId,
                        enabled = item.id !in busy, onRename = onRename, onPin = onPin, onArchive = onArchive, onDelete = onDelete) {
                        if (item.isPureChat) onOpenConversation(item.id) else onOpenProjectConversation(item.id)
                    } }
                    Spacer(Modifier.height(20.dp))
                }
                DrawerSection(appString(R.string.msg_997a5e6e513f))
                val filtered = conversations.filter { it.isPureChat && !it.isSubagent && !it.isPinned &&
                    it.displayTitle.contains(searchText, ignoreCase = true) }
                if (isLoading && filtered.isEmpty()) {
                    Text(appString(R.string.msg_ea9e465d5905), color = SecondaryInk, fontSize = 16.sp)
                } else if (filtered.isEmpty()) {
                    if (conversationsError == null) Text(if (searchText.isBlank()) appString(R.string.msg_779820e388fd) else appString(R.string.msg_e1c43a679b91), color = SecondaryInk, fontSize = 16.sp)
                }
                filtered.forEach { item ->
                    DrawerConversation(item, current = item.id == selectedConversationId,
                        enabled = item.id !in busy, onRename = onRename, onPin = onPin, onArchive = onArchive, onDelete = onDelete) { onOpenConversation(item.id) }
                }
            }
            GradientTopBar(headerHeight) {
                Column(Modifier.onSizeChanged { headerHeight = with(density) { it.height.toDp() } }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 33.dp, bottom = 22.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(R.string.app_name), modifier = Modifier.weight(1f), color = Ink, fontSize = 27.sp,
                            fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        RoundIconButton("search", appString(R.string.msg_44ce7ae909bb), { searchOpen = !searchOpen })
                    }
                    if (searchOpen) {
                        BasicTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            singleLine = true,
                            decorationBox = { inner ->
                                Row(Modifier.background(Color(0xFFF3F3F3), RoundedCornerShape(20.dp))
                                    .heightIn(min = 48.dp).padding(start = 16.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.weight(1f).padding(vertical = 12.dp)) {
                                        if (searchText.isEmpty()) Text(appString(R.string.msg_feb95554de09), color = SecondaryInk, fontSize = 16.sp)
                                        inner()
                                    }
                                    if (searchText.isNotEmpty()) IconButton(onClick = { searchText = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = appString(R.string.msg_ee32f25f7050), tint = SecondaryInk, modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                        )
                    }

                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 25.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.background(AccentBlue, RoundedCornerShape(28.dp))
                    .quietClickable(RoundedCornerShape(28.dp), onClick = onPairing)
                    .padding(horizontal = 18.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Symbol("computer", size = 24, color = Color.White)
                Spacer(Modifier.width(10.dp))
                Text(appString(R.string.msg_7c5cc4ddb1f9), color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Medium)
            }
            Box(Modifier.size(48.dp).semantics { contentDescription = appString(R.string.msg_4f87a1cc8dd3) }
                .quietClickable(CircleShape, onClick = onSettings), contentAlignment = Alignment.Center) {
                AccountAvatar(accountProfile, 36.dp)
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(icon: String, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp).quietClickable(RoundedCornerShape(14.dp),
            pressedColor = Color(0xFFF1F1F1), onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Symbol(icon, size = 24)
        Spacer(Modifier.width(18.dp))
        Text(title, color = Ink, fontSize = 17.sp)
    }
}

@Composable
private fun DrawerSection(title: String) {
    Text(title, modifier = Modifier.padding(bottom = 12.dp), color = Ink, fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold)
}

@Composable
private fun DrawerProject(project: ProjectItem, projects: List<ProjectItem>, expanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 51.dp).semantics { stateDescription = if (expanded) appString(R.string.msg_df647b73b990) else appString(R.string.msg_ee60469308d3) }.quietClickable(RoundedCornerShape(14.dp), pressedColor = Color(0xFFF1F1F1), onClick = onClick)
            .padding(start = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Symbol("folder", size = 21, color = SecondaryInk)
        Spacer(Modifier.width(11.dp))
        ProjectLabel(project, projects, 16, Modifier.weight(1f))
        Text(if (expanded) "−" else "+", color = SecondaryInk, fontSize = 20.sp)
    }
}

@Composable
private fun DrawerConversation(item: ConversationItem, indent: androidx.compose.ui.unit.Dp = 0.dp,
    current: Boolean, enabled: Boolean, onRename: (ConversationItem) -> Unit, onPin: (ConversationItem) -> Unit,
    onArchive: (ConversationItem) -> Unit, onDelete: (ConversationItem) -> Unit, onClick: () -> Unit) {
    key(item.id) {
        var menuOpen by remember { mutableStateOf(false) }
        Box(Modifier.fillMaxWidth()) {
            Box(Modifier.fillMaxWidth().heightIn(min = 48.dp).semantics { selected = current }
                .quietClickable(RoundedCornerShape(14.dp), pressedColor = Color(0xFFF1F1F1),
                    highlighted = current || menuOpen,
                    onLongClick = { menuOpen = true }, onClick = onClick)
                .padding(start = indent + 12.dp, end = 12.dp, top = 12.dp, bottom = 12.dp), contentAlignment = Alignment.CenterStart) {
                Text(item.displayTitle, color = Ink, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            ConversationActionsMenu(menuOpen, { menuOpen = false }, item.displayTitle, item.isPinned, item.isArchived,
                enabled && !item.id.startsWith("local:"), { menuOpen = false; onRename(item) },
                { menuOpen = false; onPin(item) }, { menuOpen = false; onArchive(item) },
                { menuOpen = false; onDelete(item) }, showTitle = false)
        }
    }
}

// ponytail: rows without ID/URI/path use their name; identical names need stable backend IDs to distinguish them.
internal fun ProjectItem.navigationKey(): String =
    listOf(id, uri.ifBlank { path }.trimEnd('/')).takeIf { it.any(String::isNotBlank) }?.joinToString("\u0000") ?: "name:$name"

internal fun ConversationItem.belongsTo(project: ProjectItem, allProjects: List<ProjectItem>): Boolean {
    if (isPureChat) return false
    if (!projectId.isNullOrBlank() && projectId == project.id) return true
    if (workspaceUri.isNotBlank() && project.uri.isNotBlank() &&
        workspaceUri.trimEnd('/').equals(project.uri.trimEnd('/'), ignoreCase = true)) return true
    return projectId.isNullOrBlank() && workspaceUri.isBlank() &&
        workspaceName.equals(project.name, ignoreCase = true) &&
        allProjects.count { it.name.equals(project.name, ignoreCase = true) } == 1
}
