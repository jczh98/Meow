package top.rechinx.meow.core.extension

import android.content.Context
import com.jakewharton.rxrelay2.BehaviorRelay
import top.rechinx.meow.core.extension.model.Extension
import top.rechinx.meow.core.extension.model.LoadResult
import top.rechinx.meow.core.source.SourceManager

class ExtensionManager(private val context: Context,
                       private val sourceManager: SourceManager) {

    /**
     * Relay used to notify the installed extensions.
     */
    val installedExtensionsRelay = BehaviorRelay.create<List<Extension>>()

    /**
     * List of the currently installed extensions.
     */
    var installedExtensions = emptyList<Extension>()
        private set(value) {
            field = value
            installedExtensionsRelay.accept(value)
        }

    init {
        initExtensions()
        ExtensionInstallReceiver(InstallationListener()).register(context)
    }

    /**
     * Loads and registers the installed extensions.
     */
    private fun initExtensions() {
        val extensions = ExtensionLoader.loadExtensions(context)

        installedExtensions = extensions
                .filterIsInstance<LoadResult.Success>()
                .map { it.extension }
        installedExtensions.flatMap { it.sources }
                .forEach {
                    sourceManager.registerSource(it, true)
                }
    }

    /**
     * Registers the given extension in this and the source managers.
     *
     * @param extension The extension to be registered.
     */
    private fun registerNewExtension(extension: Extension) {
        installedExtensions += extension
        extension.sources.forEach { sourceManager.registerSource(it) }
    }

    /**
     * Registers the given updated extension in this and the source managers previously removing
     * the outdated ones.
     *
     * @param extension The extension to be registered.
     */
    private fun registerUpdatedExtension(extension: Extension) {
        val mutInstalledExtensions = installedExtensions.toMutableList()
        val oldExtension = mutInstalledExtensions.find { it.pkgName == extension.pkgName }
        if (oldExtension != null) {
            mutInstalledExtensions -= oldExtension
            extension.sources.forEach { sourceManager.unregisterSource(it) }
        }
        mutInstalledExtensions += extension
        installedExtensions = mutInstalledExtensions
        extension.sources.forEach { sourceManager.registerSource(it) }
    }

    /**
     * Unregisters the extension in this and the source managers given its package name. Note this
     * method is called for every uninstalled application in the system.
     *
     * @param pkgName The package name of the uninstalled application.
     */
    private fun unregisterExtension(pkgName: String) {
        val installedExtension = installedExtensions.find { it.pkgName == pkgName }
        if (installedExtension != null) {
            installedExtensions -= installedExtension
            installedExtension.sources.forEach { sourceManager.unregisterSource(it) }
        }
    }

    /**
     * Listener which receives events of the extensions being installed, updated or removed.
     */
    private inner class InstallationListener : ExtensionInstallReceiver.Listener {

        override fun onExtensionInstalled(extension: Extension) {
            registerNewExtension(extension)
        }

        override fun onExtensionUpdated(extension: Extension) {
            registerUpdatedExtension(extension)
        }

        override fun onPackageUninstalled(pkgName: String) {
            unregisterExtension(pkgName)
        }
    }

}