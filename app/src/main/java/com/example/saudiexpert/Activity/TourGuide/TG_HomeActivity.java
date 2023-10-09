package com.example.saudiexpert.Activity.TourGuide;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Fragment.TourGuide.TG_HomeFragment;
import com.example.saudiexpert.Fragment.TourGuide.TG_TourFragment;
import com.example.saudiexpert.Fragment.TourGuide.TG_UserFragment;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTgHomeBinding;

public class TG_HomeActivity extends AppCompatActivity {

    ActivityTgHomeBinding binding;

    TG_HomeFragment TG_HomeFragment = new TG_HomeFragment();
    TG_TourFragment TG_TourFragment = new TG_TourFragment();
    TG_UserFragment TG_UserFragment = new TG_UserFragment();


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgHomeBinding.inflate(getLayoutInflater());
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
                .replace(R.id.container, TG_HomeFragment)
                .commit();
    }

    void Tour() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TG_TourFragment)
                .commit();
    }

    void User() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TG_UserFragment)
                .commit();
    }


}