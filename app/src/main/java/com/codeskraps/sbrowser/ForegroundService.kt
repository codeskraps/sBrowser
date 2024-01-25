package com.codeskraps.sbrowser

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codeskraps.sbrowser.feature.webview.media.MediaWebView
import com.codeskraps.sbrowser.util.BackgroundStatus
import com.codeskraps.sbrowser.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service() {
    companion object {
        private val TAG = ForegroundService::class.java.simpleName
        private const val NOTIF_ID = 1
        private const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val DELETE_EXTRA = "deleteExtra"
        private const val HOME_EXTRA = "homeExtra"
        private const val REFRESH_EXTRA = "refreshExtra"
    }

    @Inject
    lateinit var backgroundStatus: BackgroundStatus

    @Inject
    lateinit var mediaWebView: MediaWebView

    @Inject
    lateinit var mediaWebViewPreferences: MediaWebViewPreferences

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        mediaWebView.setUrlListener { url ->
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).run {
                notify(NOTIF_ID, createNotification(url))
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.v(TAG, "onStartCommand")

        var url = ""

        intent?.extras?.let {
            if (it.getBoolean(DELETE_EXTRA, false)) {
                stopSelf()
                return START_NOT_STICKY

            } else if (it.getBoolean(HOME_EXTRA, false)) {
                mediaWebView.loadUrl(Constants.home)
                return START_NOT_STICKY

            } else if (it.getBoolean(REFRESH_EXTRA, false)) {
                mediaWebView.reload()
                return START_NOT_STICKY

            } else {
                url = it.getString(Constants.inputExtra) ?: url
            }
        }

        createNotificationChannel()
        startForeground(NOTIF_ID, createNotification(url))
        backgroundStatus.setValue(true)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundStatus.setValue(false)
        mediaWebView.setUrlListener(null)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground MediaWebView Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService(NotificationManager::class.java).run {
            createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(contentText: String): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(if (mediaWebViewPreferences.showUrl) contentText else "Keep notification to play while the app is on the background.")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(contentPendingIntent())
            .setDeleteIntent(deletePendingIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_home, "Home", homePendingIntent())
            .addAction(R.drawable.ic_refresh, "Refresh", refreshPendingIntent())
            .build()

    private fun contentPendingIntent(): PendingIntent = PendingIntent.getActivity(
        this,
        2,
        Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        },
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun deletePendingIntent(): PendingIntent = PendingIntent.getService(
        this,
        3,
        Intent(this, ForegroundService::class.java).apply {
            putExtra(DELETE_EXTRA, true)
        },
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun homePendingIntent(): PendingIntent = PendingIntent.getService(
        this,
        4,
        Intent(this, ForegroundService::class.java).apply {
            putExtra(HOME_EXTRA, true)
        },
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun refreshPendingIntent(): PendingIntent = PendingIntent.getService(
        this,
        5,
        Intent(this, ForegroundService::class.java).apply {
            putExtra(REFRESH_EXTRA, true)
        },
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}