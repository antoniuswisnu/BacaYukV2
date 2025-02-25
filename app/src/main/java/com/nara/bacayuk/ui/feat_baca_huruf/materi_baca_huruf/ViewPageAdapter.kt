package com.nara.bacayuk.ui.feat_baca_huruf.materi_baca_huruf

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageAdapter(fragmentActivity: FragmentActivity, private val isKata:Boolean = false, listener:(CharSequence)->Unit,
                       ): FragmentStateAdapter(fragmentActivity) {
    private val dataFragments= mutableListOf(
        Huruf1Fragment.newInstance("0",listener),
        Huruf1Fragment.newInstance("1",listener),
        Huruf1Fragment.newInstance("2",listener)
    )
    private val dataFragmentsKata= mutableListOf(
        Huruf1Fragment.newInstanceKata("0",listener),
        Huruf1Fragment.newInstanceKata("1",listener),
        Huruf1Fragment.newInstanceKata("2",listener),
        Huruf1Fragment.newInstanceKata("3",listener),
        Huruf1Fragment.newInstanceKata("4",listener)
    )

    override fun getItemCount(): Int =
        if (isKata) 5 else 3

    override fun createFragment(position: Int): Fragment =
        if (isKata) dataFragmentsKata[position] else dataFragments[position]
}