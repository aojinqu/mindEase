package com.mindease.feature.main;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.mindease.R;
import com.mindease.common.ui.WindowInsetsHelper;
import com.mindease.feature.agent.AgentChatActivity;
import com.mindease.feature.analysis.AnalysisFragment;
import com.mindease.feature.community.CommunityFragment;
import com.mindease.feature.home.HomeFragment;
import com.mindease.feature.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowInsetsHelper.enableEdgeToEdge(this);
        setContentView(R.layout.activity_main);

        View navHostContainer = findViewById(R.id.nav_host_container);
        NavigationBarView bottomNav = findViewById(R.id.bottom_nav);
        WindowInsetsHelper.applyTopPadding(navHostContainer);
        WindowInsetsHelper.applyBottomPadding(bottomNav);

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
            } else if (itemId == R.id.nav_chat) {
                startActivity(new Intent(this, AgentChatActivity.class));
                return false;
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
