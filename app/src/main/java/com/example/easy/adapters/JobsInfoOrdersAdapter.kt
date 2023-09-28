package com.example.easy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.easy.data.Order
import com.example.easy.databinding.JobInformationItemBinding

class JobsInfoOrdersAdapter : RecyclerView.Adapter<JobsInfoOrdersAdapter.OrdersViewHolder>() {


    inner class OrdersViewHolder(private val binding: JobInformationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {

                tvJobTitle.text = order.jobTitle
                tvJobDate.text = order.date
                tvJobLocation.text = order.location
                btnEditOrder.setOnClickListener {
                    onClick?.invoke(order)
                }

            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.jobInformationUid == newItem.jobInformationUid
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            JobInformationItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
        /*holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }*/

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Order) -> Unit)? = null

}