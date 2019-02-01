package top.rechinx.meow.ui.catalogbrowse.filters

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_navigation_spinner.view.*
import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.R

class SelectHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(wrapper: FilterWrapper<*>) {
        wrapper as FilterWrapper.Select<*>
        val filter = wrapper.filter as Filter.Select<*>
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
                    wrapper.value = p2
                }
            }
            nav_item.setSelection(wrapper.value)
        }
    }
}