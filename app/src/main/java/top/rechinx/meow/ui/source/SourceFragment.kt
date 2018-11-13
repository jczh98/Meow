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
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.filter.FilterActivity
import top.rechinx.rikka.ext.gone

class SourceFragment: Fragment(), BaseAdapter.OnItemClickListener {

    private lateinit var adapter: SourceAdapter
    private val sourceManager: SourceManager by inject()
    private val preferences: PreferenceHelper by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_source, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SourceAdapter(activity!!, ArrayList())
        adapter.setOnItemClickListener(this)
        adapter.setOnItemCheckedlistener(object : SourceAdapter.OnItemCheckedListener {
            override fun onItemCheckedListener(isChecked: Boolean, position: Int) {
                val source = adapter.getItem(position)
                preferences.setSourceSwitch(source.id, isChecked)
            }
        })
        recyclerView.itemAnimator = null
        recyclerView.setHasFixedSize(true)
        adapter.getItemDecoration()?.let { recyclerView.addItemDecoration(it) }
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.addAll(sourceManager.getSources().toList())
        customProgressBar.gone()
    }

    override fun onItemClick(view: View, position: Int) {
        startActivity(FilterActivity.createIntent(activity!!, adapter.getItem(position).id))
    }
}