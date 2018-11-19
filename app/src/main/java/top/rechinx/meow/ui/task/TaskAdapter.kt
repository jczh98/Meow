package top.rechinx.meow.ui.task

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_task.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible

class TaskAdapter(context: Context, list: ArrayList<Task>) : BaseAdapter<Task>(context, list) {

    private var latestChapterId: Long = 0

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val offset = parent.width / 90
                outRect.set(0, 0, 0, offset)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_task, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val task = datas.get(position)
        holder.itemView.apply {
            title.text = task.title
            state.text = context.getString(getState(task))
            page.text = "${task.progress}/${task.max}"
            progress.max = task.max
            progress.progress = task.progress
            if(task.chapterId == latestChapterId) last.visible() else last.gone()
        }
    }

    fun getPositionById(id: Long): Int {
        val size = datas.size
        for (i in 0 until size) {
            if (datas[i].id == id) {
                return i
            }
        }
        return -1
    }

    fun setLast(id: Long) {
        if(id == latestChapterId) return
        val tmp = latestChapterId
        latestChapterId = id
        for(i in 0 until itemCount) {
            val item = datas[i]
            if(item.chapterId == latestChapterId) {
                notifyItemChanged(i)
            } else if(item.chapterId == tmp) {
                notifyItemChanged(i)
            }
        }
    }

    private fun getState(task: Task): Int {
        return when (task.state) {
            Task.STATE_PAUSE -> R.string.task_pause
            Task.STATE_PARSE -> R.string.task_parse
            Task.STATE_DOING -> R.string.task_dowloading
            Task.STATE_FINISH -> R.string.task_finish
            Task.STATE_WAIT -> R.string.task_wait
            Task.STATE_ERROR -> R.string.task_error
            else -> R.string.task_pause
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}