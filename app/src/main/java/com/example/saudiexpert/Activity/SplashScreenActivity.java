package com.example.saudiexpert.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.saudiexpert.Activity.TourGuide.TG_HomeActivity;
import com.example.saudiexpert.Activity.Tourist.T_HomeActivity;
import com.example.saudiexpert.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private final static int SPLASH_DISPLAY_LENGTH = 1000; //change time
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Register-Activity. */
            if (firebaseUser != null) {
                if (isTourGuide()) {
                    startActivity(new Intent(SplashScreenActivity.this, TG_HomeActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, T_HomeActivity.class));
                }
            } else {
                startActivity(new Intent(SplashScreenActivity.this, RegisterActivity.class));
            }
            ActivityCompat.finishAffinity(this);
        }, SPLASH_DISPLAY_LENGTH);
    }

    public boolean isTourGuide() {
        SharedPreferences sp = getSharedPreferences("UserType", Activity.MODE_PRIVATE);
        return sp.getBoolean("TourGuide", false);
    }

}