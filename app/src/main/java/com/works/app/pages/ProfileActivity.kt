
package com.works.app.pages

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.works.app.R
import com.works.app.utils.LocationModel
import com.works.app.utils.LocationRepository

class ProfileActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    private lateinit var locationList: List<LocationModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mapView = findViewById(R.id.mapView)
        // Konum listesini al
        locationList = LocationRepository.getLocations()

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Harita ayarları
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        // Pinleri oluştur
        addMarkers()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun addMarkers() {

        if (locationList.isEmpty()) return

        val boundsBuilder = LatLngBounds.Builder()

        locationList.forEach { location ->

            val position = LatLng(
                location.latitude,
                location.longitude
            )
            val markerOptions = MarkerOptions()
                .position(position)
                .title(location.name)
                .snippet(location.description)
            // Haritaya pin ekle
            googleMap.addMarker(markerOptions)

            googleMap.setOnMarkerClickListener {
                Toast.makeText(this, "${it.title}", Toast.LENGTH_SHORT).show()
                true
            }

            boundsBuilder.include(position)
        }

        // Tüm pinleri ekrana sığdır
        if (locationList.size == 1) {

            val location = locationList.first()

            val position = LatLng(
                location.latitude,
                location.longitude
            )

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(position, 13f)
            )

        } else {

            val bounds = boundsBuilder.build()

            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    120
                )
            )
        }

    }
}
