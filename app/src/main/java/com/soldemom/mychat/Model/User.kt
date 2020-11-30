package com.soldemom.mychat.Model

class User(var uid: String, var name: String) {
    var id: String? = null
    var introduce: String? = null
    var image: String? = null
    var friendsList = mutableListOf<String>()
    var chatList = mutableListOf<String>()

    constructor() : this("","")

}
