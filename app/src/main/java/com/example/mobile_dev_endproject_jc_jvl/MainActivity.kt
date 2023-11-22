package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// New
import com.example.mobile_dev_endproject_jc_jvl.ui.theme.Mobile_Dev_EndProject_JC_JvLTheme

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
                        // No need to define LoginScreen here
                        // You can directly navigate to LoginActivity
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Greeting("Android")

        // Button to navigate to the login screen
        Button(onClick = { navController.navigate("login") }) {
            Text("Go to Login Screen")
        }
    }
}

@Composable
fun LoginScreen() {
    // Your login screen composable content goes here
    Text(text = "Login Screen")
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