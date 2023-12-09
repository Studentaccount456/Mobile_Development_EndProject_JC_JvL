package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import android.view.View
import com.example.mobile_dev_endproject_jc_jvl.R


class TesterActivity_FireStore : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tester_screenfirestore)
    }

    fun onAddReservationButtonClick(view: View) {
        // Call the FirebaseHandler to add the reservation
        addReservation()
    }


    fun addReservation() {
        val collection = "TheTestingArea"
        val documentId = "ThisIsATest"

        // Replace this with the actual date or a variable representing the date
        val dateReservation = "ReplaceWithActualDate"

        val reservationData = hashMapOf(
            "DateReservation" to dateReservation,
            "DetailsReservation" to hashMapOf(
                "MatchId" to "Default",
                "Timeslot" to "Default",
                "DetailsTimeSlot" to hashMapOf(
                    "Participators" to hashMapOf(
                        "UserName_One" to "Default",
                        "UserAvatar_One" to "Default",
                        "UserId_One" to "Default",
                        "UserName_Two" to "Default",
                        "UserAvatar_Two" to "Default",
                        "UserId_Two" to "Default",
                        "UserName_Three" to "Default",
                        "UserAvatar_Three" to "Default",
                        "UserId_Three" to "Default",
                        "UserName_Four" to "Default",
                        "UserAvatar_Four" to "Default",
                        "UserId_Four" to "Default"
                    )
                )
            )
        )

        // Check if the document exists
        val docRef = db.collection(collection).document(documentId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, update the CourtReservations field
                    db.collection(collection).document(documentId)
                        .update("CourtReservations", reservationData)
                        .addOnSuccessListener {
                            // Successfully updated
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                        }
                } else {
                    // Document doesn't exist, create a new document with CourtReservations field
                    db.collection(collection).document(documentId)
                        .set(mapOf("CourtReservations" to reservationData))
                        .addOnSuccessListener {
                            // Successfully added
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
    }
}