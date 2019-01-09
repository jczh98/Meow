package top.rechinx.meow.ui.catalogbrowse

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_catalog_browse.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.visible
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.ui.catalogs.CatalogBinder
import top.rechinx.meow.ui.catalogs.CatalogsFragment

class CatalogBrowseFragment : Fragment() {

    private val viewModel by viewModel<CatalogBrowseViewModel>()

    private val sourceId: Long by lazy {
        arguments?.getLong(CatalogsFragment.CATALOG_SOURCE_ID) ?: -1
    }

    private val sourceManager by inject<SourceManager>()

    private val source by lazy {
        sourceManager.getOrStub(sourceId)
    }

    private lateinit var adapter: CatalogBrowseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalog_browse, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup toolbar
        catalogbrowse_toolbar.title = source.name
        catalogbrowse_toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter = CatalogBrowseAdapter()
        adapter.register(Manga::class.java, MangaBinder())

        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(view.context, 3)

        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModel.fetchMangaList(sourceId)
        viewModel.mangaListLiveData.observe(this, Observer { resource ->
            when(resource) {
                is Resource.Loading -> {
                    progress.visible()
                }
                is Resource.Success -> {
                    adapter.items = resource.value
                    adapter.notifyDataSetChanged()
                    progress.gone()
                }
            }
        })
    }

}
