package com.example.mahmud.travelmate;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Test extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
