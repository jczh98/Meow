package top.rechinx.meow.ui.catalogbrowse.filters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_navigation_spinner.view.*
import me.drakeet.multitype.ItemViewBinder
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.rikka.ext.inflate

class SelectItemBinder : ItemViewBinder<Filter.Select<*>,SelectItemBinder.SelectItemHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): SelectItemHolder {
        return SelectItemHolder(parent.inflate(R.layout.item_navigation_spinner))
    }

    override fun onBindViewHolder(holder: SelectItemHolder, item: Filter.Select<*>) {
        holder.bind(item)
    }

    class SelectItemHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(filter: Filter.Select<*>) {
            itemView.apply {
                nav_text.text = filter.name + ": "
                nav_item.prompt = filter.name
                nav_item.adapter = ArrayAdapter<Any>(itemView.context,
                        android.R.layout.simple_spinner_item, filter.options).apply {
                    setDropDownViewResource(R.layout.item_spinner_common)
                }
                nav_item.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {}

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        filter.value = p2
                    }
                }
                nav_item.setSelection(filter.value)
            }
        }
    }
}