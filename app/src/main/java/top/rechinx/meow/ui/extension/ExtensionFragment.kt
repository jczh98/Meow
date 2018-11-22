package top.rechinx.meow.ui.extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.custom_progress_bar.*
import kotlinx.android.synthetic.main.fragment_source.*
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.core.extension.ExtensionManager
import top.rechinx.meow.core.extension.model.Extension
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.filter.FilterActivity
import top.rechinx.meow.ui.source.SourceAdapter
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.mvp.MvpFragment
import top.rechinx.rikka.mvp.factory.RequiresPresenter

@RequiresPresenter(ExtensionPresenter::class)
class ExtensionFragment: MvpFragment<ExtensionPresenter>() {

    private lateinit var adapter: ExtensionAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ExtensionAdapter(activity!!, ArrayList())
        recyclerView.itemAnimator = null
        recyclerView.setHasFixedSize(true)
        adapter.getItemDecoration()?.let { recyclerView.addItemDecoration(it) }
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        customProgressBar.gone()
    }

    fun setInstalledExtensions(items: List<Extension>) {
        adapter.addAll(items)
    }

}