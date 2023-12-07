package com.example.mobile_dev_endproject_jc_jvl

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClubEstablishmentAdapter(
    private val establishments: List<ClubEstablishment>,
    private val onItemClickListener: (ClubEstablishment) -> Unit
) : RecyclerView.Adapter<ClubEstablishmentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewClubEstablishmentName)
        val addressTextView: TextView = itemView.findViewById(R.id.textViewClubEstablishmentAddress)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener(establishments[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_club_establishment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val establishment = establishments[position]
        holder.nameTextView.text = establishment.clubEstablishmentName
        holder.addressTextView.text = establishment.clubEstablishmentAddress
    }

    override fun getItemCount(): Int {
        return establishments.size
    }
}