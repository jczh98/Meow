package top.rechinx.meow.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutId(), container, false)
        initData()
        initViews()
        return view
    }

    protected abstract fun initViews()

    protected abstract fun getLayoutId(): Int

    protected open fun initData() {}
}