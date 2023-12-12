package com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_dev_endproject_jc_jvl.R
import com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory.CourtReservation

class CourtReservationAdapter(private val courtReservations: List<CourtReservation>) :
    RecyclerView.Adapter<CourtReservationAdapter.CourtReservationViewHolder>() {

    class CourtReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clubName: TextView = itemView.findViewById(R.id.clubName)
        val establishmentName: TextView = itemView.findViewById(R.id.establishmentName)
        val courtName: TextView = itemView.findViewById(R.id.courtName)
        val dateReservation: TextView = itemView.findViewById(R.id.dateReservation)
        val timeslot: TextView = itemView.findViewById(R.id.timeslot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return CourtReservationViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourtReservationViewHolder, position: Int) {
        val courtReservation = courtReservations[position]
        holder.clubName.text = "Establishment: ${courtReservation.clubEstablishmentName}"
        holder.establishmentName.text = "Address: ${courtReservation.clubEstablishmentAddress}"
        holder.courtName.text = "Court: ${courtReservation.courtName}"
        holder.dateReservation.text = "Date: ${courtReservation.dateReservation}"
        holder.timeslot.text = "TimeSlot: ${courtReservation.timeslot}"
    }

    override fun getItemCount(): Int {
        return courtReservations.size
    }
}
