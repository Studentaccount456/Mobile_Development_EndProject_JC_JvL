package com.example.mobile_dev_endproject_jc_jvl.ActivitiesDirectory

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
// New
import androidx.compose.runtime.*
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// New
import com.example.mobile_dev_endproject_jc_jvl.ui.theme.Mobile_Dev_EndProject_JC_JvLTheme
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mobile_Dev_EndProject_JC_JvLTheme {
                // Create a nav controller
                val navController = rememberNavController()

                // Set up the navigation graph
                NavHost(navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable("login") {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    // State to hold the value fetched from Firestore
    var firestoreData by remember { mutableStateOf("Loading...") }

    // Fetch data from Firestore when the composable is first recomposed
    LaunchedEffect(key1 = Unit) {
        fetchDataFromFirestore { data ->
            firestoreData = data ?: "Failed to fetch data"
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display Firestore data above the button
        Text(
            text = "Firestore Data: $firestoreData",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = { navController.navigate("login") }) {
            Text("Go to Login Screen")
        }
    }
}

// Function to fetch data from Firestore
private fun fetchDataFromFirestore(onSuccess: (String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("TestingPurposes").document("LeTest")

    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val testingName = document.getString("TestingName")
                onSuccess(testingName)
            } else {
                onSuccess(null)
            }
        }
        .addOnFailureListener { e ->
            onSuccess(null)
        }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Mobile_Dev_EndProject_JC_JvLTheme {
        Greeting("Android")
    }
}