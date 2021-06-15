package com.example.huddleup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.widget.TableLayout;

import com.example.huddleup.fragments.CreateTeamFragment;
import com.example.huddleup.fragments.SelectSportFragment;
import com.google.android.material.tabs.TabLayout;

import OtherClasses.AuthenticationPagerAdapter;

public class TeamCreateScreen extends AppCompatActivity {
    public static int selectedSportImage = 0;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_create_screen);

        // Mount fragments
        showTeamCreateFragments();
    }

    // Navigating between fragments
    // Adapted from Mikalai Daronin at
    // https://stackoverflow.com/questions/20264586/swiping-to-the-next-fragment-with-a-button-click/20264892
    public void setCurrentItem (int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

    // Get fragment classes
    private void showTeamCreateFragments() {
        setContentView(R.layout.activity_team_create_screen);
        viewPager = findViewById(R.id.ViewPagerCreateTeam);

        AuthenticationPagerAdapter pagerAdapter = new AuthenticationPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new SelectSportFragment());
        pagerAdapter.addFragment(new CreateTeamFragment());
        viewPager.setAdapter(pagerAdapter);
    }
}
