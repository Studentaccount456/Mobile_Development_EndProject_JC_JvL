package com.example.mobile_dev_endproject_jc_jvl.activitiesDirectory

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobile_dev_endproject_jc_jvl.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ReservationActivity : AppCompatActivity() {

    private lateinit var establishmentTextView: TextView
    private lateinit var courtNameTextView: TextView
    private var startingTime_Play: String = ""
    private var endingTime_Play: String = ""
    private lateinit var createMatchCheckBox: CheckBox
    private lateinit var orderFieldButton: Button
    private lateinit var returnButton: Button
    private lateinit var datePickerButton: Button
    private lateinit var dateTextView: TextView
    private lateinit var selectedDate: Date
    private var yearReservation: Int = 0
    private var monthReservation: Int = 0
    private var dayReservation: Int = 0
    private lateinit var timeGrid: LinearLayout
    var takenTimeSlots = mutableListOf<String>()
    private lateinit var reservedTimeText : TextView


    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reservation_screen)

        establishmentTextView = findViewById(R.id.establishmentTextView)
        courtNameTextView = findViewById(R.id.courtNameTextView)
        datePickerButton = findViewById(R.id.datePickerButton)
        dateTextView = findViewById(R.id.dateTextView)

        // Set up the initial date
        val calendar1 = Calendar.getInstance()
        calendar1.add(Calendar.DAY_OF_MONTH, 1)
        selectedDate = calendar1.time
        updateDateTextView()

        // Set up default values for year, month, and day
        setupDefaultValues()

        // Set up the DatePickerDialog
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Get the layout components (Time)
        timeGrid = findViewById(R.id.timeGrid)
        reservedTimeText = findViewById(R.id.reservedTimeText)
        // Time

        createMatchCheckBox = findViewById(R.id.createMatchCheckBox)
        orderFieldButton = findViewById(R.id.orderFieldButton)
        returnButton = findViewById(R.id.returnButton)

        // Retrieve data from the intent and set the text views accordingly
        val establishment = intent.getStringExtra("sentThroughClubEstablishment")
        val courtName = intent.getStringExtra("sentThroughCourtName")
        establishmentTextView.text = establishment
        courtNameTextView.text = courtName

        // Set up the time grid
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)

        for (i in 0 until 6) { // 6 rows for 8:00 to 19:30
            val rowLayout = LinearLayout(this)
            rowLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.orientation = LinearLayout.HORIZONTAL

            for (j in 0 until 4) {
                val startTime = sdf.format(calendar.time)
                calendar.add(Calendar.MINUTE, 30)

                val timeSquare = TextView(this)
                val params = LinearLayout.LayoutParams(
                    50, // width in dp
                    140, // height in dp
                    1f
                )
                params.setMargins(24, 24, 24, 24)
                timeSquare.layoutParams = params
                timeSquare.text = startTime
                timeSquare.tag = "$startTime"
                timeSquare.gravity = Gravity.CENTER
                timeSquare.setBackgroundResource(R.drawable.selector_time_square)
                timeSquare.setTextColor(ContextCompat.getColorStateList(this, R.color.text_color)) // Add this line
                timeSquare.setOnClickListener { view ->
                    // Clear the selection of all other time squares
                    if (timeSquare.tag != "disabled") {
                        clearSelection(timeGrid)

                        // Toggle the selection of the current time square
                        view.isSelected = !view.isSelected
                        val selectedTime = view.tag as String
                        val reservedEndTime = calculateReservedEndTime(selectedTime)
                        startingTime_Play = selectedTime;
                        endingTime_Play = reservedEndTime
                        reservedTimeText.text = "Time Reserved: $selectedTime to $reservedEndTime"
                    }

                }

                rowLayout.addView(timeSquare)
            }

            timeGrid.addView(rowLayout)
        }

        // Fetch reserved time slots and update UI
        fetchReservedTimeSlots("$yearReservation-$monthReservation-$dayReservation")


        // Set click listener for the "Order Field" button
        orderFieldButton.setOnClickListener {
            // Handle order field logic
            if (startingTime_Play == "" || endingTime_Play == "") {
                // Show a warning to the user
                // You can display a toast, dialog, or any other suitable UI element to notify the user
                // For example, using a Toast:
                findViewById<View>(R.id.redBorder).visibility = View.VISIBLE
                Toast.makeText(this, "Please select a time slot before ordering the field", Toast.LENGTH_SHORT).show()
            } else if (takenTimeSlots != null && takenTimeSlots.any { it.contains(startingTime_Play) || it.contains(endingTime_Play) }) {
                findViewById<View>(R.id.redBorder).visibility = View.VISIBLE
                Toast.makeText(this, "Times overlap! Can't make reservation!", Toast.LENGTH_SHORT).show()

            }
            else {
                findViewById<View>(R.id.redBorder).visibility = View.GONE
                orderField()
            }
        }

        // Set click listener for the "Return" button
        returnButton.setOnClickListener {
            // Handle return logic
            finish()
        }
    }

    private fun orderField() {

        // Get the current user's ID from Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = currentUser?.uid

        // Firestore reference to the user's profile details
        val userProfileRef = currentUserId?.let {
            firestore.collection("ThePlayers").document(it)
                .collection("TheProfileDetails").document()
        }

        val sanitizedClubEstablishment = intent.getStringExtra("SanitizedClubEstablishment")
        val sanitizedCourtName = intent.getStringExtra("SanitizedCourtName")
        val courtName = intent.getStringExtra("sentThroughCourtName")
        val sanitizedClubName = intent.getStringExtra("SanitizedClubName")

        val yearForFirestore = yearReservation
        val monthForFirestore = monthReservation
        val dayForFirestore = dayReservation

        val startTimeForFireStoreInsert = startingTime_Play
        val endTimeForFireStoreInsert = endingTime_Play

        val dateReservation = "$yearForFirestore-$monthForFirestore-$dayForFirestore"
        val dateReservationSanitized = formatDate(yearForFirestore, monthForFirestore, dayForFirestore)
        val timeslot = "$startTimeForFireStoreInsert-$endTimeForFireStoreInsert"
        val sanitizedStartTimeMoment = startTimeForFireStoreInsert.replace(":", "")
        val sanitizedEndTimeMoment = endTimeForFireStoreInsert.replace(":", "")
        val sanitizedTimeslot = "$sanitizedStartTimeMoment$sanitizedEndTimeMoment"

        // unique MatchId: Courtname_year_month_day_hour-begin_hour-end
        val matchId = "$courtName$dateReservationSanitized$sanitizedTimeslot"


        var username = "Default"
        var avatar = "Default"
        // Retrieve user's profile details from Firestore
        userProfileRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // User profile details exist, update reservationData with retrieved values
                username = documentSnapshot.getString("Username") ?: "Default"
                avatar = documentSnapshot.getString("Avatar") ?: "Default"

            } else {
                // User profile details do not exist, handle accordingly
                Log.e("ReservationActivity", "User profile details not found for ID: $currentUserId")
            }
        }?.addOnFailureListener { e ->
            // Handle failure
            Log.e("ReservationActivity", "Error getting user profile details", e)
        }

        // Sample data with default values (initially set to default)
        val reservationData = hashMapOf(
                "DateReservation" to dateReservation,
                "MatchId" to matchId,
                "Timeslot" to timeslot,
                "Participators" to hashMapOf(
                    "UserName_One" to username,
                    "UserAvatar_One" to avatar,
                    "UserId_One" to currentUserId,
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


        Log.d("ReservationActivity", "Data: $dateReservationSanitized, $matchId, $timeslot")
        Log.d("ReservationActivity", "Data: $sanitizedClubName, $sanitizedClubEstablishment, $sanitizedCourtName")

        // Update Firestore with the reservation data
        if (sanitizedClubName != null && sanitizedClubEstablishment != null && sanitizedCourtName != null) {
            val courtDocument = firestore.collection("TheClubDetails")
                .document(sanitizedClubName)
                .collection("TheClubEstablishments")
                .document(sanitizedClubEstablishment)
                .collection("TheClubCourts")
                .document(sanitizedCourtName)

            // Check if the "CourtReservations" field exists
            // Check if the "CourtReservations" field exists
            courtDocument.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    val existingReservations = documentSnapshot?.get("CourtReservations") as? HashMap<String, Any> ?: hashMapOf()

                    // Create or update the "DateReservation" entry with a list of reservations
                    val dateReservationData = existingReservations[dateReservation] as? ArrayList<HashMap<String, Any>> ?: arrayListOf()
                    val reservationDataJava = reservationData as HashMap<String, Any>
                    dateReservationData.add(reservationDataJava)
                    existingReservations[dateReservation] = dateReservationData

                    // Update "CourtReservations" field
                    courtDocument.update("CourtReservations", existingReservations)
                        .addOnSuccessListener {
                            Log.d("ReservationActivity", "Yeey the update was successful")
                        }
                        .addOnFailureListener { e ->
                            Log.e("ReservationActivity", "Update failed", e)
                        }
                } else {
                    Log.e("ReservationActivity", "Document check failed", task.exception)
                }
            }
        }
    }


    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

