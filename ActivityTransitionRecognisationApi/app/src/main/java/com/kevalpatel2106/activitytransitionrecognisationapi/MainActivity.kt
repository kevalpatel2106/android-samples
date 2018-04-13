package com.kevalpatel2106.activitytransitionrecognisationapi

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity


class MainActivity : AppCompatActivity() {

    private var pendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pendingIntent = preparePendingIntent()
        register()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregister()
    }

    private fun preparePendingIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
                this@MainActivity,
                1232,
                Intent(this@MainActivity, ActivityTransitionReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun register() {
        val request = ActivityTransitionRequest(prepareTransitionsList())
        val task = ActivityRecognition.getClient(this@MainActivity)
                .requestActivityTransitionUpdates(request, pendingIntent)

        task.addOnSuccessListener {
            // Handle success
        }

        task.addOnFailureListener {
            // Handle error
        }
    }

    private fun unregister() {
        pendingIntent?.let {
            val task = ActivityRecognition.getClient(this@MainActivity)
                    .removeActivityTransitionUpdates(it)
            task.addOnSuccessListener { pendingIntent?.cancel() }
            task.addOnFailureListener { e -> Log.e("MYCOMPONENT", e.message) }
        }
    }

    private fun prepareTransitionsList(): ArrayList<ActivityTransition> {
        val transitions = ArrayList<ActivityTransition>()
        transitions.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build())
        transitions.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
        transitions.add(ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build())
        return transitions
    }
}
