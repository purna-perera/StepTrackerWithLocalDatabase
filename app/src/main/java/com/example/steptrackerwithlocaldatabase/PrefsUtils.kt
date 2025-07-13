package com.example.steptrackerwithlocaldatabase

import android.content.Context
import android.content.SharedPreferences

fun getPrefs(context: Context): SharedPreferences =
    context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)