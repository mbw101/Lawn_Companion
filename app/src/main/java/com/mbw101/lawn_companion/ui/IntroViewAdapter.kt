package com.mbw101.lawn_companion.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter

/**
Lawn Companion
Created by Malcolm Wright
Date: 2021-05-13
 */
class IntroViewPagerAdapter(private val screens: ArrayList<ScreenItem>) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int = screens.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        super.instantiateItem(container, position)
        TODO("Not yet implemented")
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by [.instantiateItem]. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view Page View to check for association with `object`
     * @param object Object to check for association with `view`
     * @return true if `view` is associated with the key object `object`
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        TODO("Not yet implemented")
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    override fun getItem(position: Int): Fragment {
        super.getItemPosition(position)
        TODO("Not yet implemented")
    }

}
