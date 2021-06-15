package com.example.huddleup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huddleup.auth.and.database.Auth;
import com.example.huddleup.auth.and.database.User;

import java.util.ArrayList;
import java.util.Arrays;

import OtherClasses.VisualUtilities;

public class ProfileIconSelectScreen extends AppCompatActivity {
    // Declare instance variables
    final int[] ICON_IMAGES = {R.mipmap.avatar01,
            R.mipmap.avatar02,
            R.mipmap.avatar03,
            R.mipmap.avatar04,
            R.mipmap.avatar05,
            R.mipmap.avatar06,
            R.mipmap.avatar07,
            R.mipmap.avatar08,
            R.mipmap.avatar09};
    GridView gridView;
    ArrayList<ImageModel> arrayList;
    VisualUtilities vu = new VisualUtilities();
    Button saveChanges;
    int selectedPosition;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_icon_select_screen);
        gridView = (GridView) findViewById(R.id.GridViewProfileIconSelect);
        arrayList = new ArrayList<>();
        for (int value : ICON_IMAGES) {
            ImageModel imagemodel = new ImageModel();
            imagemodel.setThumbIds(value);

            // Add in array list
            arrayList.add(imagemodel);
        }

        // Create array of transparent colors
        final String[] colors = new String[9];
        Arrays.fill(colors, "#00FFFFFF");

        // Instantiate new ImageAdapter
        final ImageAdapter adapter= new ImageAdapter(getApplicationContext(), arrayList, colors);
        gridView.setAdapter(adapter);

        // Set-up item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                // Highlight selected element
                adapter.setGridColor(position, "#669FB2D5");
                adapter.notifyDataSetChanged();

                // Save final position selected
                selectedPosition = position;
            }
        });

        // Add action to "Save Changes" Button
        saveChanges = (Button) findViewById(R.id.ButtonSaveChangesToIcon);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeIconToDatabase(view, selectedPosition);

                // Add a "Save Changes Button" that activates new intent and return Homescreen
                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(intent);
            }
        });

    }

    private void writeIconToDatabase(View v, int position) {
        // Get uid
        // if a user is logged in, their user account is held in Auth.instance.user <--
        // it is different from FirebaseUser (it is a custom object for our app needs)
        // use this
        // TODO: Update 04/16 --> I get a null pointer exception using this method :( (reverted to the old version for testing purposes)
        // Exact error: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.example.huddleup.auth.and.database.User.getUid()' on a null object reference
        // final String currentUserUid = Auth.instance.user.getUid();
        final String currentUserUid = Auth.instance.firebaseAuth.getCurrentUser().getUid().trim();

        // Create new instance of User (let me know if there is a better way to do this)
        // set user to the User instance in the Auth instance
        //User user = Auth.instance.user;
        User user = new User();

        // Set uid
        user.setUid(currentUserUid);

        // Write profile icon selection to database
        int icon = ICON_IMAGES[position];
        if ((icon + "").length() != 0) {
            // Print message
            vu.popToast( "Changes have been saved", (Activity)v.getContext());
            user.setUserProfileIconIndex(icon);
        }
        else {
            // Print message
            vu.popToast("Selection could not be saved",(Activity) v.getContext());
            Log.d("ProfileIconSelectScreen", "Could not read selection");
        }
    }

    /**
     * Intents need to be modified as more screens are implemented
     * Updates 03/30/2020 - The program can now run without crashing.
     * @param view View
     * @param position int
     */
    private void selectMyIcon(View view, int position) {
        switch (position) {
            case 0:
                vu.popToast("Selected icon at position " + position, this);
            case 1:
                vu.popToast("Selected icon at position " + position, this);
            case 2:
                vu.popToast("Selected icon at position " + position, this);
            case 3:
                vu.popToast("Selected icon at position " + position, this);
            case 4:
                vu.popToast("Selected icon at position " + position, this);
            case 5:
                vu.popToast("Selected icon at position " + position, this);
            case 6:
                vu.popToast("Selected icon at position " + position, this);
            case 7:
                vu.popToast("Selected icon at position " + position, this);
            case 8:
                vu.popToast("Selected icon at position " + position, this);
            case 9:
                vu.popToast("Selected icon at position " + position, this);
        }
    }
}
