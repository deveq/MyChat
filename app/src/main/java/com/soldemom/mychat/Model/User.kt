package com.soldemom.mychat.Model

import android.os.Parcelable
import java.io.Serializable

data class User(var uid: String, var name: String) : Serializable {
    var id: String? = null
    var introduce: String? = null
    var image: String? = null
    var friendsList = mutableListOf<String>()
    var chatList = mutableListOf<String>()

    constructor() : this("","")

}
