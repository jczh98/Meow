package top.rechinx.meow.di

import toothpick.config.Binding
import toothpick.config.Module
import javax.inject.Provider

/**
 * Binds the given [instance] to its class.
 */
inline fun <reified T> Module.bindInstance(instance: T) {
    bind(T::class.java).toInstance(instance)
}

inline fun <reified B, reified P : Provider<B>> Module.bindProvider(): Binding<B>
.BoundStateForProviderClassBinding {
    return bind(B::class.java).toProvider(P::class.java)
}

inline fun <reified B, reified D : B> Module.bindTo(): Binding<B>.BoundStateForClassBinding {
    return bind(B::class.java).to(D::class.java)
}