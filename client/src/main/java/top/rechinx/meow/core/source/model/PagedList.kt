package top.rechinx.meow.core.source.model

data class PagedList<T>(val list: T,
                        val hasNextPage: Boolean)