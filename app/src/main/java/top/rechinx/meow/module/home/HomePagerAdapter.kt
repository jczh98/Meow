package top.rechinx.meow.module.home

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import top.rechinx.meow.R
import top.rechinx.meow.module.favorite.FavoriteFragment

class HomePagerAdapter(context: Context, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    private var mContext: Context = context

    override fun getItem(position: Int): Fragment {
        return FavoriteFragment()
    }

    override fun getCount(): Int = 1

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> mContext.getString(R.string.favorite)
            else -> mContext.getString(R.string.download)
        }
    }
}