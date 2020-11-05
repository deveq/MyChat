package com.soldemom.mychat.Model

class Profile {

    constructor(nickname:String, uid:String) {
        this.nickname = nickname
        this.uid = uid
    }

    constructor() {

    }

    lateinit var nickname: String
    lateinit var uid: String
    var text: String? = null
    var image: String? = null
}