package com.soldemom.mychat.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.soldemom.mychat.SplashActivity

class ChatMessagingService : FirebaseMessagingService() {
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth

    //새 토큰이 생성될때마다 onNewToken이 호출됨. 아래와 같은 경우 호출됨
    //1. 앱에서 인스턴스ID 삭제,
    //2. 새 기기에서 앱 복원
    //3. 사용자가 앱 삭제 / 재설치
    //4. 사용자가 앱 데이터 소거
    override fun onNewToken(fcmToken: String) {
        Log.d("서비스", "내 토큰 : $fcmToken")
        super.onNewToken(fcmToken)
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).update("fcmToken", fcmToken)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        sendNotification(message.data)
    }

    fun sendNotification(data: MutableMap<String, String>) {
        val title = data["title"]
        val content = data["content"]

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder : NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("LoginActivity", "if 제목 : ${data["title"]}, 내용 : ${data["content"]}")

            val channelId = "one-channel"
            val channelName = "My channel one"
            val channelDescription = "My Channel One Description"
            val channel = NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = channelDescription

            manager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(this, channelId)
        } else {
            Log.d("LoginActivity", "else 제목 : ${data["title"]}, 내용 : ${data["content"]}")

            builder = NotificationCompat.Builder(this)
        }

        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, SplashActivity::class.java),0)

        builder.setContentIntent(contentIntent)


        builder.setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentTitle(title)
            .setWhen(System.currentTimeMillis())
            .setContentText(content)
            .setAutoCancel(true)
        Log.d("LoginActivity", "밑에 제목 : ${data["title"]}, 내용 : ${data["content"]}")


        manager.notify(222, builder.build())
    }


}