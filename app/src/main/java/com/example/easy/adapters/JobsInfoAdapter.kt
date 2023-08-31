package com.example.easy.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.easy.data.JobInformation
import com.example.easy.databinding.JobInformationItemBinding

class JobsInfoAdapter : RecyclerView.Adapter<JobsInfoAdapter.SpecialViewHolder>() {
    inner class SpecialViewHolder(private val binding: JobInformationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(jobInformation: JobInformation) {
            binding.apply {
                val imageUrl = jobInformation.jobImages?.get(0)
                val imageUri = imageUrl?.let { Uri.parse(it) }
                Glide.with(itemView)
                    .load(imageUri)
                    .into(jobImageItem)
                tvJobTitle.text = jobInformation.jobTitle
                tvJobPrice.text = jobInformation.price
                Log.d("debugging", "Set data : ${jobInformation.jobImages?.get(0)}")
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<JobInformation>() {
        override fun areItemsTheSame(oldItem: JobInformation, newItem: JobInformation): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: JobInformation, newItem: JobInformation): Boolean {
            return newItem == oldItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialViewHolder {
        return SpecialViewHolder(
            JobInformationItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SpecialViewHolder, position: Int) {
        val job = differ.currentList[position]
        holder.bind(jobInformation = job)
        holder.itemView.setOnClickListener {
            onClick?.invoke(job)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((JobInformation) -> Unit)? = null
}