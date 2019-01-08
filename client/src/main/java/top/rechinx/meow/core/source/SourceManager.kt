package top.rechinx.meow.core.source

import top.rechinx.meow.core.StubSource

class SourceManager {

    private val sourcesMap = mutableMapOf<Long, Source>()

    private val stubSourcesMap = mutableMapOf<Long, StubSource>()

    init {
        createInternalSources()
                .forEach {
                    registerSource(it)
                }
    }

    fun get(sourceKey: Long): Source? {
        return sourcesMap[sourceKey]
    }

    fun getOrStub(sourceKey: Long): Source {
        return sourcesMap[sourceKey] ?: stubSourcesMap.getOrPut(sourceKey) {
            StubSource(sourceKey)
        }
    }

    fun getSources() = sourcesMap.values

    fun registerSource(source: Source, overwrite: Boolean = false) {
        if(overwrite || !sourcesMap.containsKey(source.id)) {
            sourcesMap[source.id] = source
        }
    }

    fun unregisterSource(source: Source) {
        sourcesMap.remove(source.id)
    }

    private fun createInternalSources(): List<Source> = listOf(
    )
}