package com.apni.pos

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance("https://com-apni-pos-default-rtdb.firebaseio.com/").setPersistenceEnabled(true)
    }
}