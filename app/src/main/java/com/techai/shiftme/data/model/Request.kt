package com.techai.shiftme.data.model

data class Request(
    var itemsToShift: List<String>,
    var pickLatitude: Double = 0.0,
    var pickLongitude: Double = 0.0,
    var destinationLatitude: Double = 0.0,
    var destinationLongitude: Double = 0.0,
    var dateAndTime: String,
    var vehicleType: String,
    var costOfShifting: String,
    var noOfMovers: Int,
    var status: String
)
