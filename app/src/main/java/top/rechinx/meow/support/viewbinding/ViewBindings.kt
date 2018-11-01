package top.rechinx.meow.support.viewbinding

import android.app.Activity
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * from kotterknife
 */

object ViewBindings {
    fun reset(target: Any) = LazyRegistry.reset(target)
}

// bind view

fun <V: View> BottomSheetDialog.bindView(id: Int) : ReadOnlyProperty<BottomSheetDialog, V> = required(id, viewFinder)

fun <V: View> View.bindView(id: Int) : ReadOnlyProperty<View, V> = required(id, viewFinder)

fun <V: View> Activity.bindView(id: Int) : ReadOnlyProperty<Activity, V> = required(id, viewFinder)

fun <V: View> Fragment.bindView(id: Int) : ReadOnlyProperty<Fragment, V> = required(id, viewFinder)

public fun <V : View> RecyclerView.ViewHolder.bindView(id: Int)
        : ReadOnlyProperty<RecyclerView.ViewHolder, V> = required(id, viewFinder)

// bind optional view

fun <V : View> Activity.bindOptionalView(id: Int)
        : ReadOnlyProperty<Activity, V?> = optional(id, viewFinder)

// view finder

private val View.viewFinder: View.(Int) -> View?
    get() = { findViewById(it) }

private val BottomSheetDialog.viewFinder: BottomSheetDialog.(Int) -> View?
    get() = {  findViewById(it) }

private val Activity.viewFinder: Activity.(Int) -> View?
    get() = { findViewById(it) }

private val Fragment.viewFinder: Fragment.(Int) -> View?
    get() = { view!!.findViewById(it) }

private val RecyclerView.ViewHolder.viewFinder: RecyclerView.ViewHolder.(Int) -> View?
    get() = { itemView.findViewById(it) }


// library

private fun viewNotFound(id:Int, desc: KProperty<*>): Nothing =
        throw IllegalStateException("View ID $id for '${desc.name}' not found.")

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?)
        = Lazy { t: T, desc -> t.finder(id) as V? ?: viewNotFound(id, desc) }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> optional(id: Int, finder: T.(Int) -> View?)
        = Lazy { t: T, desc ->  t.finder(id) as V? }

private class Lazy<T, V>(private val initializer: (T, KProperty<*>) -> V) : ReadOnlyProperty<T, V> {
    private object EMPTY
    private var value: Any? = EMPTY

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        LazyRegistry.register(thisRef!!, this)
        if (value == EMPTY) {
            value = initializer(thisRef, property)
        }
        @Suppress("UNCHECKED_CAST")
        return value as V
    }

    fun reset() {
        value = EMPTY
    }
}

private object LazyRegistry {
    private val lazyMap = WeakHashMap<Any, MutableCollection<Lazy<*, *>>>()

    fun register(target: Any, lazy: Lazy<*, *>) {
        lazyMap.getOrPut(target) { Collections.newSetFromMap(WeakHashMap()) }.add(lazy)
    }

    fun reset(target: Any) {
        lazyMap[target]?.forEach { it.reset() }
    }
}