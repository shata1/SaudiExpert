package com.example.saudiexpert.Activity.Tourist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Fragment.Tourist.T_HomeFragment;
import com.example.saudiexpert.Fragment.Tourist.T_TourFragment;
import com.example.saudiexpert.Fragment.Tourist.T_UserFragment;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTHomeBinding;

public class T_HomeActivity extends AppCompatActivity {

    ActivityTHomeBinding binding;

    T_HomeFragment THomeFragment = new T_HomeFragment();
    T_TourFragment TTourFragment = new T_TourFragment();
    T_UserFragment TUserFragment = new T_UserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTHomeBinding.inflate(getLayoutInflater());
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
                .replace(R.id.container, THomeFragment)
                .commit();
    }

    void Tour() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TTourFragment)
                .commit();
    }

    void User() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, TUserFragment)
                .commit();
    }
}