package com.musicplayer.kathavichar.repositories.musicPlayer
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.musicplayer.kathavichar.view.MainActivity

class MediaService : MediaSessionService() {
    private var _mediaSession: MediaSession? = null
    val mediaSession get() = _mediaSession!!

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "session_notification_channel_id"
        private val immutableFlag = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0
    }

    /**
     * This method is called when the service is being created.
     * It initializes the ExoPlayer and MediaSession instances and sets the MediaSessionServiceListener.
     */
    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate() // Call the superclass method

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs= */ 10000,          // Reduced from 15000
                /* maxBufferMs= */ 20000,          // Reduced from 30000
                /* bufferForPlaybackMs= */ 2000,   // Reduced from 2500
                /* bufferForPlaybackAfterRebufferMs= */ 4000  // Reduced from 5000
            )
            .setTargetBufferBytes(DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES / 2) // Reduce target buffer size
            .setPrioritizeTimeOverSizeThresholds(true)
            .setBackBuffer(
                /* backBufferDurationMs= */ 10000,
                /* retainBackBufferFromKeyframe= */ true
            )
            .build()

        // Create an ExoPlayer instance
        val player = ExoPlayer.Builder(this).setLoadControl(loadControl)
                    .build()

        // Create a MediaSession instance
        _mediaSession = MediaSession.Builder(this, player)
            .also { builder ->
                // Set the session activity to the PendingIntent returned by getSingleTopActivity() if it's not null
                getSingleTopActivity()?.let { builder.setSessionActivity(it) }
            }
            .build() // Build the MediaSession instance

        // Set the listener for the MediaSessionService
        setListener(MediaSessionServiceListener())
    }

    /**
     * This method is called when the system determines that the service is no longer used and is being removed.
     * It checks the player's state and if the player is not ready to play or there are no items in the media queue, it stops the service.
     *
     * @param rootIntent The original root Intent that was used to launch the task that is being removed.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Get the player from the media session
        val player = mediaSession.player

        // Check if the player is not ready to play or there are no items in the media queue
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service
            stopSelf()
        }
    }

    /**
     * This method is called when a MediaSession.ControllerInfo requests the MediaSession.
     * It returns the current MediaSession instance.
     *
     * @param controllerInfo The MediaSession.ControllerInfo that is requesting the MediaSession.
     * @return The current MediaSession instance.
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return _mediaSession
    }

    /**
     * This method is called when the service is being destroyed.
     * It releases the player and the MediaSession instances, sets the _mediaSession to null, clears the listener, and calls the superclass method.
     */
    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        // If _mediaSession is not null, run the following block
        _mediaSession?.run {
            // If getBackStackedActivity() returns a non-null value, set it as the session activity
            getBackStackedActivity()?.let { setSessionActivity(it) }
            // Release the player
            player.release()
            // Release the MediaSession instance
            release()
            // Set _mediaSession to null
            _mediaSession = null
        }
        // Clear the listener
        clearListener()
        // Call the superclass method
        super.onDestroy()
    }

    /**
     * This method creates a PendingIntent that starts the MainActivity.
     * The PendingIntent is created with the "FLAG_UPDATE_CURRENT" flag, which means that if the described PendingIntent already exists,
     * then keep it but replace its extra data with what is in this new Intent.
     * The PendingIntent also has the "FLAG_IMMUTABLE" flag if the Android version is 23 or above, which means that the created PendingIntent
     * is immutable and cannot be changed after it's created.
     *
     * @return A PendingIntent that starts the MainActivity. If the PendingIntent cannot be created for any reason, it returns null.
     */
    private fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    /**
     * This method creates a PendingIntent that starts the MainActivity with a back stack.
     * The PendingIntent is created with the "FLAG_UPDATE_CURRENT" flag, which means that if the described PendingIntent already exists,
     * then keep it but replace its extra data with what is in this new Intent.
     * The PendingIntent also has the "FLAG_IMMUTABLE" flag if the Android version is 23 or above, which means that the created PendingIntent
     * is immutable and cannot be changed after it's created.
     * The back stack is created using TaskStackBuilder, which allows the user to navigate back to the MainActivity from the PendingIntent.
     *
     * @return A PendingIntent that starts the MainActivity with a back stack. If the PendingIntent cannot be created for any reason, it returns null.
     */
    private fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@MediaService, MainActivity::class.java))
            getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @OptIn(UnstableApi::class) // MediaSessionService.Listener
    private inner class MediaSessionServiceListener : Listener {

        /**
         * This method is only required to be implemented on Android 12 or above when an attempt is made
         * by a media controller to resume playback when the {@link MediaSessionService} is in the
         * background.
         */
        override fun onForegroundServiceStartNotAllowedException() {
            if (
                Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                println("tyrtyerte")
                // Notification permission is required but not granted
                return
            }
            val notificationManagerCompat = NotificationManagerCompat.from(this@MediaService)
            ensureNotificationChannel(notificationManagerCompat)
            val builder =
                NotificationCompat.Builder(this@MediaService, CHANNEL_ID)
                    .setSmallIcon(androidx.media3.session.R.drawable.media3_notification_small_icon)
                    .setContentTitle("Manager")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .also { builder -> getBackStackedActivity()?.let { builder.setContentIntent(it) } }
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if (
            Build.VERSION.SDK_INT < 26 ||
            notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null
        ) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Manager",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }
}
