package com.nara.bacayuk.ui.listener.adapter

import android.view.View

interface AdapterListener {
    fun onClick(data: Any?, position: Int?, view: View?, type: String)
}

interface AdapterQuizListener {
    fun onClick(data: Any?, position: Int?, view: View, view2: View, type: String)
}

interface ViewPositionListener {
    fun getView(view: View?, type: String)
}