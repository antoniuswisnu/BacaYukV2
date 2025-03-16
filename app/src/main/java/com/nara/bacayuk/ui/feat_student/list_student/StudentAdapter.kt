package com.nara.bacayuk.ui.feat_student.list_student

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nara.bacayuk.R
import com.nara.bacayuk.data.model.Student
import com.nara.bacayuk.databinding.ItemSiswaBinding
import com.nara.bacayuk.ui.listener.adapter.AdapterListener
import com.nara.bacayuk.utils.loadImage


class StudentAdapter(val listener: AdapterListener): RecyclerView.Adapter<StudentAdapter.RecentAdapterViewHolder>() {

    var selectedStudent: Student? = null

    inner class RecentAdapterViewHolder(val view: View) :
        RecyclerView.ViewHolder(view)

    private val diffCallback = object : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.uuid == newItem.uuid
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitData(list: List<Student>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentAdapterViewHolder {
        val binding = ItemSiswaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentAdapterViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecentAdapterViewHolder, position: Int) {
        holder.view.apply {
            val data = differ.currentList[position]
            Log.d("TAG", "onBindViewHolder: $data")
            val binding = ItemSiswaBinding.bind(this)
//            binding.imgProfile.loadImage(holder.view.context,data.profilPicture)
            binding.txtName.text = data.fullName

            if (data == selectedStudent) {
                binding.imgProfile.borderColor = resources.getColor(R.color.primary_500)
                binding.txtName.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.primary_500))
                binding.txtName.setTextColor(resources.getColor(R.color.white))
            } else {
                binding.imgProfile.borderColor = resources.getColor(R.color.primary_100)
                binding.txtName.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.primary_100))
                binding.txtName.setTextColor(resources.getColor(R.color.black))
            }

            rootView.setOnClickListener {
                selectedStudent = data
                notifyDataSetChanged()
                listener.onClick(data, position, binding.root,"")
            }
        }
    }



    override fun getItemCount(): Int = differ.currentList.size

}

