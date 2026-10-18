package io.github.sin6626.gravigo

import android.os.Bundle
import android.content.Context
import io.github.sin6626.gravigo.i18n.AppLanguage
import android.content.Intent
import android.animation.ValueAnimator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import io.github.sin6626.gravigo.ui.demo.animateBrandSplash
import io.github.sin6626.gravigo.ui.chat.ChatViewModel
import io.github.sin6626.gravigo.ui.demo.ChatDemoScreen

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        AppLanguage.invalidate()
        super.attachBaseContext(AppLanguage.wrap(newBase))
    }
    private val chatViewModel by viewModels<ChatViewModel>()
    private var wasBackgrounded = false
    private var launchAnimation: ValueAnimator? = null
    private var launchInProgress by mutableStateOf(false)

    override fun onResume() {
        super.onResume()
        if (wasBackgrounded) { wasBackgrounded = false; chatViewModel.resumeConnection() }
    }

    override fun onPause() {
        wasBackgrounded = true
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        chatViewModel.refreshLocalErrors()
        launchInProgress = savedInstanceState == null && intent?.data == null && ValueAnimator.areAnimatorsEnabled()
        splash.setOnExitAnimationListener { screen ->
            if (!launchInProgress || !ValueAnimator.areAnimatorsEnabled() ||
                !lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED)) {
                launchInProgress = false
                screen.remove()
            } else {
                launchAnimation = animateBrandSplash(screen.view, screen.iconView) {
                    screen.remove()
                    launchInProgress = false
                }
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.WHITE
        window.navigationBarColor = android.graphics.Color.WHITE
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent { ChatDemoScreen(chatViewModel, onLeaveApp = { moveTaskToBack(true) }, deferConversation = launchInProgress) }
        if (savedInstanceState == null) intent?.dataString?.let(chatViewModel::pair)
    }

    override fun onStop() {
        launchAnimation?.cancel()
        launchAnimation = null
        launchInProgress = false
        super.onStop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.dataString?.let(chatViewModel::pair)
    }
}
