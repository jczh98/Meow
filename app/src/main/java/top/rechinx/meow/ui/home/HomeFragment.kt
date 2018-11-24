package top.rechinx.meow.ui.home

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import top.rechinx.meow.R
import top.rechinx.meow.ui.grid.download.DownloadFragment
import top.rechinx.meow.ui.grid.favorite.FavoriteFragment
import top.rechinx.meow.ui.grid.history.HistoryFragment

class HomeFragment: Fragment() {

    // Instance all fragments
    private val fragments : Array<Fragment> = arrayOf(FavoriteFragment(),
            HistoryFragment(),
            DownloadFragment())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pages = resources.getStringArray(R.array.home_tabs)
        homeViewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {

            override fun getItem(position: Int): Fragment = fragments[position]

            override fun getCount(): Int = fragments.size

            override fun getPageTitle(position: Int): CharSequence = pages[position]
        }
        homeTabLayout.setupWithViewPager(homeViewPager)
    }
}