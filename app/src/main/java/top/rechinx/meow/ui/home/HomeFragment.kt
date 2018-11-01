package top.rechinx.meow.ui.home

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import top.rechinx.meow.R
import top.rechinx.meow.support.viewbinding.bindView

class HomeFragment: Fragment() {

    private val tab by bindView<TabLayout>(R.id.home_tab_layout)
    private val viewpager by bindView<ViewPager>(R.id.home_view_pager)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewpager.adapter = HomePagerAdapter(context!!, childFragmentManager)
        tab.setupWithViewPager(viewpager)
        return view
    }
}