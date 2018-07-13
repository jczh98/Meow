package top.rechinx.meow.core

import top.rechinx.meow.model.Comic

interface SearchIterator {

    fun empty(): Boolean

    operator fun hasNext(): Boolean

    operator fun next(): Comic?

}