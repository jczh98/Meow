package top.rechinx.meow.ui.filter.items

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.support.log.L

class SelectItem(val filter: Filter.Select<*>) : AbstractFlexibleItem<SelectItem.Holder>() {

    override fun getLayoutRes(): Int {
        return R.layout.item_navigation_spinner
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): Holder? {
        return Holder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: Holder, position: Int, payloads: MutableList<Any>?) {
        holder.text.text = filter.name + ": "

        val spinner = holder.spinner
        spinner.prompt = filter.name
        spinner.adapter = ArrayAdapter<Any>(holder.itemView.context,
                android.R.layout.simple_spinner_item, filter.values).apply {
            setDropDownViewResource(R.layout.item_spinner_common)
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                filter.state = p2
            }
        }
        spinner.setSelection(filter.state)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return filter == (other as SelectItem).filter
    }

    override fun hashCode(): Int {
        return filter.hashCode()
    }

    class Holder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {

        val text: TextView = itemView.findViewById(R.id.navViewItemText)
        val spinner: Spinner = itemView.findViewById(R.id.navViewItem)
    }
}