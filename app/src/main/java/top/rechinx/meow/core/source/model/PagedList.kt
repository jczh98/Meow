package top.rechinx.meow.core.source.model

data class PagedList<T>(val list: List<T>, val hasNextPage: Boolean)