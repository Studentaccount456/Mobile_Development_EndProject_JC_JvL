package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_dev_endproject_jc_jvl.R
import com.example.mobile_dev_endproject_jc_jvl.adaptersDirectory.MatchAdapter
import com.example.mobile_dev_endproject_jc_jvl.dataClassesDirectory.Match
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MatchActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.match_screen)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        firestore.collection("TheMatches")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val matches = mutableListOf<Match>()

                for (document in querySnapshot.documents) {
                    val match = document.toObject(Match::class.java)
                    Log.d("MatchActivity", "$match")
                    match?.let {
                        matches.add(it)
                    }
                }

                displayMatches(matches)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching data: $exception")
            }
    }

    private fun displayMatches(matches: List<Match>) {
        val adapter = MatchAdapter(matches)
        recyclerView.adapter = adapter
    }
}