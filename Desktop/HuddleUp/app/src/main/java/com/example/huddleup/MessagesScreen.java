package com.example.huddleup;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.Database;
import com.example.huddleup.auth.and.database.Message;
import com.example.huddleup.auth.and.database.Team;
import com.example.huddleup.auth.and.database.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import OtherClasses.MessageAdapter;
import OtherClasses.PlayerAdapter;
import OtherClasses.TeamAdapter;
import OtherClasses.VisualUtilities;

public class MessagesScreen extends AppCompatActivity {
    DatabaseReference teamDatabaseReference;
    ProgressDialog progressDialog;
    RecyclerView messageRecyclerView;
    MessageAdapter adapter;
    List<Message> finalMessageList = new ArrayList<>();
    VisualUtilities vu = new VisualUtilities();
    final String TAG = "MessagesScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_screen);

        // Lookup the recyclerview in activity layout
        messageRecyclerView = (RecyclerView) findViewById(R.id.RecyclerViewMessagesScreen);
        messageRecyclerView.addItemDecoration(new DividerItemDecoration(MessagesScreen.this, LinearLayoutManager.VERTICAL));
        messageRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext(), RecyclerView.VERTICAL, false);
        messageRecyclerView.setLayoutManager(layoutManager);

        // Get database stuff
        final String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference currentUserNode = Database.getFirebaseDatabase().child("users").child(currentUserUid);

        // Check data
        if (currentUserUid != null) {
            Log.d("MessagesScreen", "This is the current user's uid" + currentUserUid);
            // getAllTeams(currentUserUid, TeamSelectScreen.this, teamRecyclerView);
        } else {
            Log.d("MessagesScreen", "Unable to get current uid");
        }

        ValueEventListener messageValueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();
                Log.d("MessagesScreen", "There are " + messages.size());

                if (dataSnapshot.getChildrenCount() == 0) {
                    vu.popToast("No messages at the moment", MessagesScreen.this);
                }
                else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot != null && !snapshot.getKey().equals("numUpdates")) {
                            // Load message from database into message object
                            Message currentMessage = new Message();

                            String subject = null;
                            if (snapshot.hasChild("subject")) {
                                // Get subject of message
                                subject = snapshot.child("subject").getValue(String.class);
                                currentMessage.setSubject(subject);
                                Log.d(TAG, "message subject " + subject);
                            }
                            Log.d(TAG, "message subject " + subject);

                            String message = null;
                            if (snapshot.hasChild("message")) {
                                message = snapshot.child("message").getValue(String.class);
                                Log.d(TAG, "message body " + message);
                                currentMessage.setMessage(message);
                            }
                            Log.d(TAG, "message body " + message);

                            String dateSent = null;
                            if (snapshot.hasChild("dateSent")) {
                                dateSent = snapshot.child("dateSent").getValue(String.class);
                                Log.d(TAG, "message date " + dateSent);
                                currentMessage.setDateSent(dateSent);
                            }
                            Log.d(TAG, "message date " + dateSent);

                            long timeSent = 0;
                            if (snapshot.hasChild("timeSent")) {
                                timeSent = snapshot.child("timeSent").getValue(long.class);
                                Log.d(TAG, "message time " + timeSent);
                                currentMessage.setDateTimeSent(timeSent);
                            }
                            Log.d(TAG, "message time " + timeSent);


                            // Add message object to array list
                            messages.add(currentMessage);
                        }
                        else {
                            Log.d("MessagesScreen", "Unable to get valid snapshot");
                        }
                    }
                }

                if (messages.size() != 0) {
                    // Pass data to adapter
                    Log.d(TAG, "message adapter " + messages.size());
                    finalMessageList = messages;
                    adapter = new MessageAdapter(MessagesScreen.this,finalMessageList);
                    messageRecyclerView.setAdapter(adapter);
                }
                else {
                    vu.popToast("No messages sent yet", MessagesScreen.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException(); // Don't ignore errors
            }
        };

        // Mount player information in recyclerview object
        final DatabaseReference teamNode = Database.getUsers().child(currentUserUid).child("notifications");
        teamNode.addValueEventListener(messageValueEventListener);

    }

    // TODO: Load all notifications from database and mount to recyclerview


}
