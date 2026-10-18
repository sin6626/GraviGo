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

class LanguageSwitchTest {
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
                    var changes = false
                    scenario.onActivity {
                        previous = it
                        changes = AppLanguage.selected(it) != language
                        AppLanguage.change(it, language)
                    }
                    val deadline = System.currentTimeMillis() + 10_000
                    var ready = false
                    while (!ready && System.currentTimeMillis() < deadline) {
                        instrumentation.waitForIdleSync()
                        scenario.onActivity {
                            ready = (!changes || it !== previous) && it.getString(R.string.pair_title) == expected
                        }
                        if (!ready) Thread.sleep(50)
                    }
                    assertTrue("Activity must recreate with $language resources", ready)
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
