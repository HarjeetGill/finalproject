package com.techai.shiftme.data.model

data class Request(
    var itemsToShift: ArrayList<String> = arrayListOf(),
    var pickLatitude: Double = 0.0,
    var pickLongitude: Double = 0.0,
    var destinationLatitude: Double = 0.0,
    var destinationLongitude: Double = 0.0,
    var dateAndTime: String = "",
    var vehicleType: String = "",
    var costOfShifting: String = "",
    var noOfMovers: Int = 0,
    var status: String = "",
    var userDetails: SignUpModel? = null
)
