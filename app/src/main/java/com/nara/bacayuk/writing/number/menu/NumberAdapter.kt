package com.nara.bacayuk.writing.number.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.nara.bacayuk.R

class NumberAdapter(
    private val context: Context,
    private val numbers: List<Char>
) : BaseAdapter() {

    override fun getCount(): Int = numbers.size

    override fun getItem(position: Int): Any = numbers[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_layout, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val number = numbers[position]

        holder.apply {
            numberText.text = number.toString()
            cardView.setOnClickListener {
                numberClickListener?.onNumberClick(number.toString())
            }
        }

        return view
    }

    private var numberClickListener: OnNumberClickListener? = null

    fun setOnNumberClickListener(listener: OnNumberClickListener) {
        this.numberClickListener = listener
    }

    interface OnNumberClickListener {
        fun onNumberClick(number: String)
    }

    private class ViewHolder(view: View) {
        val cardView: ConstraintLayout = view.findViewById(R.id.cardView)
        val numberText: TextView = view.findViewById(R.id.letterText)
    }
}