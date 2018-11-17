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

class TaskAdapter(context: Context, list: ArrayList<Task>) : BaseAdapter<Task>(context, list) {

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
            page.text = "${task.progress}/${task.max}"
            progress.max = task.max
            progress.progress = task.progress
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}