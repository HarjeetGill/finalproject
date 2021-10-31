package com.techai.shiftme.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Request(
    var itemsToShift: ArrayList<String> = arrayListOf(),
    var agencyFirebaseIds: ArrayList<String> = arrayListOf(),
    var agencyUpdates: ArrayList<AgencyModel> = arrayListOf(),
    var pickLatitude: Double = 0.0,
    var pickLongitude: Double = 0.0,
    var destinationLatitude: Double = 0.0,
    var destinationLongitude: Double = 0.0,
    var dateAndTime: String = "",
    var vehicleType: String = "",
    var costOfShifting: String = "",
    var noOfMovers: Int = 0,
    var status: String = "",
    var distance: String = "",
    var firebaseId: String = "",
    var requestId: String = "",
    var agencyFirebaseId: String = "",
    var userDetails: SignUpModel? = null,
    var agencyDetails: SignUpModel? = null
) : Parcelable

@Parcelize
data class AgencyModel(
    var firebaseId: String = "",
    var status: String = ""
) : Parcelable

@Parcelize
data class LocationUpdates(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : Parcelable
