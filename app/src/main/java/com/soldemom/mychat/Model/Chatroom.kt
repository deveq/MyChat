package com.soldemom.mychat.Model

class Chatroom() {

    var users: HashMap<String, Boolean> = hashMapOf()
    var comments: HashMap<String, Comment> = hashMapOf()
    //채팅목록에서 채팅을 정렬하기 위해서 timestamp를 넣어줌.
    var timestamp: Any? = null

    class Comment() {
        var senderUid: String? = null
        var text: String? = null
//        var time: Long? = null
        var timestamp: Any? = null
        var readUsers = hashMapOf<String, Any>()


        companion object {
            const val CHAT_LEFT = 0
            const val CHAT_RIGHT = 1
            const val TIME = 2
        }
    }
}