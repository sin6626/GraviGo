package io.github.sin6626.gravigo.i18n

import android.app.Activity
import android.app.Application
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.StringRes
import java.util.Locale

class GraviGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLanguage.initialize(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppLanguage.invalidate()
    }
}

/** Android 13 uses system per-app languages; older devices keep the same choice locally. */
object AppLanguage {
    private var application: Application? = null
    @Volatile private var localizedContext: Context? = null
    private const val PREFERENCES = "app_language"
    private const val LANGUAGE = "language"

    fun initialize(app: Application) {
        application = app
        val preferences = app.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        if (!preferences.contains(LANGUAGE)) {
            // Preserve the original Chinese UI for existing paired installations.
            val paired = app.getSharedPreferences("agy_standard_prefs", Context.MODE_PRIVATE)
                .getString("gateway_base_url", null)?.isNotBlank() == true
            val initial = if (paired) "zh-CN" else selected(app)
            preferences.edit().putString(LANGUAGE, initial).apply()
            if (paired && Build.VERSION.SDK_INT >= 33 && app.getSystemService(LocaleManager::class.java).applicationLocales.isEmpty) {
                app.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags(initial)
            }
        }
    }

    fun selected(context: Context): String {
        val locale = if (Build.VERSION.SDK_INT >= 33) {
            val manager = context.getSystemService(LocaleManager::class.java)
            (if (manager.applicationLocales.isEmpty) manager.systemLocales else manager.applicationLocales)[0]
        } else {
            context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(LANGUAGE, null)
                ?.let(Locale::forLanguageTag) ?: context.resources.configuration.locales[0]
        }
        return if (locale.language == "zh") "zh-CN" else "en"
    }

    fun wrap(context: Context): Context {
        val locale = Locale.forLanguageTag(selected(context))
        return context.createConfigurationContext(Configuration(context.resources.configuration).apply { setLocale(locale) })
    }

    fun change(activity: Activity, language: String) {
        require(language == "en" || language == "zh-CN")
        if (selected(activity) == language) return
        invalidate()
        activity.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().putString(LANGUAGE, language).apply()
        if (Build.VERSION.SDK_INT >= 33) {
            activity.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags(language)
        } else activity.recreate()
    }

    internal fun invalidate() { localizedContext = null }

    fun currentLocale(): Locale = context()?.resources?.configuration?.locales?.get(0) ?: Locale.SIMPLIFIED_CHINESE

    internal fun context(): Context? = localizedContext ?: application?.let {
        wrap(it).also { context -> localizedContext = context }
    }
}

// Shared by UI callbacks and background services, using only an Application context.
fun appString(@StringRes id: Int, vararg arguments: Any): String {
    val context = AppLanguage.context()
    if (context != null) return if (arguments.isEmpty()) context.getString(id) else context.getString(id, *arguments)
    val message = checkNotNull(testMessages[id]) { "Unknown string resource: $id" }
    return if (arguments.isEmpty()) message else String.format(Locale.SIMPLIFIED_CHINESE, message, *arguments)
}
