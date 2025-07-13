package com.example.steptrackerwithlocaldatabase

import android.content.Context
import android.content.SharedPreferences

/** Returns the device's shared preferences, the means of persistent storage for this app **/
fun getPrefs(context: Context): SharedPreferences =
    context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)