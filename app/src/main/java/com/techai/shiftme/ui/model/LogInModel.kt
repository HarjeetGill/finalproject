package com.techai.shiftme.ui.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogInModel(
    val password: String,
    val emailId: String
): Parcelable
