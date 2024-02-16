package com.project.expirytracker.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class DatabaseModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val name:String,
    val type:String,
    val quantity:Short,

    val mfgYear:Short,
    val mfgMonth:Byte,
    val mfgDate:Byte,

    val expYear:Short,
    val expMonth:Byte,
    val expDate:Byte,

    val itemPrice:Short
) : Parcelable


