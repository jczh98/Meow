package top.rechinx.meow.glide

import android.graphics.Color
import android.graphics.drawable.Drawable
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import top.rechinx.meow.data.catalog.model.InternalCatalog
import top.rechinx.meow.ui.widget.TextOvalDrawable

class InternalCatalogModelLoader : ModelLoader<InternalCatalog, Drawable> {

    override fun buildLoadData(model: InternalCatalog, width: Int, height: Int, options: Options): ModelLoader.LoadData<Drawable>? {
        val key = ObjectKey(model.source.id)
        val fetcher = Fetcher(model)
        return ModelLoader.LoadData(key, fetcher)
    }

    class Fetcher(private val model: InternalCatalog) : DataFetcher<Drawable> {

        override fun getDataClass(): Class<Drawable>
            = Drawable::class.java

        override fun cleanup() {}

        override fun getDataSource(): DataSource
            = DataSource.DATA_DISK_CACHE

        override fun cancel() {}

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Drawable>) {
            val sourceName = model.source.name
            val text = if (sourceName.isNotEmpty()) {
                sourceName.take(1)
            } else {
                ""
            }

            val drawable = TextOvalDrawable(
                    text = text,
                    backgroundColor = TextOvalDrawable.Colors.getColor(sourceName),
                    textColor = Color.WHITE
            )

            callback.onDataReady(drawable)
        }

    }

    override fun handles(model: InternalCatalog): Boolean = true

    class Factory : ModelLoaderFactory<InternalCatalog, Drawable> {

        override fun build(factory: MultiModelLoaderFactory): ModelLoader<InternalCatalog, Drawable> {
            return InternalCatalogModelLoader()
        }

        override fun teardown() {

        }

    }
}