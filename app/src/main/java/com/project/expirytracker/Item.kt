package com.project.expirytracker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Item (
    val ItemType:String,
    val Quantity:Int,
    val MfgDate:Date,
    val ExpiresOn:Date,
    val OriginalPrice:String
) : Parcelable