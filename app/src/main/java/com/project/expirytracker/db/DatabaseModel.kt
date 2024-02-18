package com.project.expirytracker.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class DatabaseModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val name:String,
    val type:String,
    var quantity:Short,

    val mfgYear:Short,
    val mfgMonth:Byte,
    val mfgDate:Byte,

    val expYear:Short,
    val expMonth:Byte,
    val expDate:Byte,

    val itemPrice:Short,
    var arrayData: Array<Int> = arrayOf()
) : Parcelable


fun convertIntArrayToString(array: Array<Int>): String {
    return Gson().toJson(array)
}

fun convertStringToIntArray(string: String): Array<Int> {
    return Gson().fromJson(string, object : TypeToken<Array<Int>>() {}.type)
}

// Step 3: Use TypeConverters to convert the array data to and from its stored format
class Converters {
    @TypeConverter
    fun fromIntArray(array: Array<Int>): String {
        return convertIntArrayToString(array)
    }

    @TypeConverter
    fun toIntArray(string: String): Array<Int> {
        return convertStringToIntArray(string)
    }
}