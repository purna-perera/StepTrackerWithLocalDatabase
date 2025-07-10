package com.example.steptrackerwithlocaldatabase

import android.content.Context

fun getPrefs(context: Context) = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)