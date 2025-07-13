package com.example.steptrackerwithlocaldatabase

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/** Object to manage the user's step sensor permission **/
object PermissionManager {
    private const val TAG = "PermissionManager"
    private const val REQUEST_CODE = 1001

    /** Checks if the app has permission to use the step sensor **/
    fun permissionAvailable(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /** Will show a dialog requesting permission to use the step sensor **/
    @MainThread
    fun requestUserPermission(activity: Activity?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "Permission requested")
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    REQUEST_CODE
                )
            }
        } else {
            Log.d(TAG, "No permission required due to SDK version")
        }
    }
}