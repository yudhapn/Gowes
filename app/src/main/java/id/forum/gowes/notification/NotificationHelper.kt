package id.forum.gowes.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_VIBRATE
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import id.forum.gowes.MainActivity
import id.forum.gowes.R


class NotificationHelper {

    companion object NotificationApplication {
        private const val CHANNEL_ID = "my_channel_01"
        private const val CHANNEL_NAME = "Gowes Forum"
        private const val CHANNEL_DESC = "Gowes Forum Notification"
        private var countNotif = 0

        fun displayNotification(context: Context, title: String, body: String) {
            val summaryId = 0
            val notifyId = (0..40).random()
            countNotif++

            val image = R.drawable.avatar_0

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val groupOrder = "com.forum.gowes.post"

            Glide.with(context)
                .asBitmap()
                .load(image)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel =
                                NotificationChannel(
                                    CHANNEL_ID,
                                    CHANNEL_NAME,
                                    NotificationManager.IMPORTANCE_HIGH
                                )
                            channel.description = CHANNEL_DESC
                            channel.setShowBadge(true)
                            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                            val manager = context.getSystemService(NotificationManager::class.java)
                            manager?.createNotificationChannel(channel)
                        }
                        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(resource)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setContentIntent(pendingIntent)
                            .setDefaults(DEFAULT_VIBRATE)
                            .setAutoCancel(true)
                            .setGroup(groupOrder)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                            .setPriority(NotificationCompat.PRIORITY_MAX)

                        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setContentTitle(title)
                            .setContentText(body)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setStyle(
                                NotificationCompat.InboxStyle()
                                    .setSummaryText("$countNotif Notifications")
                            )
                            .setGroup(groupOrder)
                            .setGroupSummary(true)
                            .build()


                        val notifManager = NotificationManagerCompat.from(context)
                        notifManager.notify(notifyId, builder.build())
                        notifManager.notify(summaryId, summaryNotification)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        }
    }
}