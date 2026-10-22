package io.github.sin6626.gravigo.ui.demo

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sin6626.gravigo.data.service.PreferencesManager
import io.github.sin6626.gravigo.ui.chat.ChatViewModel
import io.github.sin6626.gravigo.ui.chat.PairingScreen
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID

/** Opt-in native production footage. All network responses and storage belong to this test. */
class ProductFeatureCaptureTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun captureFeature() {
        val args = InstrumentationRegistry.getArguments()
        val feature = args.getString("promoFeature")
        val run = args.getString("promoRun")
        val english = args.getString("promoLanguage") == "en"
        val translations = if (english) JSONObject(InstrumentationRegistry.getInstrumentation().context.assets
            .open("promo-english.json").bufferedReader().use { it.readText() }) else JSONObject()
        fun tr(value: String): String = if (english) translations.getString(value) else value
        assumeTrue(run != null && run.matches(Regex("[a-zA-Z0-9_-]+")) && feature in listOf(
            "pairing", "workspace", "stream", "context", "attachments", "plan", "diff", "reading",
            "models", "tasks", "voice", "desktop", "revert", "interaction", "rich"))
        val output = File(compose.activity.getExternalFilesDir(null), "film-$run/$feature").apply { mkdirs() }
        val namespace = "film_${UUID.randomUUID()}_"
        val context = object : ContextWrapper(compose.activity.applicationContext) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int) = super.getSharedPreferences(namespace + name, mode)
            override fun getCacheDir() = File(super.getCacheDir(), namespace).apply { mkdirs() }
            override fun getNoBackupFilesDir() = File(super.getNoBackupFilesDir(), namespace).apply { mkdirs() }
        }
        val app = object : Application() { fun attach(c: Context) { attachBaseContext(c) } }.apply { attach(context) }
        val mode = mutableStateOf(if (feature == "pairing") "pairing" else "chat")
        ChatUxTest.FakeGateway().use { gateway ->
            gateway.management = true
            gateway.demoEnglish = english
            gateway.liveStream = true
            gateway.primaryModelLabel = "Gemini"
            gateway.secondaryModelLabel = "Claude"
            gateway.thirdModelLabel = "Text model"
            gateway.quotaResetTime = java.time.Instant.now().plusSeconds(7200).toString()
            gateway.taskStartedTime = java.time.Instant.now().minusSeconds(90).toString()
            gateway.accountName = "GraviGo Demo"
            gateway.accountEmail = "demo@example.invalid"
            gateway.primaryConversationTitle = tr("登录体验升级")
            gateway.projectName = "GraviGo Studio"
            gateway.messageText = tr("完善登录反馈，并补齐交互测试。")
            gateway.projectFixture = true
            gateway.planFixture = feature == "plan"
            gateway.taskFixture = feature == "tasks"
            gateway.queueFixture = feature == "tasks"
            gateway.running = feature == "tasks"
            gateway.interactionFixture = feature == "interaction"
            gateway.voiceFixture = feature == "voice"
            gateway.previewFiles = 2
            gateway.filePreviewName = if (feature == "reading") "LoginForm.kt" else "implementation_plan.md"
            gateway.filePreviewContent = if (feature == "reading") tr("""
                package studio.gravigo

                data class LoginState(
                    val email: String = "",
                    val isSubmitting: Boolean = false,
                    val error: String? = null,
                )

                fun canSubmit(state: LoginState): Boolean {
                    return state.email.contains("@") &&
                        !state.isSubmitting
                }

                // 网络失败时保留输入，允许重试。
            """.trimIndent()).trimIndent() else tr("""
                # 登录体验升级

                ## 先把交互想清楚

                **目标**：输入不丢失，错误可恢复。

                ### 1. 输入反馈

                邮箱格式即时校验，提交前保留已填写内容。

                ### 2. 网络恢复

                失败后显示重试入口，不清空用户输入。

                ### 3. 验收清单

                | 场景 | 预期 |
                | --- | --- |
                | 重复提交 | 只发一次请求 |
                | 连接失败 | 保留草稿 |
                | 重试成功 | 回到当前会话 |

                ```kotlin
                if (!state.isSubmitting) submit()
                ```

                ```katex
                E = mc^2
                ```

                ```mermaid
                flowchart TD
                  A[输入] --> B[校验]
                  B --> C[提交]
                  C --> D[反馈]
                ```
            """.trimIndent()).trimIndent()
            gateway.diffJson = JSONObject().put("cascadeId", "A").put("files", JSONArray().put(JSONObject()
                .put("fileUri", "file:///test/LoginForm.kt").put("fileName", "LoginForm.kt").put("actionType", "MODIFY")
                .put("originalContent", "fun submit() {\n    clearInput()\n    sendRequest()\n}")
                .put("modifiedContent", "fun submit() {\n    keepInput()\n    sendRequest()\n}")
                .put("firstTouchedStepIndex", 2).put("lastTouchedStepIndex", 4))).toString()
            PreferencesManager(app).apply {
                gatewayBaseUrl = gateway.url; deviceToken = "isolated-film-demo"
                if (feature != "workspace" && feature != "pairing") lastConversationId = "A"
            }
            val vm = ChatViewModel(app)
            val store = ViewModelStore().apply { put("film", vm) }
            var captureBounds: androidx.compose.ui.geometry.Rect? = null
            fun waitFile(name: String) {
                val deadline = SystemClock.elapsedRealtime() + 60_000
                while (!File(output, name).exists()) {
                    check(SystemClock.elapsedRealtime() < deadline) { "Capture controller timed out: $name" }
                    Thread.sleep(100)
                }
            }
            fun tick(milliseconds: Long = 250) {
                val until = SystemClock.elapsedRealtime() + milliseconds
                while (SystemClock.elapsedRealtime() < until) {
                    compose.mainClock.advanceTimeByFrame()
                    Thread.sleep(16)
                }
            }
            fun pause() = tick(1_300)
            fun await(timeout: Long = 10_000, condition: () -> Boolean) {
                val deadline = SystemClock.elapsedRealtime() + timeout
                while (!condition()) {
                    check(SystemClock.elapsedRealtime() < deadline) { "Native state did not become ready: $feature" }
                    tick(32)
                }
            }
            fun snapshot(name: String) {
                compose.waitForIdle()
                val full = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot() ?: error("No native screenshot")
                val b = captureBounds ?: compose.onNodeWithTag("film-screen").fetchSemanticsNode().boundsInWindow.also { captureBounds = it }
                val cropped = Bitmap.createBitmap(full, b.left.toInt(), b.top.toInt(), b.width.toInt(), b.height.toInt())
                File(output, "$name.png").outputStream().use { cropped.compress(Bitmap.CompressFormat.PNG, 100, it) }
                cropped.recycle(); full.recycle()
            }
            fun emit(text: String, running: Boolean) {
                gateway.emit("A", JSONObject().put("type", "update").put("cascadeId", "A")
                    .put("status", if (running) "RUNNING" else "IDLE").put("isFullSnapshot", true)
                    .put("messages", JSONArray().put(JSONObject().put("id", "film-user").put("stepIndex", 1).put("type", "user").put("text", gateway.messageText))
                        .put(JSONObject().put("id", "film-agent").put("stepIndex", 2).put("type", "agent").put("text", text))).toString())
            }
            try {
                compose.setContent {
                    Box(Modifier.fillMaxSize().background(Color.White).testTag("film-screen")) {
                        when (mode.value) {
                            "pairing" -> PairingScreen(false, null, onPair = { _, _ -> })
                            "file" -> LinkedFilePreview("file:///test/${gateway.filePreviewName}", vm) { mode.value = "chat" }
                            "desktop" -> {
                                val state by vm.state.collectAsState()
                                ChatDemoScreen(vm, {})
                                DesktopStatusBottomSheet(onDismiss = { mode.value = "chat" }, status = state.desktopStatus,
                                    loading = state.isLoadingDesktopStatus, refreshing = state.isRefreshingDesktopStatus,
                                    error = state.desktopStatusError, onRefresh = vm::loadDesktopStatus)
                            }
                            else -> ChatDemoScreen(vm, {})
                        }
                    }
                }
                await(15_000) { !vm.state.value.isLoadingModels && !vm.state.value.isLoadingConversations && !vm.state.value.isLoadingMessages }
                if (feature == "context") {
                    compose.runOnIdle { vm.createProjectConversation(vm.state.value.projects.single()) }
                    await(10_000) { vm.state.value.selectedConversationId == "C" && !vm.state.value.isLoadingMessages }
                }
                pause()
                snapshot("before")
                val b = compose.onNodeWithTag("film-screen").fetchSemanticsNode().boundsInWindow
                File(output, "bounds.json").writeText(JSONObject().put("x", b.left.toInt()).put("y", b.top.toInt())
                    .put("width", b.width.toInt()).put("height", b.height.toInt()).toString())
                File(output, "ready").writeText("ready")
                waitFile("go")
                compose.mainClock.autoAdvance = false
                tick(700)
                when (feature) {
                    "pairing" -> { compose.onAllNodes(hasSetTextAction())[0].performTextInput("DEMO-PAIRING-CODE"); pause() }
                    "workspace" -> {
                        compose.onNode(hasText(tr("项目")) and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)).performClick().also { tick() }
                        pause(); compose.onNodeWithText("GraviGo Studio").performClick().also { tick() }; pause()
                        compose.onNodeWithText(tr("新建会话")).performClick().also { tick() }
                        await(10_000) { vm.state.value.selectedConversationId == "C" }
                    }
                    "stream" -> {
                        gateway.messageText = tr("网络失败时，请保留输入并提供重试。")
                        compose.runOnIdle { vm.setDraft(tr("网络失败时，请保留输入并提供重试。")) }; pause()
                        compose.onNodeWithContentDescription(tr("发送消息")).performClick().also { tick() }
                        val text = tr("## 登录体验升级\n\n收到。先保留输入，再完善反馈。\n\n- 邮箱格式即时校验\n- 失败后保留草稿\n- 重试成功继续当前任务\n\n**实现与交互测试会一起补齐。**")
                        for (end in 8..text.length step 8) { emit(text.take(end), true); tick(160) }
                        emit(text, false)
                        await(10_000) { vm.state.value.messages.any { it.effectiveText == text } && !vm.state.value.isRunning && vm.state.value.outgoing.isEmpty() }
                    }
                    "context" -> {
                        compose.runOnIdle { vm.setDraft("@Chat") }
                        await(10_000) { vm.state.value.contextSuggestions.isNotEmpty() }
                        pause(); compose.onNodeWithContentDescription(tr("引用文件 src/ChatViewModel.kt")).performClick().also { tick() }
                        compose.runOnIdle { vm.setDraft("/plan") }
                        await(10_000) { vm.state.value.commandSuggestions.isNotEmpty() }
                        pause(); compose.onNodeWithContentDescription(tr("选择命令 /plan")).performClick().also { tick() }
                        compose.runOnIdle { vm.setDraft(tr("结合这个文件，先给出实现方案。")) }
                        check(vm.state.value.workspaceContexts.isNotEmpty() && vm.state.value.slashCommand != null)
                    }
                    "attachments" -> {
                        val parent = output.parentFile
                        fun shared(name: String) = androidx.core.content.FileProvider.getUriForFile(compose.activity,
                            "${compose.activity.packageName}.fileprovider", File(parent, name))
                        compose.runOnIdle { vm.addImages(listOf(shared("brand.png"))) }
                        await(10_000) { vm.state.value.attachments.isNotEmpty() }
                        pause(); compose.runOnIdle { vm.addFile(shared("requirements.txt")); vm.setDraft(tr("参考附件，保持品牌与页面风格一致。")) }
                        await(10_000) { vm.state.value.fileAttachment != null }
                        compose.onNodeWithContentDescription(tr("移除待发送图片")).assertExists()
                    }
                    "plan" -> {
                        compose.onNodeWithText(tr("查看与评论")).performClick().also { tick() }; pause()
                        compose.onNodeWithContentDescription(tr("评论第 1 行")).performClick().also { tick() }
                        compose.onNodeWithContentDescription(tr("方案评论内容")).performTextInput(tr("补充网络失败后的恢复步骤。"))
                        pause(); compose.onNodeWithText(tr("加入评论")).performClick().also { tick() }
                        compose.onNodeWithText(tr("待提交评论 1 条")).assertExists()
                    }
                    "diff" -> {
                        compose.onNodeWithContentDescription(tr("更多选项")).performClick().also { tick() }
                        compose.onNodeWithText(tr("审阅变更")).performClick().also { tick() }; pause()
                        compose.onNodeWithContentDescription(tr("评论新增第 2 行")).performClick().also { tick() }
                        compose.onNodeWithContentDescription(tr("变更评论内容")).performTextInput(tr("这里保留用户输入，避免重复填写。"))
                        pause(); compose.onNodeWithText(tr("加入评论")).performClick().also { tick() }
                        compose.onNodeWithText(tr("待提交评论 1 条")).assertExists()
                    }
                    "reading" -> {
                        compose.runOnIdle { mode.value = "file" }
                        await(10_000) { compose.onAllNodesWithContentDescription(tr("自动换行")).fetchSemanticsNodes().isNotEmpty() }
                        pause(); compose.onNodeWithContentDescription(tr("自动换行")).performClick().also { tick() }; pause()
                        compose.onNodeWithContentDescription(tr("自动换行")).performClick().also { tick() }
                    }
                    "rich" -> {
                        compose.runOnIdle { mode.value = "file" }
                        await(10_000) { compose.onAllNodesWithContentDescription(tr("文件内容")).fetchSemanticsNodes().isNotEmpty() }
                        pause(); compose.onNodeWithContentDescription(tr("文件内容")).performScrollToIndex(8)
                        pause(); compose.onNodeWithContentDescription(tr("文件内容")).performScrollToIndex(9)
                        pause(); compose.onNodeWithContentDescription(tr("文件内容")).performScrollToIndex(10)
                        pause(); compose.onNodeWithContentDescription(tr("文件内容")).performScrollToIndex(11)
                    }
                    "models" -> {
                        compose.onNode(hasSetTextAction()).performClick().also { tick() }
                        compose.onNodeWithContentDescription(tr("选择模型")).performClick().also { tick() }; pause()
                        compose.onNodeWithText("Claude").performClick().also { tick() }; pause()
                        compose.onNodeWithContentDescription(tr("查看模型额度")).performClick().also { tick() }
                        await(10_000) { vm.state.value.modelQuotaSummary != null }
                        compose.onNodeWithText("73%").assertExists()
                    }
                    "tasks" -> {
                        compose.onNodeWithContentDescription(tr("收起后台任务")).performClick().also { tick() }; pause()
                        compose.onNodeWithContentDescription(tr("展开后台任务")).performClick().also { tick() }; pause()
                        compose.onNodeWithContentDescription(tr("立即发送 先补充单元测试")).performClick().also { tick() }
                    }
                    "voice" -> {
                        compose.runOnIdle { vm.startVoiceInput(); vm.offerVoiceAudio(byteArrayOf(1, 2), .2f, 1_000L) }
                        for (i in 2..5) { tick(400); compose.runOnIdle { vm.offerVoiceAudio(byteArrayOf(1, 2), .15f + i * .05f, i * 1_000L) } }
                        compose.runOnIdle { vm.finishVoiceInput() }
                        await(10_000) { vm.state.value.draft.contains(tr("继续检查动画")) }
                    }
                    "desktop" -> {
                        if (english) compose.runOnIdle { vm.loadDesktopStatus(); mode.value = "desktop" }
                        else {
                            compose.onNodeWithContentDescription(tr("打开菜单")).performClick().also { tick() }; pause()
                            compose.onNodeWithText(tr("电脑状态")).performClick().also { tick() }
                        }
                        pause()
                        await(10_000) { vm.state.value.desktopStatus != null || vm.state.value.desktopStatusError != null }
                        check(vm.state.value.desktopStatus != null) { vm.state.value.desktopStatusError.orEmpty() }
                        tick()
                        compose.onNodeWithText(tr("权限与审批")).assertExists()
                    }
                    "revert" -> {
                        compose.onNodeWithContentDescription(tr("回退到这条消息")).performClick().also { tick() }; pause()
                        compose.onNodeWithText(tr("确认回退吗？")).assertExists()
                    }
                    "interaction" -> {
                        compose.onNodeWithContentDescription(tr("选择 保留动效")).performClick().also { tick() }; pause()
                        compose.onNodeWithContentDescription(tr("选择 保留滚动位置")).performClick().also { tick() }
                        compose.onNodeWithContentDescription(tr("补充说明")).performTextInput(tr("优先保证输入和阅读体验。"))
                    }
                }
                compose.runOnIdle { compose.activity.window.insetsController?.hide(android.view.WindowInsets.Type.ime()) }
                pause(); snapshot("after")
                File(output, "done").writeText("native actions and state checks passed")
                waitFile("ack")
            } finally {
                compose.mainClock.autoAdvance = true
                InstrumentationRegistry.getInstrumentation().runOnMainSync {
                    store.clear()
                }
            }
        }
    }
}
