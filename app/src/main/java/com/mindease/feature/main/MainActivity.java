package com.mindease.feature.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.mindease.R;
import com.mindease.feature.analysis.AnalysisFragment;
import com.mindease.feature.calendar.CalendarFragment;
import com.mindease.feature.community.CommunityFragment;
import com.mindease.feature.home.HomeFragment;
import com.mindease.feature.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationBarView bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            switchFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_analysis) {
                switchFragment(new AnalysisFragment());
                return true;
            } else if (itemId == R.id.nav_calendar) {
                switchFragment(new CalendarFragment());
                return true;
            } else if (itemId == R.id.nav_community) {
                switchFragment(new CommunityFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_container, fragment)
                .commit();
    }
}
