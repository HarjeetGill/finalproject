package com.techai.shiftme.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SignUpModel(
    val fullName: String = "",
    val address: String = "",
    val password: String = "",
    val emailId: String = "",
    val phoneNumber: String = "",
    var firebaseId: String = "",
): Parcelable
