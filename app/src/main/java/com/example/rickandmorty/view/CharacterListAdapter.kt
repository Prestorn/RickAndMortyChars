package com.example.rickandmorty.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.CharacterListItemBinding
import com.example.rickandmorty.model.db.LocalCharacter
import kotlin.math.roundToInt

class CharacterListAdapter(
    private val characterList: List<LocalCharacter>,
    private val onItemClick: (LocalCharacter) -> Unit
) : RecyclerView.Adapter<CharacterListAdapter.MyHolder>() {
    class MyHolder(val binding: CharacterListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: CharacterListItemBinding =
            CharacterListItemBinding.inflate(inflater, parent, false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyHolder,
        position: Int
    ) {
        val firstCharacter: Int = position * 2
        val secondCharacter: Int = position * 2 + 1
        holder.binding.characterOneName.text = characterList[firstCharacter].name
        holder.binding.characterOneSpecies.text = characterList[firstCharacter].species
        holder.binding.characterOneGender.text = characterList[firstCharacter].gender
        holder.binding.characterOneStatusText.text = characterList[firstCharacter].status
        when (characterList[firstCharacter].status) {
            "Alive" -> holder.binding.characterOneStatusImage.setImageResource(R.drawable.green_circle)
            "Dead" -> holder.binding.characterOneStatusImage.setImageResource(R.drawable.red_circle)
            else -> {
                holder.binding.characterOneStatusImage.setImageResource(R.drawable.gray_circle)
                holder.binding.characterOneStatusText.text = "Unknown"
            }
        }

        if (characterList[firstCharacter].image != "Unknown") {
            Glide.with(holder.itemView.context)
                .load(characterList[firstCharacter].image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.empty_image)
                .into(holder.binding.characterOneImage)
        }


        holder.binding.characterOne.setOnClickListener { onItemClick(characterList[firstCharacter]) }


        if (secondCharacter < characterList.size) {
            holder.binding.characterTwoName.text = characterList[secondCharacter].name
            holder.binding.characterTwoSpecies.text = characterList[secondCharacter].species
            holder.binding.characterTwoGender.text = characterList[secondCharacter].gender
            holder.binding.characterTwoStatusText.text = characterList[secondCharacter].status
            when (characterList[secondCharacter].status) {
                "Alive" -> holder.binding.characterTwoStatusImage.setImageResource(R.drawable.green_circle)
                "Dead" -> holder.binding.characterTwoStatusImage.setImageResource(R.drawable.red_circle)
                else -> {
                    holder.binding.characterTwoStatusImage.setImageResource(R.drawable.gray_circle)
                    holder.binding.characterTwoStatusText.text = "Unknown"
                }
            }

            if (characterList[secondCharacter].image != "Unknown") {
                Glide.with(holder.itemView.context)
                    .load(characterList[secondCharacter].image)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.empty_image)
                    .into(holder.binding.characterTwoImage)
            }


            holder.binding.characterTwo.setOnClickListener { onItemClick(characterList[secondCharacter]) }

        } else {
            holder.binding.characterTwo.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return (characterList.size / 2.0).roundToInt()
    }

}