package io.github.sin6626.gravigo.ui.demo

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelStore
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sin6626.gravigo.data.service.PreferencesManager
import io.github.sin6626.gravigo.i18n.AppLanguage
import io.github.sin6626.gravigo.ui.chat.ChatViewModel
import org.json.JSONObject
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.util.UUID

/** Locale changes use Android's real frame clock rather than the Compose test scheduler. */
class ProductLocaleCaptureTest {
    @Test fun captureLanguage() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val run = InstrumentationRegistry.getArguments().getString("promoRun")
        val english = InstrumentationRegistry.getArguments().getString("promoLanguage") == "en"
        assumeTrue(run != null && run.matches(Regex("[a-zA-Z0-9_-]+")))
        val prefix = "locale_film_${UUID.randomUUID()}_"
        val context = object : ContextWrapper(instrumentation.targetContext) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int) = super.getSharedPreferences(prefix + name, mode)
            override fun getCacheDir() = File(super.getCacheDir(), prefix).apply { mkdirs() }
            override fun getNoBackupFilesDir() = File(super.getNoBackupFilesDir(), prefix).apply { mkdirs() }
        }
        val app = object : Application() { fun attach(c: Context) { attachBaseContext(c) } }.apply { attach(context) }
        val output = File(context.getExternalFilesDir(null), "film-$run/settings").apply { mkdirs() }
        val store = ViewModelStore()
        val original = AppLanguage.selected(instrumentation.targetContext)
        var crop = android.graphics.Rect()
        fun waitFor(condition: () -> Boolean) {
            val deadline = SystemClock.elapsedRealtime() + 60_000
            while (!condition()) { check(SystemClock.elapsedRealtime() < deadline) { "Locale capture timed out" }; Thread.sleep(80) }
        }
        fun find(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
            if (node == null) return null
            if (node.text?.toString() == text || node.contentDescription?.toString() == text) return node
            for (i in 0 until node.childCount) find(node.getChild(i), text)?.let { return it }
            return null
        }
        fun click(text: String) {
            waitFor { find(instrumentation.uiAutomation.rootInActiveWindow, text) != null }
            val node = checkNotNull(find(instrumentation.uiAutomation.rootInActiveWindow, text))
            val bounds = android.graphics.Rect(); node.getBoundsInScreen(bounds)
            val now = SystemClock.uptimeMillis()
            val down = android.view.MotionEvent.obtain(now, now, android.view.MotionEvent.ACTION_DOWN, bounds.exactCenterX(), bounds.exactCenterY(), 0)
            val up = android.view.MotionEvent.obtain(now, now + 70, android.view.MotionEvent.ACTION_UP, bounds.exactCenterX(), bounds.exactCenterY(), 0)
            try {
                check(instrumentation.uiAutomation.injectInputEvent(down, true))
                check(instrumentation.uiAutomation.injectInputEvent(up, true))
            } finally { down.recycle(); up.recycle() }
            Thread.sleep(1_000)
        }
        fun snapshot(name: String) {
            val full = checkNotNull(instrumentation.uiAutomation.takeScreenshot())
            val image = Bitmap.createBitmap(full, crop.left, crop.top, crop.width(), crop.height())
            File(output, "$name.png").outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
            image.recycle(); full.recycle()
        }
        ChatUxTest.FakeGateway().use { gateway ->
            gateway.management = true
            gateway.demoEnglish = english
            gateway.accountName = "GraviGo Demo"; gateway.accountEmail = "demo@example.invalid"
            gateway.primaryModelLabel = "Gemini"
            gateway.primaryConversationTitle = if (english) "Login experience" else "登录体验升级"
            gateway.messageText = if (english) "Improve login feedback and add interaction tests." else "完善登录反馈，并补齐交互测试。"
            PreferencesManager(app).apply { gatewayBaseUrl = gateway.url; deviceToken = "isolated-locale-demo"; lastConversationId = "A" }
            ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
                try {
                    scenario.onActivity { activity ->
                        AppLanguage.change(activity, if (english) "en" else "zh-CN")
                        val vm = ChatViewModel(app).also { store.put("locale", it) }
                        activity.setContent { Box(Modifier.fillMaxSize().background(Color.White)) { ChatDemoScreen(vm, {}) } }
                    }
                    Thread.sleep(2_000)
                    scenario.onActivity { activity ->
                        val view = activity.findViewById<android.view.View>(android.R.id.content)
                        val location = IntArray(2); view.getLocationOnScreen(location)
                        crop = android.graphics.Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)
                    }
                    snapshot("before")
                    File(output, "bounds.json").writeText(JSONObject().put("x", crop.left).put("y", crop.top).put("width", crop.width()).put("height", crop.height()).toString())
                    File(output, "ready").writeText("ready")
                    waitFor { File(output, "go").exists() }
                    click(if (english) "Open menu" else "打开菜单"); click(if (english) "Account & settings" else "账号与设置")
                    check(find(instrumentation.uiAutomation.rootInActiveWindow, "GraviGo Demo") != null)
                    click(if (english) "简体中文" else "English")
                    waitFor { find(instrumentation.uiAutomation.rootInActiveWindow, if (english) "账号与设置" else "Account & settings") != null }
                    click(if (english) "English" else "简体中文")
                    waitFor { find(instrumentation.uiAutomation.rootInActiveWindow, if (english) "Account & settings" else "账号与设置") != null }
                    snapshot("after")
                    File(output, "done").writeText("real-clock Chinese/English switching and isolated account passed")
                    waitFor { File(output, "ack").exists() }
                } finally {
                    scenario.onActivity { AppLanguage.change(it, original); store.clear() }
                }
            }
        }
    }
}
