package io.github.sin6626.gravigo.ui.demo

import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import io.github.sin6626.gravigo.MainActivity
import io.github.sin6626.gravigo.R
import io.github.sin6626.gravigo.data.service.PreferencesManager
import io.github.sin6626.gravigo.i18n.AppLanguage
import io.github.sin6626.gravigo.i18n.appString
import io.github.sin6626.gravigo.i18n.localError
import io.github.sin6626.gravigo.i18n.relocalizeError
import io.github.sin6626.gravigo.ui.chat.ChatViewModel
import org.junit.Assert.*
import org.junit.Test
import android.graphics.Bitmap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class LanguageSwitchTest {
    @Test fun languageChangesDoNotExposeABlackWindow() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val originalLanguage = AppLanguage.selected(instrumentation.targetContext)
        fun isBlack(bitmap: Bitmap): Boolean = (1..5).all { x ->
            (1..5).all { y ->
                val pixel = bitmap.getPixel(bitmap.width * x / 6, bitmap.height * y / 6)
                android.graphics.Color.red(pixel) < 48 && android.graphics.Color.green(pixel) < 48 &&
                    android.graphics.Color.blue(pixel) < 48
            }
        }
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            try {
                val deadline = System.currentTimeMillis() + 10_000
                var ready = false
                while (!ready && System.currentTimeMillis() < deadline) {
                    instrumentation.uiAutomation.takeScreenshot()?.let {
                        ready = !isBlack(it)
                        it.recycle()
                    }
                    if (!ready) Thread.sleep(50)
                }
                assertTrue("Initial page must be visible before testing locale changes", ready)
                for (language in listOf("en", "zh-CN", "en")) {
                    val running = AtomicBoolean(true)
                    val frames = AtomicInteger()
                    val blackFrames = AtomicInteger()
                    val sampler = thread(name = "language-transition-frames") {
                        while (running.get()) {
                            instrumentation.uiAutomation.takeScreenshot()?.let {
                                if (isBlack(it)) blackFrames.incrementAndGet()
                                frames.incrementAndGet()
                                it.recycle()
                            }
                            Thread.sleep(10)
                        }
                    }
                    try {
                        scenario.onActivity { AppLanguage.change(it, language) }
                        instrumentation.waitForIdleSync()
                        Thread.sleep(650)
                    } finally {
                        running.set(false)
                        sampler.join(5_000)
                    }
                    assertFalse("Screenshot sampler must stop", sampler.isAlive)
                    assertTrue("Locale transition needs actual screen samples", frames.get() > 1)
                    assertEquals("Black frames while switching to $language", 0, blackFrames.get())
                }
            } finally {
                scenario.onActivity { AppLanguage.change(it, originalLanguage) }
            }
        }
    }

    @Test fun nativeLanguageChangesKeepDraftAndRetainedViewModel() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val originalLanguage = AppLanguage.selected(context)
        var localMessage: String? = null
        var rawMessage: String? = null
        var retained: ChatViewModel? = null
        val draft = "Keep this draft 原文 unchanged"
        val preferences = PreferencesManager(context)
        val attachment = preferences.storeDraftFile("attachment bytes".toByteArray(), "language-test.txt", "text/plain")
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity {
                retained = ViewModelProvider(it)[ChatViewModel::class.java]
                retained!!.setDraft(draft)
                retained!!.pair("")
            }
            try {
                for ((language, expected) in listOf("en" to "Pair device", "zh-CN" to "设备配对", "en" to "Pair device")) {
                    var previous: MainActivity? = null
                    scenario.onActivity {
                        previous = it
                        AppLanguage.change(it, language)
                    }
                    val deadline = System.currentTimeMillis() + 10_000
                    var ready = false
                    while (!ready && System.currentTimeMillis() < deadline) {
                        instrumentation.waitForIdleSync()
                        scenario.onActivity {
                            ready = it === previous && it.getString(R.string.pair_title) == expected
                        }
                        if (!ready) Thread.sleep(50)
                    }
                    assertTrue("Activity must update $language resources without recreating", ready)
                    scenario.onActivity {
                        assertSame(retained, ViewModelProvider(it)[ChatViewModel::class.java])
                        assertEquals(draft, retained!!.state.value.draft)
                        assertEquals(appString(R.string.msg_611fb781fbf6), retained!!.state.value.error)
                        assertEquals(expected, appString(R.string.pair_title))
                        assertEquals(language, AppLanguage.selected(it))
                        localMessage?.let { message -> assertEquals(expected, relocalizeError(message)) }
                        rawMessage?.let { message -> assertSame(message, relocalizeError(message)) }
                        localMessage = localError(R.string.pair_title)
                        rawMessage = String(localMessage!!.toCharArray())
                    }
                    assertArrayEquals("attachment bytes".toByteArray(), attachment.file.readBytes())
                }
                scenario.recreate()
                scenario.onActivity { assertEquals("Pair device", it.getString(R.string.pair_title)) }
            } finally {
                scenario.onActivity {
                    retained!!.setDraft("")
                    retained!!.clearError()
                    AppLanguage.change(it, originalLanguage)
                }
            }
        }
    }
}
