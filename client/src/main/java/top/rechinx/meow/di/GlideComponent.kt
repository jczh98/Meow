package top.rechinx.meow.di

import dagger.Subcomponent
import top.rechinx.meow.di.modules.GlideDaggerModule
import top.rechinx.meow.glide.ClientGlideModule

@Subcomponent(modules = [GlideDaggerModule::class])
interface GlideComponent {

    fun inject(clientGlideModule: ClientGlideModule)

    @Subcomponent.Builder
    interface Builder {
        fun build(): GlideComponent
    }
}