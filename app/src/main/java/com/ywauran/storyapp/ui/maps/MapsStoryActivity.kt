package com.ywauran.storyapp.ui.maps

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.ywauran.storyapp.R
import com.ywauran.storyapp.databinding.ActivityMapsStoryBinding
import com.ywauran.storyapp.helper.Result
import com.ywauran.storyapp.ui.story.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsStoryBinding
    private val storyViewModel: StoryViewModel by viewModels()
    private val boundBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        hideActionBar()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.run {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        observeStoryLocationList()
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun observeStoryLocationList() {
        storyViewModel.getListStoryLocation().observe(this) { responseResult ->
            when (responseResult) {
                is Result.Loading -> {}
                is Result.Success -> {
                    responseResult.data?.listStory?.forEach { story ->
                        addMarker(LatLng(story.lat ?: 0.0, story.lon ?: 0.0), story.name.orEmpty())
                    }
                    animateCameraToBounds()
                }
                is Result.Error -> {
                    Toast.makeText(this, responseResult.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addMarker(latLon: LatLng, creator: String) {
        mMap.addMarker(MarkerOptions().position(latLon).title(creator))
        boundBuilder.include(latLon)
    }

    private fun animateCameraToBounds() {
        val bounds = boundBuilder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
            bounds,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels,
            300)
        )
    }

    private fun hideActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}
