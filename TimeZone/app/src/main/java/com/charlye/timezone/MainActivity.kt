package com.charlye.timezone

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.city_row.view.*
import kotlinx.android.synthetic.main.time_row.view.cityText
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    companion object {
        var cityList : MutableList<CityClock?> = mutableListOf()
        lateinit var cityViewRecycler : RecyclerView
        lateinit var timeViewRecycler : RecyclerView
        lateinit var cAdapter: CityAdapter
        lateinit var tAdapter: TimeAdapter

    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val searchButton = findViewById(R.id.searchBtn) as Button
            val searchInput = findViewById(R.id.searchInput) as TextInputEditText

            //if search input is not empty it will cal fetchGeo() other wise it display a toast saying "search is empty"
            searchButton.setOnClickListener({
                val city = searchInput.text.toString()
                if(city.length < 1) {
                    Toast.makeText(this, "Search is empty",
                        Toast.LENGTH_LONG).show()
                } else {
                    fetchGeo(city)
                }
            })

            //Set Up City Recycler View
            cityViewRecycler = findViewById(R.id.cityRecycler) as RecyclerView
            cityViewRecycler.layoutManager = LinearLayoutManager(this )

            //Set up Time Recycler View
            timeViewRecycler = findViewById(R.id.timeRecycler) as RecyclerView
            tAdapter = TimeAdapter(cityList)
            timeViewRecycler.layoutManager = LinearLayoutManager(this )
            timeViewRecycler.adapter = tAdapter

        }


    //Notifies TimeAdapter when Data has been added to List & displays a toast
    fun updateList(city : CityClock) {
        cityList.add(city)
        runOnUiThread({
            val msg = " City was added!"
            Toast.makeText(this, msg,
                Toast.LENGTH_LONG).show();
        })

        runOnUiThread({
       tAdapter.notifyDataSetChanged()
       })
    }

    // Makes an http request to Goole Geocode API for for information on the given city (String)
    // If there are no results it will display a toast saying so, other wise it will render the search results
    fun fetchGeo( city : String)  {

        val geocodeUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="
        val geoKey = "&key=" + getString(R.string.geoKey)
        val url = geocodeUrl + city + geoKey

        val request= Request.Builder().url(url).build()
        val client = OkHttpClient()

        //if response fails
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("failed")
                e.printStackTrace()
            }

            //if response is succesfull
            override fun onResponse(call: Call, response: Response)  {
                val body = response.body()?.string()
                val gson = Gson()
                val cityResults = gson.fromJson(body, Results::class.java)

                if(cityResults.results.size < 1) {
                    runOnUiThread{
                        Toast.makeText(this@MainActivity, "City was not Found", Toast.LENGTH_LONG).show()
                    }

                } else {
                    runOnUiThread{
                        cityViewRecycler.adapter = CityAdapter(cityResults, this@MainActivity)
                    }
                }
            }
        }
        )
    }

    // This is a custom adapter for the City Recycler View
    class CityAdapter (val cityResults: Results, val context: Context ): RecyclerView.Adapter<CityViewHolder>() {

        //returns the data length
        override fun getItemCount(): Int {
            return cityResults.results.size
        }

        // returns a cityViewHolder
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CityViewHolder {
            val layoutInflater = LayoutInflater.from(p0.context )
            val row = layoutInflater.inflate(R.layout.city_row, p0, false)

            return CityViewHolder(row)
        }

        //OnBindViewHolder binds the data to the city_row layout
        //has a button and when that it is clicked it add the city to the cityList
        //thus triggering the updateList() method
        override fun onBindViewHolder(p0: CityViewHolder, p1: Int) {
            val city = cityResults.results[p1]
            val cityName = city.address_components[0].long_name
            val lat = "&lat=" + city.geometry.location.lat.toString()
            val lng = "&lng=" + city.geometry.location.lng.toString()
            p0?.itemView.cityText.text = city.formatted_address
            p0.itemView.addBtn.setOnClickListener({
                //fetchTimeZone(formatAddress, lat, lng)
                val timeZoneUrl = "http://api.timezonedb.com/v2.1/get-time-zone?"
                val meta = "&format=json&by=position"
                val timeKey = "key=" +  (context as MainActivity).getString(R.string.timeKey)
                val url = timeZoneUrl + timeKey + meta + lat + lng

                val request= Request.Builder().url(url).build()
                val client = OkHttpClient()

                client.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("failed")
                        e.printStackTrace()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        val body = response?.body()?.string()
                        val gson = Gson()
                        val timeZone = gson.fromJson(body, timeZone::class.java)
                        val timeParts = timeZone.formatted.split(" ")
                        val cityClock = CityClock(cityName, timeZone.countryCode, timeParts[1], timeZone.zoneName)

                        (context as MainActivity).updateList(cityClock)
                    }
                }
                )
            })
        }

    }
}

class CityViewHolder(v: View) : RecyclerView.ViewHolder(v) {

}








