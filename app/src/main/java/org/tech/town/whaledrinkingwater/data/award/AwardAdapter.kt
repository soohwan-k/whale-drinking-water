package org.tech.town.whaledrinkingwater.data.award

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.tech.town.whaledrinkingwater.databinding.ItemAwardBinding

class AwardAdapter: androidx.recyclerview.widget.ListAdapter<AwardItem, AwardAdapter.ViewHolder>(
    diffUtil
) {

    inner class ViewHolder(private val binding: ItemAwardBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(awardItem: AwardItem){
            binding.awardImageView.setImageResource(awardItem.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAwardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<AwardItem>(){
            override fun areItemsTheSame(oldItem: AwardItem, newItem: AwardItem): Boolean {
                return oldItem.image == newItem.image
            }

            override fun areContentsTheSame(oldItem: AwardItem, newItem: AwardItem): Boolean {
                return oldItem == newItem
            }

        }
    }


}