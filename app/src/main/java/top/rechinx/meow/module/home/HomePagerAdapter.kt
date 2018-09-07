package top.rechinx.meow.module.home

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import top.rechinx.meow.module.favorite.FavoriteFragment
import top.rechinx.meow.module.favorite.HistoryFragment

class HomePagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return FavoriteFragment()
    }

    override fun getCount(): Int = 1

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Favorite"
            else -> "Download"
        }
    }
}