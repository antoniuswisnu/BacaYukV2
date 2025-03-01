package com.nara.bacayuk.writing.number.menu

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Tulis
import com.nara.bacayuk.utils.invisible
import com.nara.bacayuk.utils.visible

class NumberAdapterBackup(
    private val context: Context,
    private val numbers: List<Char>,
    private val reportTulisAngka: List<Tulis>?
) : BaseAdapter() {

    private val diffCallback = object : DiffUtil.ItemCallback<Tulis>() {
        override fun areItemsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tulis, newItem: Tulis): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

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

            val data = reportTulisAngka?.find { it.tulisAngka == number.toString() }
//            imgChecklist.invisible()
            if (data?.reportTulisAngka?.latihanAngka == true) {
                imgChecklist.visible()
            } else {
                imgChecklist.invisible()
            }
            Log.d("NumberAdapter", "getView: $data")
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
        val imgChecklist: View = view.findViewById(R.id.img_checklist)
    }
}