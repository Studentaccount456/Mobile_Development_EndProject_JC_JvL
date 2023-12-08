package com.example.mobile_dev_endproject_jc_jvl

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
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
                    launchActivity(ClubEstablishmentsActivity::class.java)
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

        fetchClubsFromFirebase()

        val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)


        val mapCoordinates = intent.getParcelableExtra<GeoPoint>("TheMapCoordinates")
        if (mapCoordinates != null) {
            // If "TheMapCoordinates" is present, set the map location to the geopoint
            setMapLocation(mapCoordinates)
        } else {
            // Default location: Antwerp
            mapView.controller.setCenter(createGeoPoint(51.2194, 4.4025))
        }

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                val threshold = 225
                val itemList = getOverlayItemsList()

                for (marker in itemList) {
                    val distance = p.distanceToAsDouble(marker.point)
                    if (distance < threshold) {
                        // If the tapped point is close to a marker, start HomeActivity
                        launchEstablishmentDetailsActivity(marker)
                        return true
                    }
                }
                // When nothing is pressed
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                if (!isZoomingOut) {
                    isZoomingOut = true
                    zoomOutHandler = Handler(mainLooper)
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
        firestore.collection("TheClubDetails")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val clubName = document.id

                    firestore.collection("TheClubDetails").document(clubName).collection("TheClubEstablishments")
                        .get()
                        .addOnSuccessListener { establishmentResult ->
                            for (establishmentDocument in establishmentResult) {
                                val clubEstablishmentName = establishmentDocument.getString("ClubEstablishmentName")
                                val clubEstablishmentAddress = establishmentDocument.getString("ClubEstablishmentAddress")
                                val clubEstablishmentLocation = establishmentDocument.getGeoPoint("ClubEstablishmentLocation")

                                if (clubEstablishmentName != null && clubEstablishmentLocation != null) {
                                    val clubMarker = CustomOverlayItem(
                                        clubEstablishmentName,
                                        "Club Location",
                                        createGeoPoint(clubEstablishmentLocation.latitude, clubEstablishmentLocation.longitude)
                                    )
                                    // Attach extra values
                                    clubMarker.extraData = createMarkerExtrasData(clubName, clubEstablishmentAddress, clubEstablishmentName)

                                    val clubMarkerOverlay = ItemizedIconOverlay<OverlayItem>(
                                        applicationContext,
                                        listOf(clubMarker),
                                        null
                                    )
                                    mapView.overlays.add(clubMarkerOverlay)
                                }
                            }
                            // Force redraw of the map
                            mapView.invalidate()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("MapActivity", "Error fetching establishments from Firebase: $exception")
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapActivity", "Error fetching clubs from Firebase: $exception")
            }
    }

    private fun launchEstablishmentDetailsActivity(marker: OverlayItem) {
        val intent = Intent(this@MapActivity, EstablishmentDetailsActivity::class.java)

        if (marker is CustomOverlayItem) {
            val extrasData = marker.extraData
            intent.putExtra("ClubName", extrasData["ClubName"])
            intent.putExtra("ClubEstablishmentAddress", extrasData["ClubEstablishmentAddress"])
            intent.putExtra("EstablishmentName", extrasData["EstablishmentName"])
        }

        intent.putExtra("TheMapCoordinates", marker.point as Parcelable)

        startActivity(intent)
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

    private fun setMapLocation(geopoint: GeoPoint) {
        mapView.controller.setCenter(geopoint)
    }

    private fun createMarkerExtrasData(clubName: String, establishmentAddress: String?, establishmentName: String?): HashMap<String, String?> {
        val extrasData = HashMap<String, String?>()
        extrasData["ClubName"] = clubName
        extrasData["ClubEstablishmentAddress"] = establishmentAddress
        extrasData["EstablishmentName"] = establishmentName

        return extrasData
    }
}
