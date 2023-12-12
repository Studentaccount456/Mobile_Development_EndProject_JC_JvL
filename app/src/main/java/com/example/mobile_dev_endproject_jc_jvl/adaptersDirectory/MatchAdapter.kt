package com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobile_dev_endproject_jc_jvl.R
import com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory.AccountActivity
import com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory.JoinMatchActivity
import com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory.Match

class MatchAdapter(private val matches: List<Match>) :
    RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressTextView: TextView = itemView.findViewById(R.id.textViewClubEstablishmentAddress)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.textViewDateTime)
        val imagesLayout: LinearLayout = itemView.findViewById(R.id.linearImages)
        val usernamesLayout: LinearLayout = itemView.findViewById(R.id.linearUsernames)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_item, parent, false)
        return MatchViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]

        holder.addressTextView.text = match.clubEstablishmentAddress
        holder.dateTimeTextView.text = "Date: ${match.dateReservation}        TimeSlot: ${match.timeslot}"

        // Clear existing views in layouts
        holder.imagesLayout.removeAllViews()
        holder.usernamesLayout.removeAllViews()

        // Bind data to the images layout (LinearLayout)
        val imageLinearLayout = LinearLayout(holder.itemView.context)
        imageLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageLinearLayout.orientation = LinearLayout.HORIZONTAL
        imageLinearLayout.gravity = Gravity.CENTER


        // Bind data to the usernames layout (LinearLayout)
        val usernameLinearLayout = LinearLayout(holder.itemView.context)
        usernameLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        usernameLinearLayout.orientation = LinearLayout.HORIZONTAL
        usernameLinearLayout.gravity = Gravity.CENTER


        for (i in 1..4) {
            val avatarKey = "UserAvatar_$i"
            val usernameKey = "UserName_$i"
            val userIdKey = "UserId_$i"

            val avatarValue = match.participators[avatarKey]
            val usernameValue = match.participators[usernameKey]
            val userIdValue = match.participators[userIdKey]

            val imageView = ImageView(holder.itemView.context)
            val layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            // Check if avatar is "Default"
            if (avatarValue != null && avatarValue == "Default") {
                imageView.setImageResource(R.drawable.ic_joinmatch)
            } else {
                Glide.with(holder.itemView).load(avatarValue).into(imageView)
            }

            imageView.layoutParams = layoutParams
            // Padding in pixels
            val paddingPx = 16
            imageView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)

            imageView.setOnClickListener {
                // Check if avatar is "Default"
                if (avatarValue != null && avatarValue == "Default") {
                    // Launch JoinMatchActivity
                    val intent = Intent(holder.itemView.context, JoinMatchActivity::class.java)
                    intent.putExtra("dateReservation", match.dateReservation)
                    intent.putExtra("timeslot", match.timeslot)
                    intent.putExtra("PositionSquare", i.toString())
                    intent.putExtra("matchId", match.matchId)
                    // Pass any necessary data to JoinMatchActivity using intent.putExtra if needed
                    holder.itemView.context.startActivity(intent)
                } else {
                    // Launch AccountActivity
                    val intent = Intent(holder.itemView.context, AccountActivity::class.java)
                    intent.putExtra("sentThroughUserId", userIdValue)
                    // Pass any necessary data to AccountActivity using intent.putExtra if needed
                    holder.itemView.context.startActivity(intent)
                }
            }

            imageLinearLayout.addView(imageView)

            // Check if username is "Default"
            if (usernameValue != null && usernameValue == "Default") {
                // Add an empty TextView for "Default" username
                val emptyUsernameTextView = TextView(holder.itemView.context)
                emptyUsernameTextView.text = "Available"
                emptyUsernameTextView.layoutParams = layoutParams

                val usernamePaddingPx = 16
                emptyUsernameTextView.setPadding(usernamePaddingPx * 4, 0, usernamePaddingPx, usernamePaddingPx)

                usernameLinearLayout.addView(emptyUsernameTextView)
            } else {
                // Add the username TextView
                val usernameTextView = TextView(holder.itemView.context)
                usernameTextView.text = usernameValue
                usernameTextView.layoutParams = layoutParams

                val usernamePaddingPx = 16
                usernameTextView.setPadding(usernamePaddingPx * 4, 0, usernamePaddingPx, usernamePaddingPx)

                usernameLinearLayout.addView(usernameTextView)
            }
        }

        holder.imagesLayout.addView(imageLinearLayout)
        holder.usernamesLayout.addView(usernameLinearLayout)
    }




    override fun getItemCount(): Int {
        return matches.size
    }
}
