package io.github.sin6626.gravigo.i18n

import androidx.annotation.StringRes
import java.lang.ref.WeakReference

private data class LocalErrorReference(
    val text: WeakReference<String>,
    @StringRes val resource: Int,
    val arguments: Array<out Any>,
)

private val localErrors = mutableListOf<LocalErrorReference>()

/** Track only app-created errors; never reinterpret server messages or user content. */
fun localError(@StringRes resource: Int, vararg arguments: Any): String {
    val text = String(appString(resource, *arguments).toCharArray())
    synchronized(localErrors) {
        localErrors.removeAll { it.text.get() == null }
        localErrors.add(LocalErrorReference(WeakReference(text), resource, arguments.copyOf()))
    }
    return text
}

fun relocalizeError(text: String?): String? {
    if (text == null) return null
    val reference = synchronized(localErrors) { localErrors.firstOrNull { it.text.get() === text } }
        ?: return text
    return localError(reference.resource, *reference.arguments)
}
