package com.charlye.timezone

import java.io.Serializable


//Created Custom classes to be able to read ing the objects from TimeZoneDB API
//and Google Map Services GeoCode API

//Free Time Zone DB JSON Structure
class timeZone(val status : String, val message: String,
               val countryCode: String, val countryName: String, val zoneName: String, val abbreviation: String,
               val gmtOffset: String, val dst: String, val zoneStart: String, val zoneEnd: String, val nextAbbreviation: String,
               val timestamp: String, val formatted: String ) : Serializable {



}


// Google Map Services GeoCode JSON Structure
class Results (val results : List<GeoCode>, val status : String)

class GeoCode(val address_components : List<AddressComponent>, val formatted_address : String, val geometry : Geometry, val place_id : String, val types : List<String> )

class AddressComponent( val long_name : String, val short_name : String, val types : List<String>)

class Geometry(val location : Location, val location_type : String, val viewport : ViewPort)

class ViewPort(val northeast : Location, val southwest : Location )

class Location(val lat : Double, val lng: Double)



// My Custom Object for storing City Time Information
class CityClock( var city : String, var country : String, var time : String, var zone : String) : Serializable {

}

