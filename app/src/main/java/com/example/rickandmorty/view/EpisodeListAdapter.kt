package com.example.rickandmorty.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.EpisodesListItemBinding
import com.example.rickandmorty.model.db.Episode

class EpisodeListAdapter(
    private val episodeList: List<Episode>
) : RecyclerView.Adapter<EpisodeListAdapter.MyHolder>() {

    class MyHolder(val binding: EpisodesListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: EpisodesListItemBinding = EpisodesListItemBinding.inflate(
            inflater, parent, false
        )
        return MyHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyHolder,
        position: Int
    ) {
        holder.binding.episodeNumber.text = episodeList[position].id.toString()
        holder.binding.episodeName.text = episodeList[position].name
    }

    override fun getItemCount(): Int {
        return episodeList.size
    }


}