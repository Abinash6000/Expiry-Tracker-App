package com.project.expirytracker

data class ItemModel(
    val id:Int,
    val name:String,
    val price:Short,
    val year:Short,
    val month:Byte,
    val date:Byte
)
