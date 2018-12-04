package top.rechinx.meow.ui.grid.history

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import com.google.android.material.snackbar.Snackbar
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.SelectableAdapter
import eu.davidea.flexibleadapter.helpers.ActionModeHelper
import kotlinx.android.synthetic.main.fragment_grid.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.details.DetailActivity
import top.rechinx.meow.ui.grid.GridAdapter
import top.rechinx.meow.ui.grid.items.GridItem
import top.rechinx.rikka.mvp.MvpFragment
import top.rechinx.rikka.mvp.factory.RequiresPresenter

@RequiresPresenter(HistoryPresenter::class)
class HistoryFragment: MvpFragment<HistoryPresenter>(),
        FlexibleAdapter.OnItemClickListener,
        FlexibleAdapter.OnItemLongClickListener,
        ActionMode.Callback{

    private val adapter by lazy { GridAdapter(activity!!) }

    private var actionModeHelper : ActionModeHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    fun initViews() {
        adapter.addListener(this)
        actionModeHelper = ActionModeHelper(adapter, R.menu.menu_grid, this)
                .withDefaultMode(SelectableAdapter.Mode.MULTI)
        adapter.addListener(this)
        recyclerView.adapter = adapter
        presenter.load()
    }


    fun onMangasLoaded(list: List<Manga>) {
        adapter.updateDataSet(list.map { GridItem(it) })
    }

    fun onMangasLoadError(throwable: Throwable) {
    }

    override fun onItemClick(view: View, position: Int) : Boolean{
        if(adapter.mode != SelectableAdapter.Mode.IDLE && actionModeHelper != null) {
            val active = actionModeHelper!!.onClick(position)
            return active
        } else {
            val manga = adapter.getItem(position)?.manga ?: return false
            val intent = DetailActivity.createIntent(activity!!, manga.sourceId, manga.url!!)
            startActivity(intent)
            return true
        }
    }

    override fun onItemLongClick(position: Int) {
        actionModeHelper?.onLongClick(activity as AppCompatActivity, position)
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_select_all -> {
                adapter.selectAll()
                return true
            }
            R.id.action_delete -> {
                deleteSelectedItems()
            }
        }
        return false
    }

    private fun deleteSelectedItems() {
        presenter.deleteHistoryManga(adapter.selectedPositions.map {
            val item = adapter.getItem(it)?.manga ?: return
            return@map item.id
        })
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
    }

    fun onHistoryDeleted(ids: List<Long>) {
        for (id in ids) {
            adapter.removeItemByMangaId(id)
        }
        Snackbar.make(layoutView, R.string.message_delete_success, Snackbar.LENGTH_SHORT)
    }
}