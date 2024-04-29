package com.oguzavanoglu.kotlinmaps.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.oguzavanoglu.kotlinmaps.R
import com.oguzavanoglu.kotlinmaps.databinding.ActivityMapsBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncer : ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var trackBoolen : Boolean? = null
    private var selectedLatitude : Double? = null
    private var selectedLongitude: Double? = null
    private lateinit var db : PlaceDatabase
    private lateinit var placeDao: PlaceDao
    val compositeDisposable = CompositeDisposable()//Kullan at
    var placeFromMainActivity : Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLauncher()

        sharedPreferences = this.getSharedPreferences("com.oguzavanoglu.kotlinmaps", MODE_PRIVATE)
        trackBoolen = false


        selectedLatitude=0.0
        selectedLongitude=0.0

        //binding.saveButton.isEnabled = false

        db = Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()

        placeDao = db.PlaceDao()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latlng ->
            mMap.clear()//Daha önce eklenmiş marker varsa onu silecek

            mMap.addMarker(MarkerOptions().position(latlng))

            selectedLatitude = latlng.latitude
            selectedLongitude = latlng.longitude
        }

        val intent = intent

        val info = intent.getStringExtra("info")

        if (info.equals("new")){

            binding.saveButton.visibility = View.VISIBLE
            binding.deleteButton.visibility = View.GONE

            locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

            locationListener = object : LocationListener{
                override fun onLocationChanged(location: Location) {

                    trackBoolen = sharedPreferences.getBoolean("trackBoolen",false)

                    if (!trackBoolen!!){
                        val userLocation = LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                        sharedPreferences.edit().putBoolean("trackBoolen",true).apply()
                    }

                }

            }

            if (ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                //  Permission denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION)){
                    //Request permission

                    Snackbar.make(binding.root,"Permission needed for Location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        //Request permission
                        permissionLauncer.launch(Manifest.permission.ACCESS_FINE_LOCATION)

                    }.show()
                }else{
                    //Request Permission
                    permissionLauncer.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

            }else{
                //Permission granted

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)

                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null){

                    val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                }

                mMap.isMyLocationEnabled = true //Benim konumumu etkinleştirdik mi? Bunu iznimiz varken yapıyoruz. Mavi nokta çıkıyor
            }


            /*
            //latitude , longitude

            //Lat->48.85391 , Lng->2.2913515

            val eiffel = LatLng(48.85391,2.2913515)
            mMap.addMarker(MarkerOptions().position(eiffel).title("Eiffel Tower"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel,15f))


             */

        }else{

            mMap.clear()
            placeFromMainActivity = intent.getSerializableExtra("selectedPlace") as? Place

            placeFromMainActivity?.let {

                val latlng = LatLng(it.latitude,it.longitude)

                mMap.addMarker(MarkerOptions().position(latlng).title(it.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,15f))

                binding.placeNameText.setText(it.name)
                binding.saveButton.visibility = View.GONE
                binding.deleteButton.visibility = View.VISIBLE

            }


        }

    }

    private fun registerLauncher(){


        permissionLauncer = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->

            if (result){
                //Permission granted
                if (ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                    //Permission granted
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)

                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (lastLocation != null){

                        val lastUserLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15f))
                    }

                    mMap.isMyLocationEnabled = true
                }

            }else{
                //Permission denied
                Toast.makeText(this@MapsActivity,"Permission needed",Toast.LENGTH_LONG).show()
            }

        }

    }
/*
    override fun onMapClick(latlng: LatLng) {

        mMap.clear()//Daha önce eklenmiş marker varsa onu silecek

        mMap.addMarker(MarkerOptions().position(latlng))

        selectedLatitude = latlng.latitude
        selectedLongitude = latlng.longitude

        binding.save.isEnabled = true


    }

 */

    fun save(view : View){

        if (selectedLongitude !=null && selectedLatitude!= null){

            val place = Place(binding.placeNameText.text.toString(),selectedLatitude!!,selectedLongitude!!)

            compositeDisposable.add(
                placeDao.insert(place)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )

        }
    }
    private fun handleResponse(){

        val intent = Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun delete(view : View){

        placeFromMainActivity?.let {
            compositeDisposable.add(
                placeDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }

    }


    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }



}