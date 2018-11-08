package top.rechinx.meow.ui.source

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
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.filter.FilterActivity
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.mvp.MvpFragment
import top.rechinx.rikka.mvp.factory.RequiresPresenter

class SourceFragment: Fragment(), BaseAdapter.OnItemClickListener {

    private lateinit var adapter: SourceAdapter
    private val sourceManager: SourceManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SourceAdapter(activity!!, ArrayList())
        adapter.setOnItemClickListener(this)
        sourceRecyclerView.itemAnimator = null
        sourceRecyclerView.setHasFixedSize(true)
        adapter.getItemDecoration()?.let { sourceRecyclerView.addItemDecoration(it) }
        sourceRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        sourceRecyclerView.adapter = adapter
        adapter.addAll(sourceManager.getSources().toList())
        custom_progress_bar.gone()
    }

    override fun onItemClick(view: View, position: Int) {
        startActivity(FilterActivity.createIntent(activity!!, adapter.getItem(position).id))
    }
}