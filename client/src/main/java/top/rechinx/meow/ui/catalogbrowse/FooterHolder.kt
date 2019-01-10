package top.rechinx.meow.ui.catalogbrowse

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_footer.view.*
import top.rechinx.meow.R
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.inflate
import top.rechinx.meow.rikka.ext.visible
import top.rechinx.meow.rikka.ext.visibleIf

class FooterHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        parent.inflate(R.layout.item_footer)
) {

    fun bind(showProgress: Boolean, showEndReached: Boolean) {
        itemView.apply {
            progress.visibleIf { showProgress }
            message.visibleIf { showEndReached }
        }
    }
}