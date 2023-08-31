package com.example.easy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easy.databinding.ViewPagerItemBinding

class ViewPager2Images : RecyclerView.Adapter<ViewPager2Images.ViewPager2ImagesHolder>() {
    inner class ViewPager2ImagesHolder(private val binding: ViewPagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            binding.apply {
                Glide.with(itemView).load(imagePath).into(binding.imageJobDetails)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return newItem == oldItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImagesHolder {
        return ViewPager2ImagesHolder(
            ViewPagerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPager2ImagesHolder, position: Int) {
        val jobImage = differ.currentList[position]
        holder.bind(jobImage)
    }

}
