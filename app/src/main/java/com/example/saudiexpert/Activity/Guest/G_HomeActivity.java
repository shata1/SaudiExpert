package com.example.saudiexpert.Activity.Guest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Fragment.Guest.G_HomeFragment;
import com.example.saudiexpert.Fragment.Guest.G_TourFragment;
import com.example.saudiexpert.Fragment.Guest.G_UserFragment;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityGhomeBinding;

public class G_HomeActivity extends AppCompatActivity {

    ActivityGhomeBinding binding;

    G_HomeFragment GHomeFragment = new G_HomeFragment();
    G_TourFragment GTourFragment = new G_TourFragment();
    G_UserFragment GUserFragment = new G_UserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGhomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Home();

        binding
                .bottomNavigation
                .setOnItemSelectedListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.home:
                            Home();
                            return true;
                        case R.id.tour:
                            Tour();
                            return true;
                        case R.id.user:
                            User();
                            return true;
                    }
                    return false;
                });
    }

    void Home() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, GHomeFragment)
                .commit();
    }

    void Tour() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, GTourFragment)
                .commit();
    }

    void User() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, GUserFragment)
                .commit();
    }
}