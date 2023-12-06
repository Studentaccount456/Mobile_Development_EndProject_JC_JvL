package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import java.lang.reflect.Field

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize osmdroid
        Configuration.getInstance().load(applicationContext, getPreferences(MODE_PRIVATE))

        setContentView(R.layout.map_screen)

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        // Fetch and add markers for clubs from Firebase
        fetchClubsFromFirebase()

        // Add user's location overlay
        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)

        // Set the default location to Antwerp
        mapView.controller.setCenter(createGeoPoint(51.2194, 4.4025))

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                val threshold = 100 // You can adjust this value as needed
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

                // If the tapped point is not close to any marker, do nothing
                Log.d("MapActivity", "No marker tapped.")
                return false
            }



            override fun longPressHelper(p: GeoPoint): Boolean {
                // Handle long press if needed
                return false
            }
        })

        mapView.overlays.add(mapEventsOverlay)
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
                // Handle errors
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
}
