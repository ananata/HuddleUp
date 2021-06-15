package com.example.huddleup.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.example.huddleup.ImageAdapter;
import com.example.huddleup.ImageModel;
import com.example.huddleup.R;
import com.example.huddleup.TeamCreateScreen;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import OtherClasses.VisualUtilities;

/**
 * Create an instance of this fragment.
 */
public class SelectSportFragment extends Fragment {

    // Declare instance variables
    private final int[] ICON_IMAGES = {R.mipmap.ic_archery,
            R.mipmap.ic_badminton,
            R.mipmap.ic_baseball,
            R.mipmap.ic_basketball,
            R.mipmap.ic_bowling,
            R.mipmap.ic_boxing,
            R.mipmap.ic_cycling,
            R.mipmap.ic_fencing,
            R.mipmap.ic_football,
            R.mipmap.ic_golf,
            R.mipmap.ic_hockey,
            R.mipmap.ic_ice_skating,
            R.mipmap.ic_karate,
            R.mipmap.ic_ping_pong,
            R.mipmap.ic_pool,
            R.mipmap.ic_rings,
            R.mipmap.ic_rowing,
            R.mipmap.ic_rythmic_gymnastics,
            R.mipmap.ic_sailing,
            R.mipmap.ic_shooting,
            R.mipmap.ic_skiing,
            R.mipmap.ic_soccer,
            R.mipmap.ic_tennis,
            R.mipmap.ic_volleyball,
            R.mipmap.ic_weightlifting,
            R.mipmap.ic_sport_other};

    GridView gridView;
    ArrayList<ImageModel> arrayList;

    private VisualUtilities vu = new VisualUtilities();
    private int selectedPosition;
    FloatingActionButton addTeam;

    public SelectSportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * Create send message interface
     * Allows information to be sent between fragments
     */
    //interface SendMessage {
     //   void sendData(String message);
    //}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_sport, container, false);
    }

    /**
     * Declare onActivityCreated method
     * @param savedInstanceState
     *
     * Get user selection
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onCreate(savedInstanceState);

        GridView gridView = (GridView) Objects.requireNonNull(getActivity()).findViewById(R.id.GridViewSportSelect);
        arrayList = new ArrayList<>();
        for (int value : ICON_IMAGES) {
            ImageModel imagemodel = new ImageModel();
            imagemodel.setThumbIds(value);

            Log.d("SelectSportFragment", imagemodel + "");

            // Add in array list
            arrayList.add(imagemodel);
        }

        // Create array of transparent colors
        final String[] colors = new String[ICON_IMAGES.length];
        Arrays.fill(colors, "#00FFFFFF");

        // Instantiate new ImageAdapter
        final ImageAdapter adapter= new ImageAdapter(getActivity(), arrayList, colors);
        gridView.setAdapter(adapter);

        // Set-up item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                // Highlight selected element
                adapter.setGridColor(position, "#669FB2D5");
                adapter.notifyDataSetChanged();

                // Save final id selected
                selectedPosition = ICON_IMAGES[position];
                //selectMyIcon(view, position);
            }
        });

        // Add action to "Save Changes" Button
        Button nextButton = (Button) getActivity().findViewById(R.id.ButtonSaveSportsIconSelection);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                // Print message together
                vu.popToast("Selection has been saved", (Activity)view.getContext());

                // Set Sport edit text on next fragment based on image selected
                // TODO: 04/08/2020 <-- Figure out a way to pass icon id to next fragment
               // String selectedSport = getSportMessage(selectedPosition);
               // EditText sport = (EditText) view.findViewById(R.id.EditTextTeamSport);
                // sport.setAutofillHints(selectedSport);
                //sport.setText(selectedSport);

                //Bundle args = new Bundle();
                //args.putInt("icon", (int)selectedPosition);
                //CreateTeamFragment nextFragment = new CreateTeamFragment();
                //nextFragment.setArguments(args);
                // Set global variable
                TeamCreateScreen.selectedSportImage = selectedPosition;

                // Start fragment transaction (moves to user form)
                ((TeamCreateScreen) Objects.requireNonNull(getActivity())).setCurrentItem (1, true);


            }
        });

    }

    private String getSportMessage(int id) {
        switch(id) {
            case R.mipmap.ic_archery:
                return "Archery";
            case R.mipmap.ic_badminton:
                return "Badminton";
            case R.mipmap.ic_baseball:
                return "Baseball";
            case R.mipmap.ic_basketball:
                return "Basketball";
            case R.mipmap.ic_karate:
                return "Karate";
            case R.mipmap.ic_fencing:
                return "Fencing";
            case R.mipmap.ic_cycling:
                return "Cycling";
            case R.mipmap.ic_bowling:
                return "Bowling";
            case R.mipmap.ic_boxing:
                return "Boxing";
            case R.mipmap.ic_rythmic_gymnastics:
                return "Rhythmic Gymnastics";
            case R.mipmap.ic_hockey:
                return "Hockey";
            case R.mipmap.ic_football:
                return "Football";
            case R.mipmap.ic_golf:
                return "Golf";
            case R.mipmap.ic_ping_pong:
                return "Ping-Pong";
            case R.mipmap.ic_pool:
                return "Pool";
            case R.mipmap.ic_rings:
                return "Ring Gymnastics";
            case R.mipmap.ic_sailing:
                return "Sailing";
            case R.mipmap.ic_shooting:
                return "Sharp-shooting";
            case R.mipmap.ic_skiing:
                return "Skiing";
            case R.mipmap.ic_soccer:
                return "Soccer";
            case R.mipmap.ic_tennis:
                return "Tennis";
            case R.mipmap.ic_volleyball:
                return "Volleyball";
            case R.mipmap.ic_rowing:
                return "Rowing";
            case R.mipmap.ic_weightlifting:
                return "Weight-lifting";
            case R.mipmap.ic_ice_skating:
                return "Ice Skating";
            case R.mipmap.ic_sport_other:
                return "Other";
        }

        // Return default
        return "Archery";
    }

}
