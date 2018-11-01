package top.rechinx.meow.core.source

import android.content.Context
import top.rechinx.meow.core.source.internal.Dmzj

class SourceManager(private val context: Context) {

    private val sourcesMap = mutableMapOf<Long, Source>()

    init {
        createInternalSources().forEach{ registerSource(it)}
    }

    open fun get(sourceKey: Long): Source? {
        return sourcesMap[sourceKey]
    }

    fun getSources() = sourcesMap.values

    fun registerSource(source: Source) {
        if(!sourcesMap.containsKey(source.id)) {
            sourcesMap[source.id] = source
        }
    }

    fun unregisterSource(source: Source) {
        sourcesMap.remove(source.id)
    }

    private fun createInternalSources(): List<Source> = listOf(
            Dmzj()
    )
}