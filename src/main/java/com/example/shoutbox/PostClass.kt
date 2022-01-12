package com.example.shoutbox


class Posts(
    val content:String,
    val login:String,
    val date:String,
    val id:String) {

    fun changeDate(): String {
        val data = date.split("T",".")
        val dateFormat = data[0] +" "+ data[1]
        return dateFormat
    }

    override fun toString(): String {
        return "$content $login $date"
    }
}