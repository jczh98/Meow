package top.rechinx.meow.ui.extension

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_source.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.R
import top.rechinx.meow.core.extension.model.Extension
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.rikka.ext.gone

class ExtensionAdapter(context: Context, list: ArrayList<Extension>): BaseAdapter<Extension>(context, list), KoinComponent {

    private lateinit var onItemCheckedListener: OnItemCheckedListener

    fun setOnItemCheckedlistener(listener: OnItemCheckedListener) {
        this.onItemCheckedListener = listener
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val offset = parent.width / 90
                outRect.set(offset, 0, offset, (offset * 1.5).toInt())
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val extension = datas[position]
        holder.itemView.title.text = extension.name
        holder.itemView.switchCompat.gone()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_source, parent, false))
    }

    interface OnItemCheckedListener {
        fun onItemCheckedListener(isChecked: Boolean, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}