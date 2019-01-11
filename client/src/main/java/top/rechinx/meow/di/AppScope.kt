package top.rechinx.meow.di

import toothpick.Scope
import toothpick.Toothpick

/**
 * Global scope for application level dependency injection
 */
object AppScope {

    fun root(): Scope {
        return Toothpick.openScope(this)
    }

    fun subscope(any: Any): Scope {
        return Toothpick.openScopes(this, any)
    }

    /**
     * Injects the application dependencies on the given object. Note the provided object must have
     * members annotated with the @Inject annotation.
     */
    fun inject(obj: Any) {
        Toothpick.inject(obj, root())
    }

    /**
     * Returns an instance of [T] from the root scope.
     */
    inline fun <reified T> getInstance(): T {
        return root().getInstance(T::class.java)
    }

}