package com.bangkit.storyapplicationgagas.View.UI.Maps

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bangkit.storyapplicationgagas.Data.Repository.StoryRepository
import com.bangkit.storyapplicationgagas.R
import com.bangkit.storyapplicationgagas.View.ViewModelFactory

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapViewModelFactory: ViewModelFactory

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Menambahkan observer untuk data story
        mapViewModel.stories.observe(viewLifecycleOwner) { data ->
            val boundsBuilder = LatLngBounds.Builder()  // Untuk menambahkan batas peta

            // Menambahkan marker berdasarkan data
            data.forEach { story ->
                val latLng = LatLng(
                    story.lat ?: 0.0,  // Jika lat null, gunakan 0.0 sebagai default
                    story.lon ?: 0.0   // Jika lon null, gunakan 0.0 sebagai default
                )
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(story.name)
                        .snippet(story.description)
                )

                boundsBuilder.include(latLng)  // Menambahkan lat/lon ke bounds
            }



            // Menyesuaikan tampilan peta dengan marker yang ditambahkan
            val bounds = boundsBuilder.build()
            val padding = 100 // Padding untuk kamera
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.moveCamera(cameraUpdate)
        }

        // Memulai pemanggilan data story
        mapViewModel.getStoriesWithLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel dan Factory
        mapViewModelFactory = ViewModelFactory.getInstance(requireContext())
        mapViewModel = ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)

        // Menyambungkan peta
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}