// Function to clear the selection of all time squares in the grid
private fun clearSelection(parentLayout: LinearLayout) {
    for (i in 0 until parentLayout.childCount) {
        val rowLayout = parentLayout.getChildAt(i) as LinearLayout
        for (j in 0 until rowLayout.childCount) {
            val timeSquare = rowLayout.getChildAt(j) as TextView
            timeSquare.isSelected = false
        }
    }
}

private fun calculateReservedEndTime(selectedTime: String): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val startTime = sdf.parse(selectedTime)
    calendar.time = startTime
    calendar.add(Calendar.MINUTE, 90)

    return sdf.format(calendar.time)
}
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                selectedDate = selectedCalendar.time
                yearReservation = year
                monthReservation = month
                dayReservation = dayOfMonth
                updateDateTextView()
                enableAllTimeSlots(timeGrid)
                fetchReservedTimeSlots("$yearReservation-$monthReservation-$dayReservation")
            },
            yearReservation,
            monthReservation,
            dayReservation
        )

        // Set the minimum and maximum date
        calendar.add(Calendar.DAY_OF_MONTH, 30) // 31 days in the future
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun updateDateTextView() {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)
        dateTextView.text = "Date picked: $formattedDate"
    }

    private fun setupDefaultValues() {
        val defaultCalendar = Calendar.getInstance()
        defaultCalendar.add(Calendar.DAY_OF_MONTH, 1)
        yearReservation = defaultCalendar.get(Calendar.YEAR)
        monthReservation = defaultCalendar.get(Calendar.MONTH)
        dayReservation = defaultCalendar.get(Calendar.DAY_OF_MONTH)
    }


    fun fetchTimeSlots(
        sanitizedClubName: String,
        sanitizedClubEstablishment: String,
        sanitizedCourtName: String,
        dateToFetchLeTimeSlots: String,
        onTimeSlotsFetched: (List<String>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val firestore = FirebaseFirestore.getInstance()

        // Reference to the CourtReservations collection
        val courtReservationsRef = firestore.collection("TheClubDetails")
            .document(sanitizedClubName)
            .collection("TheClubEstablishments")
            .document(sanitizedClubEstablishment)
            .collection("TheClubCourts")
            .document(sanitizedCourtName)

        // Fetch the CourtReservations document
        courtReservationsRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val timeSlots = mutableListOf<String>()

                if (documentSnapshot.exists()) {
                    // Extract the CourtReservations map field
                    val courtReservations = documentSnapshot.get("CourtReservations") as? Map<*, *>
                    Log.d("ReservationActivity", "0) Fetch Successful $courtReservations")

                    // Check if DetailsReservation exists
                    val detailsReservations = courtReservations?.get(dateToFetchLeTimeSlots) as? List<Map<*, *>>
                    Log.d("ReservationActivity", "0.1) Fetch Successful $detailsReservations")

                    // Iterate over the entries in the DetailsReservations list
                    detailsReservations?.forEach { details ->
                        // Extract Timeslot if it exists
                        val timeslot = details["Timeslot"] as? String
                        if (timeslot != null) {
                            Log.d("ReservationActivity", "1) Fetch Successful: $timeslot")
                            timeSlots.add(timeslot)
                        }

                    }

                    // Disable reserved time slots in the UI
                    Log.d("ReservationActivity", "2.0) Reaches here?")
                    disableReservedTimeSlots(timeGrid, timeSlots)
                }

                // Invoke the callback with the list of time slots
                onTimeSlotsFetched(timeSlots)
            }
            .addOnFailureListener { e ->
                // Invoke the error callback in case of failure
                onError(e)
            }
    }

    private fun fetchReservedTimeSlots(dateToFetchLeTimeSlots : String) {
        val sanitizedClubName = intent.getStringExtra("SanitizedClubName")
        val sanitizedClubEstablishment = intent.getStringExtra("SanitizedClubEstablishment")
        val sanitizedCourtName = intent.getStringExtra("SanitizedCourtName")

        if (sanitizedClubName != null) {
            if (sanitizedClubEstablishment != null) {
                if (sanitizedCourtName != null) {
                    fetchTimeSlots(sanitizedClubName, sanitizedClubEstablishment, sanitizedCourtName, dateToFetchLeTimeSlots,
                        onTimeSlotsFetched = { reservedTimeSlots ->
                            Log.d("ReservationActivity", "3) reservedTimeSlots: $reservedTimeSlots")
                            updateReservedTimeSlotsUI(reservedTimeSlots)
                        },
                        onError = { e ->
                            Log.e("ReservationActivity", "Error fetching reserved time slots", e)
                        }
                    )
                }
            }
        }
    }

    private fun updateReservedTimeSlotsUI(reservedTimeSlots: List<String>) {
        Log.d("ReservationActivity", "4)  UpdateUI called")
        val timeGrid: LinearLayout = findViewById(R.id.timeGrid)

        for (i in 0 until timeGrid.childCount) {
            val rowLayout = timeGrid.getChildAt(i) as LinearLayout
            for (j in 0 until rowLayout.childCount) {
                val timeSquare = rowLayout.getChildAt(j) as TextView
                val timeSlot = timeSquare.text.toString()

                // Disable the time square if the time slot is reserved
                timeSquare.isEnabled = !reservedTimeSlots.contains(timeSlot)
            }
        }
    }

    private fun disableReservedTimeSlots(parentLayout: LinearLayout, reservedTimeSlots: List<String>) {
        val updatedReservedTimeSlots = mutableListOf<String>()
        Log.d("ReservationActivity", "before Show list:$reservedTimeSlots")

        for (i in 0 until reservedTimeSlots.size) {
            val timeSlot = reservedTimeSlots[i]
            val startTime = timeSlot.split("-")[0].trim()
            val endTime = timeSlot.split("-")[1].trim()

            // Add the original time slot to the updated list
            updatedReservedTimeSlots.add(startTime)

            if (endTime.endsWith("30")) {
                // If the ending timeslot ends with "30"
                val hour = endTime.split(":")[0].toInt()
                var firstTime: String = ""
                var secondTime: String = ""
                if (hour <= 10) {
                    firstTime = "0${hour - 1}:30"
                    secondTime = "0${hour}:00"
                } else {
                    firstTime = "${hour - 1}:30"
                    secondTime = "${hour}:00"
                }

                // Add the new timeslots to the updatedReservedTimeSlots list
                updatedReservedTimeSlots.add("$firstTime-$secondTime")
            } else if (endTime.endsWith("00")) {
                // If the ending timeslot ends with "00"
                val hour = endTime.split(":")[0].toInt() - 1
                var firstTime: String = ""
                var secondTime: String = ""
                if (hour <= 10) {
                    firstTime = "0$hour:00"
                    secondTime = "0$hour:30"
                } else {
                    firstTime = "$hour:00"
                    secondTime = "$hour:30"
                }

                // Add the new timeslots to the updatedReservedTimeSlots list
                updatedReservedTimeSlots.add("$firstTime-$secondTime")
            }
        }

        Log.d("ReservationActivity", "Show list:$updatedReservedTimeSlots")
        if (updatedReservedTimeSlots != null) {
            takenTimeSlots = updatedReservedTimeSlots
        } else {
            // Handle the case where updatedReservedTimeSlots is null
            // You might want to assign a default value or take some other action
        }
        // Apply the disabled state to the UI
        for (i in 0 until parentLayout.childCount) {
            val rowLayout = parentLayout.getChildAt(i) as LinearLayout
            for (j in 0 until rowLayout.childCount) {
                val timeSquare = rowLayout.getChildAt(j) as TextView
                val time = timeSquare.text.toString()

                Log.d("ReservationActivity", "Which tags are applied? ${timeSquare.tag}")

                if (updatedReservedTimeSlots.any { it.contains(time) }) {
                    timeSquare.isEnabled = false
                    timeSquare.setBackgroundResource(R.drawable.selector_disabled_time_square)
                    timeSquare.tag = "disabled"
                }
            }
        }
    }

    private fun enableAllTimeSlots(parentLayout: LinearLayout) {
        // Enable all time squares in the UI

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)

        for (i in 0 until parentLayout.childCount) {
            val rowLayout = parentLayout.getChildAt(i) as LinearLayout
            for (j in 0 until rowLayout.childCount) {
                val timeSquare = rowLayout.getChildAt(j) as TextView
                val startTime = sdf.format(calendar.time)

                timeSquare.isEnabled = true
                timeSquare.setBackgroundResource(R.drawable.selector_time_square)
                timeSquare.tag = startTime
                timeSquare.isSelected = false // Clear selection

                calendar.add(Calendar.MINUTE, 30)
            }
        }
        reservedTimeText.text = "" // Set reservedTimeText to an empty string
        startingTime_Play = ""
        endingTime_Play = ""
    }

}

