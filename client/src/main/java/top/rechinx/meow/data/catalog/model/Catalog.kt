package top.rechinx.meow.data.catalog.model

import top.rechinx.meow.core.source.Source

abstract class Catalog {
    abstract val name: String
    abstract val description: String
}

abstract class LocalCatalog : Catalog(){
    abstract val source: Source
}

data class InternalCatalog(
        override val name: String,
        override val description: String,
        override val source: Source) : LocalCatalog()