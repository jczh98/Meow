package top.rechinx.meow.ui.setting

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_theme.view.*
import org.koin.standalone.KoinComponent
import top.rechinx.meow.R
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.utils.ThemeHelper
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible

class ThemeAdapter(context: Context, list: ArrayList<Triple<Int, Int, String>>): BaseAdapter<Triple<Int, Int, String>>(context, list), KoinComponent {

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val theme = datas[position]
        holder.itemView.apply {
            itemColor.background = ColorDrawable(theme.second)
            title.text = theme.third
            if (ThemeHelper.getTheme(context) == theme.first) {
                selected.visible()
            } else {
                selected.gone()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_theme, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}