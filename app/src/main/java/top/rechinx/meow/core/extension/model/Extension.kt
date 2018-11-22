package top.rechinx.meow.core.extension.model

import top.rechinx.meow.core.source.Source

data class Extension(val name: String,
                     val pkgName: String,
                     val versionName: String,
                     val versionCode: Int,
                     val sources: List<Source>,
                     val hasUpdate: Boolean = false)