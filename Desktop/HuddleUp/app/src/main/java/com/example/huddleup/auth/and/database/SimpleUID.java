package com.example.huddleup.auth.and.database;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.huddleup.PlannedEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import OtherClasses.Event;

class SimpleUID {
    static final String[] uid = new String[1];

//    public static void makeEventUid(PlannedEvent e) {
//        uid[0] = e.getTime() + e.getName();
//        Database.finishCreateEvent(uid[0]);
//    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    static void test() {
        Calendar c = new Calendar.Builder().build();
        System.out.println("-------------------[simple uid test]->"+c.getTimeInMillis());
    }

    //////////////
    // I should be able to implement something similar for the teams

}
