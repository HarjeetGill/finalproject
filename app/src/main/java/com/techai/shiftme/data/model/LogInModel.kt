package com.techai.shiftme.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogInModel(
    val password: String,
    val emailId: String
): Parcelable
