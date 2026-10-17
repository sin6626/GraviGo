package com.antigravity.mobile.ui.demo

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AppSettingsLayoutTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun largeTextKeepsSwitchAndSourceLinksReachable() {
        val checked = mutableStateOf(true)
        var dismissed = false
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 1.5f)) {
                AppSettingsBottomSheet(checked.value, { checked.value = it },
                    profile = com.antigravity.mobile.data.model.AccountProfile("很长的账号名称用于检查大字号排版",
                        "long-account-name@example.com", "", "Pro")) { dismissed = true }
            }
        }
        compose.onNodeWithContentDescription("文件默认换行").performScrollTo().performClick().assertIsOff()
        compose.onNodeWithText("原项目").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("本项目代码").assertIsDisplayed()
        val bitmap = checkNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
        File(compose.activity.getExternalFilesDir(null), "settings-large-font.png").outputStream().use {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
        compose.onNodeWithContentDescription("关闭账号与设置").performScrollTo().performClick()
        compose.waitUntil(5_000) { dismissed }
    }
}
