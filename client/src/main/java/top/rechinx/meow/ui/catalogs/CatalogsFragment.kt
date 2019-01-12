package top.rechinx.meow.ui.catalogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_catalogs.*
import me.drakeet.multitype.MultiTypeAdapter
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import timber.log.Timber
import toothpick.config.Module
import top.rechinx.meow.R
import top.rechinx.meow.data.catalog.model.Catalog
import top.rechinx.meow.data.catalog.model.LocalCatalog
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.ui.base.BaseFragment
import top.rechinx.meow.ui.base.viewModel
import javax.inject.Inject
import javax.inject.Provider


class CatalogsFragment : BaseFragment(), CatalogAdapter.Listener {

    companion object {
        const val CATALOG_SOURCE_ID = "catalog_source_id"
    }

    override fun getModule(): Module? {
        return null
    }

    @Inject lateinit var  vmProvider: Provider<CatalogsViewModel>

    private val viewModel: CatalogsViewModel by lazy {
        InjectedViewModelProviders.of(this).get(vmProvider)
    }

    private lateinit var adapter : CatalogAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_catalogs, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CatalogAdapter(this)
        adapter.register(Catalog::class.java, CatalogBinder(adapter))

        recycler.adapter = adapter
        recycler.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        initSubscriptions()
    }

    private fun initSubscriptions() {
        viewModel.getLocalCatalogs()
                .observe(this, Observer { resource ->
                    when(resource) {
                        is Resource.Success -> {
                            adapter.items = resource.value
                            adapter.notifyDataSetChanged()
                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {

                        }
                    }

                })
    }

    override fun onCatalogClick(catalog: Catalog) {
        val bundle = Bundle()
        val id = when(catalog) {
            is LocalCatalog -> catalog.source.id
            else -> return
        }
        bundle.putLong(CATALOG_SOURCE_ID, id)
        findNavController().navigate(R.id.action_catalogs_to_catalog_browse, bundle)
    }

}
