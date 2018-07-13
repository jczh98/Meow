package top.rechinx.meow.module.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife

abstract class BaseFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutId(), container, false)
        ButterKnife.bind(this, view)
        initPresenter()
        initView()
        return view
    }

    protected abstract fun initView()

    protected abstract fun initPresenter()

    protected abstract fun getLayoutId(): Int
}