package top.rechinx.meow.core.extension.model

sealed class LoadResult {

    class Success(val extension: Extension) : LoadResult()
    class Error(val message: String? = null) : LoadResult() {
        constructor(exception: Throwable) : this(exception.message)
    }
}
