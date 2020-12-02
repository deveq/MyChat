package com.soldemom.mychat.Model

class Chatroom() {

    var users: HashMap<String, Boolean> = hashMapOf()
    var comments: HashMap<String, Comment> = hashMapOf()

    class Comment() {
        var senderUid: String? = null
        var text: String? = null
//        var time: Long? = null
//        var timestamp: Map<String, String>? = null
    }
}