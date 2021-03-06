package com.mapmyindia.sdk.demo.kotlin.activity

import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.mapmyindia.sdk.demo.R
import com.mapmyindia.sdk.demo.kotlin.settings.MapmyIndiaPlaceWidgetSetting
import com.mapmyindia.sdk.maps.MapView
import com.mapmyindia.sdk.maps.MapmyIndiaMap
import com.mapmyindia.sdk.maps.OnMapReadyCallback
import com.mapmyindia.sdk.maps.annotations.MarkerOptions
import com.mapmyindia.sdk.maps.camera.CameraPosition
import com.mapmyindia.sdk.maps.camera.CameraUpdateFactory
import com.mapmyindia.sdk.maps.geometry.LatLng
import com.mapmyindia.sdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapmyindia.sdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapmyindia.sdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import com.mmi.services.api.OnResponseCallback
import com.mmi.services.api.autosuggest.model.ELocation
import com.mmi.services.api.hateaosnearby.MapmyIndiaHateosNearby
import com.mmi.services.api.hateaosnearby.MapmyIndiaHateosNearbyManager
import com.mmi.services.api.nearby.model.NearbyAtlasResponse
import com.mmi.services.api.nearby.model.NearbyAtlasResult
import java.util.ArrayList


class FullModeFragmentAutocompleteActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private var mapmyIndiaMap: MapmyIndiaMap? = null


    private lateinit var search: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_mode_fragment)
        mapView = findViewById(R.id.map_view)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        search = findViewById(R.id.search)
        search.setOnClickListener {
            if (mapmyIndiaMap != null) {
                val placeOptions: PlaceOptions = PlaceOptions.builder()
                    .location(MapmyIndiaPlaceWidgetSetting.instance.location)
                    .filter(MapmyIndiaPlaceWidgetSetting.instance.filter)
                    .hint(MapmyIndiaPlaceWidgetSetting.instance.hint)
                    .enableTextSearch(MapmyIndiaPlaceWidgetSetting.instance.isEnableTextSearch)
                    .pod(MapmyIndiaPlaceWidgetSetting.instance.pod)
                    .saveHistory(MapmyIndiaPlaceWidgetSetting.instance.isEnableHistory)
                    .attributionHorizontalAlignment(MapmyIndiaPlaceWidgetSetting.instance.signatureVertical)
                    .attributionVerticalAlignment(MapmyIndiaPlaceWidgetSetting.instance.signatureHorizontal)
                    .logoSize(MapmyIndiaPlaceWidgetSetting.instance.logoSize)
                    .backgroundColor(resources.getColor(MapmyIndiaPlaceWidgetSetting.instance.backgroundColor))
                    .bridge(MapmyIndiaPlaceWidgetSetting.instance.isBridgeEnable)
                    .hyperLocal(MapmyIndiaPlaceWidgetSetting.instance.isHyperLocalEnable)
                    .toolbarColor(resources.getColor(MapmyIndiaPlaceWidgetSetting.instance.toolbarColor))
                    .build()

                val placeAutocompleteFragment: PlaceAutocompleteFragment =
                    if (MapmyIndiaPlaceWidgetSetting.instance.isDefault) {
                        PlaceAutocompleteFragment.newInstance()
                    } else {
                        PlaceAutocompleteFragment.newInstance(placeOptions)
                    }
                placeAutocompleteFragment.setOnPlaceSelectedListener(object :
                    PlaceSelectionListener {
                    override fun onCancel() {
                        supportFragmentManager.popBackStack(
                            PlaceAutocompleteFragment::class.java.simpleName,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    }

                    override fun onPlaceSelected(eLocation: ELocation?) {
                        if (mapmyIndiaMap != null) {
                            mapmyIndiaMap?.clear()
                            val latLng = LatLng(
                                eLocation?.latitude?.toDouble()!!,
                                eLocation.longitude?.toDouble()!!
                            )
                            mapmyIndiaMap?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    latLng,
                                    12.0
                                )
                            )
                            mapmyIndiaMap?.addMarker(
                                MarkerOptions().position(latLng).setTitle(eLocation.placeName)
                                    .setSnippet(eLocation.placeAddress)
                            )
                        }
                        search.text = eLocation?.placeName
                        supportFragmentManager.popBackStack(
                            PlaceAutocompleteFragment::class.java.simpleName,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE
                        )
                    }

                })
                placeAutocompleteFragment.setSuggestedSearchSelectionListener {
                    callHateOs(it.hyperLink)
                }
                supportFragmentManager.beginTransaction().add(
                    R.id.fragment_container,
                    placeAutocompleteFragment,
                    PlaceAutocompleteFragment::class.java.simpleName
                )
                    .addToBackStack(PlaceAutocompleteFragment::class.java.simpleName)
                    .commit()
            } else {
                Toast.makeText(this, "Please wait map is loading", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callHateOs(hyperlink: String) {
        val hateOs = MapmyIndiaHateosNearby.builder()
            .hyperlink(hyperlink)
            .build()
        MapmyIndiaHateosNearbyManager.newInstance(hateOs).call(object :
            OnResponseCallback<NearbyAtlasResponse> {
            override fun onSuccess(nearbyAtlasResponse: NearbyAtlasResponse?) {
                if (nearbyAtlasResponse != null) {
                    val nearByList = nearbyAtlasResponse.suggestedLocations
                    if (nearByList.size > 0) {
                        addMarker(nearByList)
                    }
                } else {
                    Toast.makeText(this@FullModeFragmentAutocompleteActivity, "Not able to get value, Try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(p0: Int, p1: String?) {
                Toast.makeText(this@FullModeFragmentAutocompleteActivity, p1?:"Something went wrong", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addMarker(nearByList: ArrayList<NearbyAtlasResult>) {
        mapmyIndiaMap?.clear()
        for (marker in nearByList) {
            if (marker.getLatitude() != null && marker.getLongitude() != null) {
                mapmyIndiaMap?.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            marker.getLatitude(),
                            marker.getLongitude()
                        )
                    ).title(marker.getPlaceName())
                )
            } else {
                mapmyIndiaMap?.addMarker(
                    MarkerOptions().eLoc(
                        marker.eLoc
                    ).title(marker.getPlaceName())
                )
            }
        }
    }


    override fun onMapError(p0: Int, p1: String?) {}

    override fun onMapReady(mapmyIndiaMap: MapmyIndiaMap) {
        this.mapmyIndiaMap = mapmyIndiaMap


        mapmyIndiaMap.cameraPosition =
            CameraPosition.Builder().target(LatLng(28.0, 77.0)).zoom(4.0).build()
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
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

}
