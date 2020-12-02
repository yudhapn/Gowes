package id.forum.gowes.notification

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.KoinComponent
import org.koin.core.inject

@ExperimentalCoroutinesApi
class NotificationService : FirebaseMessagingService(), KoinComponent {
//   private val chatRepository: ChatRepository by inject()
   private val auth: FirebaseAuth by inject()

   override fun onMessageReceived(remoteMessage: RemoteMessage) {
      super.onMessageReceived(remoteMessage)
      val data = remoteMessage.data
//        val user = FirebaseAuth.getInstance().currentUser
        Log.d("NotificationService", "Message Notification Body: $data")
      val notification = Notification(
         body = data.get("body") ?: "",
         title = data.get("title") ?: "",
         receiverId = data.get("receiverId") ?: "",
         lastText = data.get("lastText") ?: "",
         idChat = data.get("type") ?: ""
      )
      Log.d("NotificationService", "Message Notification Body: " + notification.lastText)
      if (notification.receiverId.equals("yudha")) {
//         CoroutineScope(Dispatchers.IO).launch {
//            chatRepository.getUserChats()
//            chatRepository.getMessages(notification.idChat)
//         }
         val title = notification.title
         val body = notification.body

         NotificationHelper.run { displayNotification(applicationContext, title, body) }
         // Check if message contains a notification_nav payload.
         if (remoteMessage.notification != null) {
         }

      }
   }

   override fun onNewToken(p0: String) {
      super.onNewToken(p0)
      val user = auth.currentUser
      val refreshToken = FirebaseInstanceId.getInstance().token
      user?.let {
         updateToken(refreshToken)
      }
   }

   private fun updateToken(refreshToken: String?) {
      val user = auth.currentUser
   }
}