package top.rechinx.meow.core.extension

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import dalvik.system.PathClassLoader
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import top.rechinx.meow.core.extension.model.Extension
import top.rechinx.meow.core.extension.model.LoadResult
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.support.log.L
import java.lang.Exception

object ExtensionLoader {

    private const val EXTENSION_FEATURE = "meow.extension"
    private const val METADATA_SOURCE_CLASS = "meow.extension.class"

    private const val PACKAGE_FLAGS = PackageManager.GET_CONFIGURATIONS or PackageManager.GET_SIGNATURES

    /**
     * Return a list of all the installed extensions initialized concurrently.
     *
     * @param context The application context.
     */
    fun loadExtensions(context: Context) : List<LoadResult> {
        val pkgManager = context.packageManager
        val installedPkgs = pkgManager.getInstalledPackages(PACKAGE_FLAGS)
        val extPkgs = installedPkgs.filter { isPackageAnExtension(it) }

        if(extPkgs.isEmpty()) return emptyList()

        return runBlocking {
            val deferred = extPkgs.map {
                async { loadExtension(context, it.packageName, it) }
            }
            deferred.map { it.await() }
        }
    }

    /**
     * Loads an extension given its package name.
     *
     * @param context The application context.
     * @param pkgName The package name of the extension to load.
     * @param pkgInfo The package info of the extension.
     */
    private fun loadExtension(context: Context, pkgName: String, pkgInfo: PackageInfo): LoadResult {
        val pkgManager = context.packageManager

        val appInfo = try {
            pkgManager.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
        } catch (error: PackageManager.NameNotFoundException) {
            // Unlikely, but the package may have been uninstalled at this point
            return LoadResult.Error(error)
        }

        val extName = pkgManager.getApplicationLabel(appInfo)?.toString()
                .orEmpty().substringAfter("Meow: ")

        val versionName = pkgInfo.versionName
        val versionCode = pkgInfo.versionCode

        val classLoader = PathClassLoader(appInfo.sourceDir, null, context.classLoader)

        val sources = appInfo.metaData.getString(METADATA_SOURCE_CLASS)
                .split(";")
                .map {
                    val sourceClass = it.trim()
                    if (sourceClass.startsWith("."))
                        pkgInfo.packageName + sourceClass
                    else
                        sourceClass
                }
                .flatMap {
                    try {
                        val obj = Class.forName(it, false, classLoader).newInstance()
                        when (obj) {
                            is HttpSource -> listOf(obj)
                            else -> throw Exception("Unknown source class type! ${obj.javaClass}")
                        }
                    } catch (e: Throwable) {
                        return LoadResult.Error(e)
                    }
                }

        val extension = Extension(extName, pkgName, versionName, versionCode, sources)
        return LoadResult.Success(extension)
    }

    /**
     * Attempts to load an extension from the given package name. It checks if the extension
     * contains the required feature flag before trying to load it.
     */
    fun loadExtensionFromPkgName(context: Context, pkgName: String): LoadResult {
        val pkgInfo = try {
            context.packageManager.getPackageInfo(pkgName, PACKAGE_FLAGS)
        } catch (error: PackageManager.NameNotFoundException) {
            // Unlikely, but the package may have been uninstalled at this point
            return LoadResult.Error(error)
        }
        if (!isPackageAnExtension(pkgInfo)) {
            return LoadResult.Error("Tried to load a package that wasn't a extension")
        }
        return loadExtension(context, pkgName, pkgInfo)
    }

    /**
     * Returns true if the given package is an extension.
     *
     * @param pkgInfo The package info of the application.
     */
    private fun isPackageAnExtension(pkgInfo: PackageInfo): Boolean {
        return pkgInfo.reqFeatures.orEmpty().any { it.name == EXTENSION_FEATURE }
    }
}