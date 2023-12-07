package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private val firestore = FirebaseFirestore.getInstance()
    private val ZOOM_REPEAT_INTERVAL = 50L
    private var isZoomingOut = false
    private var zoomOutHandler: Handler? = null

    private val zoomOutRunnable: Runnable = object : Runnable {
        override fun run() {
            if (isZoomingOut) {
                mapView.controller.zoomOut()
                zoomOutHandler?.postDelayed(this, ZOOM_REPEAT_INTERVAL)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize osmdroid
        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

        setContentView(R.layout.map_screen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED

        bottomNavigationView.menu.findItem(R.id.navigation_home).isChecked = true

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    launchActivity(HomeActivity::class.java)
                    true
                }
                R.id.navigation_court -> {
                    launchActivity(ClubActivity::class.java)
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

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        // Firebase Fetch
        fetchClubsFromFirebase()

        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)

        // Default location: Antwerp
        mapView.controller.setCenter(createGeoPoint(51.2194, 4.4025))

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                val threshold = 225
                val itemList = getOverlayItemsList()

                Log.d("MapActivity", "Tapped Point Coordinates: ${p.latitude}, ${p.longitude}")

                for (marker in itemList) {
                    Log.d("MapActivity", "Marker Coordinates: ${marker.point.latitude}, ${marker.point.longitude}")
                    val distance = p.distanceToAsDouble(marker.point)
                    Log.d("MapActivity", "Distance to $marker: $distance")
                    if (distance < threshold) {
                        // If the tapped point is close to a marker, start HomeActivity
                        Log.d("MapActivity", "Marker tapped! Starting HomeActivity.")
                        val intent = Intent(this@MapActivity, HomeActivity::class.java)
                        startActivity(intent)
                        return true
                    }
                }
                // When nothing is pressed
                Log.d("MapActivity", "No marker tapped.")
                return false
            }



            override fun longPressHelper(p: GeoPoint): Boolean {
                if (!isZoomingOut) {
                    isZoomingOut = true
                    zoomOutHandler = Handler()
                    zoomOutHandler?.postDelayed(zoomOutRunnable, ZOOM_REPEAT_INTERVAL)
                }
                return true
            }
        })

        mapView.overlays.add(mapEventsOverlay)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            // Long press ended
            stopZoomingOut()
        }
        return super.onTouchEvent(event)
    }

    private fun stopZoomingOut() {
        isZoomingOut = false
        zoomOutHandler?.removeCallbacksAndMessages(null)
        zoomOutHandler = null
    }

    private fun createGeoPoint(latitude: Double, longitude: Double): IGeoPoint {
        return GeoPoint(latitude, longitude)
    }

    private fun fetchClubsFromFirebase() {
        Log.d("MapActivity", "Here it staaaaaaaaaaaaarts")
        firestore.collection("TheClubDetails")
            .get()
            .addOnSuccessListener { result ->
                Log.d("MapActivity", "Number of documents: ${result.size()}")
                for (document in result) {
                    val clubName = document.getString("ClubName")
                    val clubLocation = document.getGeoPoint("ClubLocation")
                    if (clubLocation != null) {
                        Log.d("MapActivity", "1) Club Location - Latitude: ${clubLocation.latitude}, Longitude: ${clubLocation.longitude}, ${clubName}")
                    }

                    if (clubName != null && clubLocation != null) {
                        Log.d("MapActivity", "2) Club Location - Latitude: ${clubLocation.latitude}, Longitude: ${clubLocation.longitude}")
                        val clubMarker = OverlayItem(clubName, "Club Location", createGeoPoint(clubLocation.latitude, clubLocation.longitude))
                        val clubMarkerOverlay = ItemizedIconOverlay<OverlayItem>(
                            applicationContext,
                            listOf(clubMarker),
                            null
                        )

                        mapView.overlays.add(clubMarkerOverlay)

                        Log.d("MapActivity", "Added marker overlay for $clubName")


                        // Log statement for debugging
                        Log.d("MapActivity", "Fetched Club: $clubName, Location: $clubLocation")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapActivity", "Error fetching clubs from Firebase: $exception")
            }
    }

    private fun getOverlayItemsList(): List<OverlayItem> {
        val overlays = mapView.overlays
        val itemList = mutableListOf<OverlayItem>()

        for (overlay in overlays) {
            if (overlay is ItemizedIconOverlay<*> && overlay.size() > 0) {
                val item = overlay.getItem(0) as? OverlayItem
                if (item != null) {
                    itemList.add(item)
                }
            }
        }

        return itemList
    }




    // Forward lifecycle events to the MapView
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }

    private fun launchActivity(cls: Class<*>) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}
