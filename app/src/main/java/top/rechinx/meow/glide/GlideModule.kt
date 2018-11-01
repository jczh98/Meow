package top.rechinx.meow.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import top.rechinx.meow.data.database.model.Manga
import java.io.InputStream

@GlideModule
class GlideModule: AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(Manga::class.java, InputStream::class.java, MangaModelLoader.Factory())
        registry.append(InputStream::class.java, InputStream::class.java, PassthroughModelLoader
                .Factory())
    }
}