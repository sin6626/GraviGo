package io.github.sin6626.gravigo.ui.demo

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sin6626.gravigo.data.service.PreferencesManager
import io.github.sin6626.gravigo.ui.chat.ChatViewModel
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID

/** Explicit opt-in capture of native UI with isolated demo data; never uses the paired desktop. */
class ProductPromoCaptureTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun captureNativeProductSequence() {
        val run = InstrumentationRegistry.getArguments().getString("promoRun")
        assumeTrue("Only run when preparing promo assets", run != null && run.matches(Regex("[a-zA-Z0-9_-]+")))
        val output = File(compose.activity.getExternalFilesDir(null), "promo-$run").apply { mkdirs() }
        val namespace = "promo_${UUID.randomUUID()}_"
        val context = object : ContextWrapper(compose.activity.applicationContext) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int) = super.getSharedPreferences(namespace + name, mode)
            override fun getCacheDir() = File(super.getCacheDir(), namespace).apply { mkdirs() }
            override fun getNoBackupFilesDir() = File(super.getNoBackupFilesDir(), namespace).apply { mkdirs() }
        }
        val app = object : Application() { fun attach(c: Context) { attachBaseContext(c) } }.apply { attach(context) }
        ChatUxTest.FakeGateway().use { gateway ->
            gateway.management = true
            gateway.liveStream = true
            gateway.primaryModelLabel = "Antigravity"
            gateway.primaryConversationTitle = "登录页体验优化"
            gateway.messageText = "帮我优化登录页，并补齐交互测试。"
            gateway.filePreviewContent = """
                # 登录页体验优化

                ## 实现方案

                保持现有页面布局，优先完善输入反馈和错误提示。

                ### 1. 输入反馈

                邮箱格式即时校验，提交前保留已填写的内容。

                ### 2. 错误恢复

                网络失败后可以重试，不清空账号和输入内容。

                ### 3. 交互验证

                覆盖重复提交、加载状态和输入焦点的切换。

                确认方案后继续实现。
            """.trimIndent()
            PreferencesManager(app).apply { gatewayBaseUrl = gateway.url; deviceToken = "promo-demo-only"; lastConversationId = "A" }
            val vm = ChatViewModel(app)
            val store = ViewModelStore().apply { put("promo", vm) }
            fun snapshot(name: String) {
                compose.waitForIdle()
                val bitmap = compose.onNodeWithTag("promo-screen").captureToImage().asAndroidBitmap()
                File(output, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            }
            fun showReply(text: String, pending: Boolean = false) {
                val messages = JSONArray().put(JSONObject().put("id", "promo-user").put("type", "user").put("text", gateway.messageText))
                    .put(JSONObject().put("id", "promo-answer").put("type", "agent").put("text", text))
                gateway.emit("A", JSONObject().put("type", "update").put("cascadeId", "A").put("status", "IDLE")
                    .put("isFullSnapshot", true).put("messages", messages).put("canProceed", pending)
                    .put("proceedArtifactUri", if (pending) "file:///test/plan.md" else JSONObject.NULL).toString())
                compose.waitUntil(10_000) { vm.state.value.messages.any { it.effectiveText == text } }
            }
            try {
                compose.setContent {
                    Box(Modifier.fillMaxWidth().height(with(LocalDensity.current) { 1920.toDp() }).testTag("promo-screen")) {
                        ChatDemoScreen(vm, {})
                    }
                }
                compose.waitUntil(15_000) { !vm.state.value.isLoadingMessages && vm.state.value.messages.isNotEmpty() }
                compose.waitUntil(10_000) { gateway.requests.any { it.first.contains("cascade/stream") } }
                Thread.sleep(400)
                showReply("我会先梳理登录流程，再补齐交互与测试。\n\n- 输入校验与反馈\n- 网络失败后的重试\n- 保留用户已填写的内容")
                snapshot("01-conversation")
                val bounds = compose.onNodeWithTag("promo-screen").fetchSemanticsNode().boundsInWindow
                File(output, "bounds.json").writeText(JSONObject().put("x", bounds.left.toInt()).put("y", bounds.top.toInt())
                    .put("width", bounds.width.toInt()).put("height", bounds.height.toInt()).toString())
                File(output, "ready").writeText("ready")
                val deadline = SystemClock.elapsedRealtime() + 45_000
                while (!File(output, "go").exists()) {
                    check(SystemClock.elapsedRealtime() < deadline) { "Capture controller did not start" }
                    Thread.sleep(100)
                }
                Thread.sleep(2_000)
                compose.runOnIdle { vm.setDraft("网络失败时保留输入，再给一个重试入口。") }
                snapshot("02-instruction")
                Thread.sleep(2_000)
                compose.runOnIdle { vm.send() }
                compose.waitUntil(10_000) { !vm.state.value.isSending }
                showReply("收到。会保留当前输入，并加入重试入口。\n\n方案已经整理好，你可以先审阅，再决定是否继续实现。", pending = true)
                snapshot("03-plan-ready")
                Thread.sleep(2_000)
                compose.onNodeWithText("查看与评论").performClick()
                compose.waitUntil(10_000) { compose.onAllNodesWithText("登录页体验优化").fetchSemanticsNodes().isNotEmpty() }
                snapshot("04-plan")
                File(output, "actions-done").writeText("done")
                Thread.sleep(13_000)
            } finally { compose.runOnIdle { store.clear() } }
        }
    }
}
