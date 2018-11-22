package top.rechinx.meow.core.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.experimental.async
import top.rechinx.meow.core.extension.model.Extension
import top.rechinx.meow.core.extension.model.LoadResult
import top.rechinx.rikka.ext.launchNow

/**
 * Broadcast receiver that listens for the system's packages installed, updated or removed, and only
 * notifies the given [listener] when the package is an extension.
 *
 * @param listener The listener that should be notified of extension installation events.
 */
internal class ExtensionInstallReceiver(private val listener: Listener) :
        BroadcastReceiver() {

    /**
     * Registers this broadcast receiver
     */
    fun register(context: Context) {
        context.registerReceiver(this, filter)
    }

    /**
     * Returns the intent filter this receiver should subscribe to.
     */
    private val filter get() = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addDataScheme("package")
    }

    /**
     * Called when one of the events of the [filter] is received. When the package is an extension,
     * it's loaded in background and it notifies the [listener] when finished.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                if (!isReplacing(intent)) launchNow {
                    val result = getExtensionFromIntent(context, intent)
                    when (result) {
                        is LoadResult.Success -> listener.onExtensionInstalled(result.extension)
                    }
                }
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                launchNow {
                    val result = getExtensionFromIntent(context, intent)
                    when (result) {
                        is LoadResult.Success -> listener.onExtensionUpdated(result.extension)
                    }
                }
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                if (!isReplacing(intent)) {
                    val pkgName = getPackageNameFromIntent(intent)
                    if (pkgName != null) {
                        listener.onPackageUninstalled(pkgName)
                    }
                }
            }
        }
    }

    /**
     * Returns true if this package is performing an update.
     *
     * @param intent The intent that triggered the event.
     */
    private fun isReplacing(intent: Intent): Boolean {
        return intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
    }

    /**
     * Returns the extension triggered by the given intent.
     *
     * @param context The application context.
     * @param intent The intent containing the package name of the extension.
     */
    private suspend fun getExtensionFromIntent(context: Context, intent: Intent?): LoadResult {
        val pkgName = getPackageNameFromIntent(intent) ?:
                return LoadResult.Error("Package name not found")
        return async { ExtensionLoader.loadExtensionFromPkgName(context, pkgName) }.await()
    }

    /**
     * Returns the package name of the installed, updated or removed application.
     */
    private fun getPackageNameFromIntent(intent: Intent?): String? {
        return intent?.data?.encodedSchemeSpecificPart ?: return null
    }

    /**
     * Listener that receives extension installation events.
     */
    interface Listener {
        fun onExtensionInstalled(extension: Extension)
        fun onExtensionUpdated(extension: Extension)
        fun onPackageUninstalled(pkgName: String)
    }

}
