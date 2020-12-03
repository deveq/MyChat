package com.soldemom.mychat.fcm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.soldemom.mychat.MainActivity
import com.soldemom.mychat.R

class ChatMessagingService : FirebaseMessagingService() {
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth

    //새 토큰이 생성될때마다 onNewToken이 호출됨. 아래와 같은 경우 호출됨
    //1. 앱에서 인스턴스ID 삭제,
    //2. 새 기기에서 앱 복원
    //3. 사용자가 앱 삭제 / 재설치
    //4. 사용자가 앱 데이터 소거
    override fun onNewToken(fcmToken: String) {
        Log.d("서비스","내 토큰 : $fcmToken")
        super.onNewToken(fcmToken)
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).update("fcmToken",fcmToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//        message?.data?.let {data ->
//
//            val builder = NotificationCompat
//                .Builder(this, "channel.parayo.default")
//                .setContentTitle(data["title"])
//                .setContentText(data["content"])
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//
//            with(NotificationManagerCompat.from(this)) {
//                notify(0,builder.build())
//            }
//
//        }
        val pushDataMap = message.data
        sendNotification(pushDataMap)

    }

    fun sendNotification(data: MutableMap<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val contentIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(data["title"])
            .setContentText(data["content"])
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        val notificationManagerCompat = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManagerCompat.notify(0, notificationBuilder.build())
    }


}