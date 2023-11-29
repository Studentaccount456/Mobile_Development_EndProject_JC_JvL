package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class FieldActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.field_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Right Icon active
        bottomNavigationView.menu.findItem(R.id.navigation_field).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }
                R.id.navigation_field -> {
                    launchActivity(FieldActivity::class.java)
                    true
                }
                R.id.navigation_match -> {
                    launchActivity(MatchActivity::class.java)
                    true
                }
                R.id.navigation_account -> {
                    item.isChecked = true
                    launchActivity(AccountActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}