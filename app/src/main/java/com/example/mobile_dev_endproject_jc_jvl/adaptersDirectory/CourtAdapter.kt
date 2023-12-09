package com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_dev_endproject_jc_jvl.R

class CourtAdapter(private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<CourtAdapter.ViewHolder>() {

    private val courtList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_court, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courtName = courtList[position]
        holder.bind(courtName)
        holder.itemView.setOnClickListener { onItemClick(courtName) }
    }

    override fun getItemCount(): Int = courtList.size

    fun addData(courtName: String) {
        courtList.add(courtName)
        notifyDataSetChanged()
    }

    fun clearData() {
        courtList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courtNameTextView: TextView = itemView.findViewById(R.id.courtNameTextView)

        fun bind(courtName: String) {
            courtNameTextView.text = courtName
        }
    }
}
